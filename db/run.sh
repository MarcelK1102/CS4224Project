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
	ssh $CLUSTER_NAME@$MASTER "cat ~/$i.out | grep '#!#!STATS:''"
done
#4a
cqlsh $MASTER --request-timeout=3600 -e "select sum(W_YTD) from warehouse.warehouse"
#4b
cqlsh $MASTER --request-timeout=3600 -e "select sum(D_YTD), sum(D_NEXT_O_ID) from warehouse.district"
#4c
cqlsh $MASTER --request-timeout=3600 -e "select sum(C_BALANCE), sum(C_YTD_PAYMENT), sum(C_PAYMENT_CNT), sum(C_DELIVERY_CNT) from warehouse.customer"
#4d
cqlsh $MASTER --request-timeout=3600 -e "select max(O_ID), sum(O_OL_CNT) from warehouse.orders"
#4e
cqlsh $MASTER --request-timeout=3600 -e "select sum(OL_AMOUNT), sum(OL_QUANTITY) from warehouse.order_line" 
#4f
cqlsh $MASTER --request-timeout=3600 -e "select sum(S_QUANTITY), sum(S_YTD), sum(S_ORDER_CNT) from warehouse.stock"