#!/bin/bash
VERSION=3.11.3
MASTER=192.168.48.249
SEEDS=${MASTER},192.168.48.250,192.168.48.251,192.168.48.252,192.168.48.253
CLUSTER_NAME=cs4224g
PROJECT_PATH=..

#Add ssh key for shared drive as to avoid giving password in the future
ssh-keygen -t rsa
ssh ${CLUSTER_NAME}@${MASTER} mkdir -p .ssh #DO THIS IF NOT DONE
cat ~/.ssh/id_rsa.pub | ssh ${CLUSTER_NAME}@${MASTER} 'cat >> ~/.ssh/authorized_keys'

#Execute install script
for i in $(echo $SEEDS | sed "s/,/ /g")
do
    ssh $CLUSTER_NAME@$i "bash -s" < ./install.sh $CLUSTER_NAME $SEEDS $i $VERSION &
done

wait
echo "DONE INSTALLING"

scp $PROJECT_PATH/db/*.cql ${CLUSTER_NAME}@${MASTER}:~/
ssh $CLUSTER_NAME@$MASTER "bash -s" < ./initdb.sh $VERSION $MASTER
echo "DONE DATABASE SETUP"