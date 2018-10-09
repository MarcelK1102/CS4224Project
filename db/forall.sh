#!/bin/bash
trap control_c SIGINT
MASTER=192.168.48.249
SEEDS=${MASTER},192.168.48.250,192.168.48.251,192.168.48.252,192.168.48.253
CLUSTER_NAME=cs4224g
ssh-keygen -t rsa
ssh ${CLUSTER_NAME}@${MASTER} mkdir -p .ssh #DO THIS IF NOT DONE
cat ~/.ssh/id_rsa.pub | ssh ${CLUSTER_NAME}@${MASTER} 'cat >> ~/.ssh/authorized_keys'
for i in $(echo $SEEDS | sed "s/,/ /g")
do
    ssh $CLUSTER_NAME@$i "bash -s" < ./install_cassandra.sh $CLUSTER_NAME $SEEDS $i &
done

wait
echo "DONE INSTALLING"

scp *.cql ${CLUSTER_NAME}@${MASTER}:~/
ssh $CLUSTER_NAME@$MASTER "bash -s" < ./initdb.sh