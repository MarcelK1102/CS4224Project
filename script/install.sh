#!/bin/bash
THIS_IP=$1
rm -rf /temp/g
mkdir /temp/g
cd /temp/g
ps aux | grep mongo | awk '{print $2}' | xargs kill -9
ps aux | grep app.jar | awk '{print $2}' | xargs kill -9
wget -O mongoDB.tgz https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-rhel70-4.1.4.tgz
mkdir mongoDB && tar zxvf mongoDB.tgz -C mongoDB --strip-components 1
mkdir data
mkdir data/db
mkdir data/conf
sed -i '/mongoDB/d' ~/.profile
echo "export PATH=\$PATH:/temp/g/mongoDB/bin" >> ~/.profile