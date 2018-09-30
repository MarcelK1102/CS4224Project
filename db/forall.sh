#!/bin/bash
MASTER=192.168.48.249
SEEDS=${MASTER},192.168.48.250,192.168.48.251,192.168.48.252,192.168.48.253
CLUSTER_NAME=cs4224g
for i in $(echo $SEEDS | sed "s/,/ /g")
do
    sshpass -p "!C45zV9D" ssh $CLUSTER_NAME@$i "bash -s" < ./install_cassandra.sh $CLUSTER_NAME $SEEDS $i &
done

wait

sshpass -p "!C45zV9D" ssh $CLUSTER_NAME@$MASTER "bash -s" < ./initdb.sh