#!/bin/bash
THIS_IP=$1
mkdir /temp/g
cd /temp/g
ps aux | grep mongo | awk '{print $2}' | xargs kill -9
rm -rf *
wget -O mongoDB.tgz https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-rhel70-4.0.3.tgz
mkdir mongoDB && tar zxvf mongoDB.tgz -C mongoDB --strip-components 1
mkdir data
mkdir data/db
mkdir data/conf
sed -i '/mongoDB/d' ~/.profile
echo "export PATH=\$PATH:/temp/g/mongoDB/bin" >> ~/.profile
cd mongoDB/bin