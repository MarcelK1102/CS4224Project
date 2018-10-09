#!/bin/bash
MASTER=192.168.48.249
SEEDS=${MASTER},192.168.48.250,192.168.48.251,192.168.48.252,192.168.48.253 #ip of all nodes
CLUSTER_NAME=cs4224g
JAR=../build/libs/cs4224-project-all.jar #path to jar
XACT=/temp/4224-project-files/xact-files
CONSISTENCY=$2
NC=$1

# ssh-keygen -t rsa
# ssh ${CLUSTER_NAME}@${MASTER} mkdir -p .ssh #DO THIS IF NOT DONE
# cat ~/.ssh/id_rsa.pub | ssh ${CLUSTER_NAME}@${MASTER} 'cat >> ~/.ssh/authorized_keys'
scp $JAR $CLUSTER_NAME@$MASTER:~/app.jar
IFS=', ' read -r -a array <<< $SEEDS
control_c() {
    for i in $(echo $SEEDS | sed "s/,/ /g")
	do
		ssh $CLUSTER_NAME@${i} "ps aux | grep app.jar | awk '{print \$2}' | xargs kill -9"
	done
    exit
}
trap control_c SIGINT
for i in $(seq 1 $NC); do
	ssh $CLUSTER_NAME@${array[$(( ($i - 1) % 5 ))]} "java -jar ~/app.jar $CONSISTENCY < $XACT/$i.txt > ~/$i.out" &
done

wait
echo "ALL TRANSACTIONS DONE"

for i in $(seq 1 $NC); do
	ssh $CLUSTER_NAME@$MASTER "tail -n 1 ~/$i.out"
done