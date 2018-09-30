#!/bin/bash
VERSION=3.11.3
CLUSTER_NAME=$1
SEEDS=$2
THIS_IP=$3
echo "${CLUSTER_NAME} $THIS_IP"
ps aux | grep cassandra | awk '{print $2}' | xargs kill -9
cd /temp
rm -r apache-cassandra-${VERSION}*
wget http://www-eu.apache.org/dist/cassandra/${VERSION}/apache-cassandra-${VERSION}-bin.tar.gz
tar -zxvf apache-cassandra-${VERSION}-bin.tar.gz 
rm pache-cassandra-${VERSION}-bin.tar.gz 
sed -i '/apache-cassandra/d' ~/.profile
echo "export PATH=\$PATH:/temp/apache-cassandra-${VERSION}/bin" >> ~/.profile
cd /temp/apache-cassandra-${VERSION}/conf
sed -i -e "s/cluster_name:.*$/cluster_name: '${CLUSTER_NAME}'/" cassandra.yaml
sed -i -e "s/seeds:.*$/seeds: \"${SEEDS}\"/" cassandra.yaml
sed -i -e "s/listen_address:.*$/listen_address: ${THIS_IP}/" cassandra.yaml
sed -i -e "s/rpc_address:.*$/rpc_address: ${THIS_IP}/" cassandra.yaml
../bin/cassandra