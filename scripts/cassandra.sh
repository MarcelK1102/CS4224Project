#!/bin/bash

# Add the seed nodes here

SEEDS[0]='192.168.48.249'
SEEDS[1]='192.168.48.251'
SEEDS[2]='192.168.48.253'

containsElement () {
  local e
  for e in "${@:2}"; do [[ "$e" == "$1" ]] && return 0; done
  return 1
}

function join { local IFS="$1"; shift; echo "$*"; }

echo 'Downloading cassandra...'

curl -L http://www-us.apache.org/dist/cassandra/3.11.0/apache-cassandra-3.11.0-bin.tar.gz | tar xvz

echo 'Finish downloading and unpacking'

IP_ADDR=`/sbin/ifconfig enp2s0f0 | grep 'inet ' | cut -d: -f2 | awk '{ print $2}'`

echo "Your IP: ${IP_ADDR}"

echo 'Modifying cassandra.yaml...'

sed -i "s/cluster_name: 'Test Cluster'/cluster_name: 'Team 7'/g" apache-cassandra-3.11.0/conf/cassandra.yaml
sed -i 's/endpoint_snitch: SimpleSnitch/endpoint_snitch: GossipingPropertyFileSnitch/g' apache-cassandra-3.11.0/conf/cassandra.yaml
ALL_SEEDS=`join , ${SEEDS[@]}`
sed -i 's/- seeds: "127.0.0.1"/- seeds: "'${ALL_SEEDS[@]}'"/g' apache-cassandra-3.11.0/conf/cassandra.yaml

if [[ ${SEEDS[*]} =~ $IP_ADDR ]]; then
  # settings for seed node
  sed -i 's/listen_address: localhost/listen_address: '${IP_ADDR}'/g' apache-cassandra-3.11.0/conf/cassandra.yaml
  sed -i 's/rpc_address: localhost/rpc_address: '${IP_ADDR}'/g' apache-cassandra-3.11.0/conf/cassandra.yaml
  sh -c "echo 'auto_bootstrap: false' >> apache-cassandra-3.11.0/conf/cassandra.yaml "
else
  # settings for other nodes
  sed -i 's/listen_address: localhost/listen_address: '${IP_ADDR}'/g' apache-cassandra-3.11.0/conf/cassandra.yaml
  sed -i 's/rpc_address: localhost/rpc_address: '${IP_ADDR}'/g' apache-cassandra-3.11.0/conf/cassandra.yaml
fi

sed -i 's/read_request_timeout_in_ms: 5000/read_request_timeout_in_ms: 60000/g' apache-cassandra-3.11.0/conf/cassandra.yaml

echo 'Complete'