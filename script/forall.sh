#!/bin/bash
MASTER=192.168.48.253
SHARDS=192.168.48.249,192.168.48.250,192.168.48.251,192.168.48.252,192.168.48.253
CONFIGS=192.168.48.251,192.168.48.252,192.168.48.253
CLUSTER_NAME=cs4224g
PROJECT_PATH=..

#Add ssh key for shared drive as to avoid giving password in the future
# ssh-keygen -t rsa
# ssh ${CLUSTER_NAME}@${MASTER} mkdir -p .ssh #DO THIS IF NOT DONE
# cat ~/.ssh/id_rsa.pub | ssh ${CLUSTER_NAME}@${MASTER} 'cat >> ~/.ssh/authorized_keys'

#Execute install script
for i in $(echo $SHARDS | sed "s/,/ /g")
do
    ssh $CLUSTER_NAME@$i "bash -s" < ./install.sh $i &
done

wait
echo "DONE INSTALLING"

for i in $(echo $CONFIGS | sed "s/,/ /g")
do
    ssh $CLUSTER_NAME@$i "/temp/g/mongoDB/bin/mongod --configsvr --dbpath /temp/g/data/conf --bind_ip $i --replSet conf &" & #Run on 3 server
done

for i in $(echo $SHARDS | sed "s/,/ /g")
do
    ssh $CLUSTER_NAME@$i "/temp/g/mongoDB/bin/mongod --shardsvr --replSet warehouse --dbpath /temp/g/data/db/ --bind_ip $i &" &
done

mongo 192.168.48.253:27019 --eval 'rs.initiate(
  {
    _id: "conf",
    configsvr: true,
    members: [
      { _id : 0, host : "192.168.48.251:27019" },
      { _id : 1, host : "192.168.48.252:27019" },
      { _id : 2, host : "192.168.48.253:27019" }
    ]
  }
)'

mongo 192.168.48.253:27018 --eval 'rs.initiate(
  {
    _id : "warehouse",
    members: [
      { _id : 0, host : "192.168.48.249:27018" },
      { _id : 1, host : "192.168.48.250:27018" },
      { _id : 2, host : "192.168.48.251:27018" },
      { _id : 3, host : "192.168.48.252:27018" },
      { _id : 4, host : "192.168.48.253:27018" }
    ]
  }
)'

for i in $(echo $SHARDS | sed "s/,/ /g")
do
    ssh $CLUSTER_NAME@$i "mongos --configdb conf/192.168.48.251:27019,192.168.48.252:27019,192.168.48.253:27019 --bind_ip $i &" &
done

mongo 192.168.48.253:27017 --eval 'sh.addShard("warehouse/192.168.48.249:27018")'
mongo 192.168.48.253:27017 --eval 'sh.addShard("warehouse/192.168.48.250:27018")'
mongo 192.168.48.253:27017 --eval 'sh.addShard("warehouse/192.168.48.251:27018")'
mongo 192.168.48.253:27017 --eval 'sh.addShard("warehouse/192.168.48.252:27018")'
mongo 192.168.48.253:27017 --eval 'sh.addShard("warehouse/192.168.48.253:27018")'

mongo 192.168.48.253:27017 --eval "use warehouse"
mongo 192.168.48.253:27017 --eval sh.enableSharding("warehouse")

