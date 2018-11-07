#!/bin/bash
#Here the three first shards will be config servers
SHARDS=(192.168.48.249 192.168.48.250 192.168.48.251 192.168.48.252 192.168.48.253)
CLUSTER_NAME=cs4224g
PROJECT_PATH=..

##Add ssh key for shared drive as to avoid giving password in the future
# ssh-keygen -t rsa
# ssh ${CLUSTER_NAME}@${MASTER} mkdir -p .ssh #DO THIS IF NOT DONE
# cat ~/.ssh/id_rsa.pub | ssh ${CLUSTER_NAME}@${MASTER} 'cat >> ~/.ssh/authorized_keys'

#Execute install script
for i in {0..4}
do
    ssh $CLUSTER_NAME@${SHARDS[i]} "bash -s" < $PROJECT_PATH/script/install.sh $i &
done

wait
echo "DONE INSTALLING"

for i in {0..2}
do
    ssh $CLUSTER_NAME@${SHARDS[i]}  "/temp/g/mongoDB/bin/mongod --configsvr --dbpath /temp/g/data/conf --bind_ip ${SHARDS[i]} --replSet conf &" &
done

sleep 20

echo "rs.initiate({
    _id: 'conf',
    configsvr: true,
    members: [
      { _id : 0, host : '${SHARDS[0]}:27019' },
      { _id : 1, host : '${SHARDS[1]}:27019' },
      { _id : 2, host : '${SHARDS[2]}:27019' }
    ]
  }
)" | ssh $CLUSTER_NAME@${SHARDS[0]} "/temp/g/mongoDB/bin/mongo ${SHARDS[0]}:27019"

for i in {0..4}
do
    ssh $CLUSTER_NAME@${SHARDS[i]} "/temp/g/mongoDB/bin/mongod --shardsvr --replSet warehouse --dbpath /temp/g/data/db/ --bind_ip ${SHARDS[i]} &" &
done

sleep 20

echo "rs.initiate({
    _id : 'warehouse',
    members: [
      { _id : 0, host : '${SHARDS[0]}:27018' },
      { _id : 1, host : '${SHARDS[1]}:27018' },
      { _id : 2, host : '${SHARDS[2]}:27018' },
      { _id : 3, host : '${SHARDS[3]}:27018' },
      { _id : 4, host : '${SHARDS[4]}:27018' }
    ]
  }
)" | ssh $CLUSTER_NAME@${SHARDS[0]}  "/temp/g/mongoDB/bin/mongo ${SHARDS[0]}:27018"

for i in {0..4}
do
  ssh $CLUSTER_NAME@${SHARDS[i]} "/temp/g/mongoDB/bin/mongos --configdb conf/${SHARDS[0]}:27019,${SHARDS[1]}:27019,${SHARDS[2]}:27019 --bind_ip ${SHARDS[i]} &" &
done

sleep 20

echo "
sh.addShard('warehouse/${SHARDS[0]}:27018')
sh.addShard('warehouse/${SHARDS[1]}:27018')
sh.addShard('warehouse/${SHARDS[2]}:27018')
sh.addShard('warehouse/${SHARDS[3]}:27018')
sh.addShard('warehouse/${SHARDS[4]}:27018')
sh.enableSharding('warehouse')
" | ssh $CLUSTER_NAME@${SHARDS[0]} "/temp/g/mongoDB/bin/mongo ${SHARDS[0]}"

ssh $CLUSTER_NAME@${SHARDS[0]} "wget https://www.comp.nus.edu.sg/~cs4224/4224-project-files.zip;unzip 4224-project-files.zip;sed -i -e 's/,null,/,,/g' 4224-project-files/data-files/*.csv"