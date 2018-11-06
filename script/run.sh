#!/bin/bash
JAR=../build/libs/cs4224-project-all.jar #path to jar
MASTER=192.168.48.249
SEEDS=${MASTER},192.168.48.250,192.168.48.251,192.168.48.252,192.168.48.253 #ip of all nodes
CLUSTER_NAME=cs4224g
XACT=4224-project-files/xact-files
CONSISTENCY=$2
NC=$1

scp $JAR $CLUSTER_NAME@$MASTER:~/app.jar

IFS=', ' read -r -a array <<< $SEEDS
for i in $(seq 1 $NC); do
	ssh $CLUSTER_NAME@${array[$(( ($i - 1) % 5 ))]} "java -jar ~/app.jar $CONSISTENCY < $XACT/$i.txt > ~/$i.out && tail $i.out >> stats.out && echo 'Transaction $i done'" &
done

wait
echo "ALL TRANSACTIONS DONE"

for i in $(seq 1 $NC); do
	ssh $CLUSTER_NAME@$MASTER "cat ~/$i.out | grep '#!#!STATS:''"
done
#4a
echo 'db.warehouse.aggregate([ { 
    $group: { 
        _id: null, 
        sum: { 
            $sum: "$W_YTD"
        } 
    } 
} ] )
db.district.aggregate([ { 
    $group: { 
        _id: null, 
        sum: { 
            $sum: "$D_YTD"
        } 
    } 
} ] )
db.district.aggregate([ { 
    $group: { 
        _id: null, 
        sum: { 
            $sum: "$D_NEXT_O_ID"
        } 
    } 
} ] )
db.customer.aggregate([ { 
    $group: { 
        _id: null, 
        sum: { 
            $sum: "$C_BALANCE"
        } 
    } 
} ] )
db.customer.aggregate([ { 
    $group: { 
        _id: null, 
        sum: { 
            $sum: "$C_YTD_PAYMENT"
        } 
    } 
} ] )
db.customer.aggregate([ { 
    $group: { 
        _id: null, 
        sum: { 
            $sum: "$C_PAYMENT_CNT"
        } 
    } 
} ] )
db.customer.aggregate([ { 
    $group: { 
        _id: null, 
        sum: { 
            $sum: "$C_DELIVERY_CNT"
        } 
    } 
} ] )
db.order.find().sort({O_ID:-1}).limit(1)
db.order.aggregate([ { 
    $group: { 
        _id: null, 
        sum: { 
            $sum: "$O_OL_CNT"
        } 
    } 
} ] )
db.order_line.aggregate([ { 
    $group: { 
        _id: null, 
        sum: { 
            $sum: "$OL_AMOUNT"
        } 
    } 
} ] )
db.order_line.aggregate([ { 
    $group: { 
        _id: null, 
        sum: { 
            $sum: "$OL_QUANTITY"
        } 
    } 
} ] )
db.stock.aggregate([ { 
    $group: { 
        _id: null, 
        sum: { 
            $sum: "$S_QUANTITY"
        } 
    } 
} ] )
db.stock.aggregate([ { 
    $group: { 
        _id: null, 
        sum: { 
            $sum: "$S_YTD"
        } 
    } 
} ] )
db.stock.aggregate([ { 
    $group: { 
        _id: null, 
        sum: { 
            $sum: "$S_ORDER_CNT"
        } 
    } 
} ] )' | mongo $MASTER >> database.out
#end