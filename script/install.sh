#!/bin/bash
CLUSTER_NAME=$1
SEEDS=$2
THIS_IP=$3
mkdir /temp/g
cd /temp/g
ps aux | grep mongod | awk '{print $2}' | xargs kill -9
rm -rf *
wget -O mongoDB.tgz https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-rhel70-4.0.3.tgz
mkdir mongoDB && tar zxvf mongoDB.tgz -C mongoDB --strip-components 1
mkdir data
mkdir data/db
sed -i '/mongoDB/d' ~/.profile
echo "export PATH=\$PATH:/temp/g/mongoDB/bin" >> ~/.profile
cd mongoDB/bin
./mongod --dbpath /temp/g/data/db --bind_ip $THIS_IP