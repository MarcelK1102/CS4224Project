#!/bin/bash
CLUSTER_NAME=$1
SEEDS=$2
THIS_IP=$3
CASSANDRA=apache-cassandra-$4
ps aux | grep cassandra | awk '{print $2}' | xargs kill -9
cd /temp
rm -r $CASSANDRA*
wget http://www-eu.apache.org/dist/cassandra/${VERSION}/$CASSANDRA-bin.tar.gz
tar -zxvf $CASSANDRA-bin.tar.gz 
rm $CASSANDRA-bin.tar.gz 
sed -i '/apache-cassandra-/d' ~/.profile
echo "export PATH=\$PATH:/temp/$CASSANDRA/bin" >> ~/.profile
cd /temp/$CASSANDRA/conf
sed -i -e "s/cluster_name:.*$/cluster_name: '${CLUSTER_NAME}'/" cassandra.yaml
sed -i -e "s/seeds:.*$/seeds: \"${SEEDS}\"/" cassandra.yaml
sed -i -e "s/listen_address:.*$/listen_address: ${THIS_IP}/" cassandra.yaml
sed -i -e "s/rpc_address:.*$/rpc_address: ${THIS_IP}/" cassandra.yaml
sed -i -e "s/rpc_address:.*$/rpc_address: ${THIS_IP}/" cassandra.yaml
sed -i -e "s/request_timeout_in_ms:.*$/request_timeout_in_ms: 600000/" cassandra.yaml
../bin/cassandra