#!/bin/bash
JAR=../build/libs/cs4224-project-all.jar #path to jar
MASTER=192.168.48.249
SEEDS=${MASTER},192.168.48.250,192.168.48.251,192.168.48.252,192.168.48.253 #ip of all nodes
CLUSTER_NAME=cs4224g
XACT=4224-project-files/xact-files
CONSISTENCY=$2
NC=$1

# scp $JAR $CLUSTER_NAME@$MASTER:~/app.jar

mongoimport --host $IP -d warehouse --ignoreBlanks -c warehouse --type csv --fields W_ID,W_NAME,W_STREET_1,W_STREET_2,W_CITY,W_STATE,W_ZIP,W_TAX,W_YTD --file ~/4224-project-files/data-files/warehouse.csv --drop
mongoimport --host $IP -d warehouse --ignoreBlanks -c district --type csv --fields D_W_ID,D_ID,D_NAME,D_STREET_1,D_STREET_2,D_CITY,D_STATE,D_ZIP,D_TAX,D_YTD,D_NEXT_O_ID --file ~/4224-project-files/data-files/district.csv --drop
mongoimport --host $IP -d warehouse --ignoreBlanks -c customer --type csv --fields C_W_ID,C_D_ID,C_ID,C_FIRST,C_MIDDLE,C_LAST,C_STREET_1,C_STREET_2,C_CITY,C_STATE,C_ZIP,C_PHONE,C_SINCE,C_CREDIT,C_CREDIT_LIM,C_DISCOUNT,C_BALANCE,C_YTD_PAYMENT,C_PAYMENT_CNT,C_DELIVERY_CNT,C_DATA --file ~/4224-project-files/data-files/customer.csv --drop
mongoimport --host $IP -d warehouse --ignoreBlanks -c order --type csv --fields O_W_ID,O_D_ID,O_ID,O_C_ID,O_CARRIER_ID,O_OL_CNT,O_ALL_LOCAL,O_ENTRY_D --file ~/4224-project-files/data-files/order.csv --drop
mongoimport --host $IP -d warehouse --ignoreBlanks -c item --type csv --fields I_ID,I_NAME,I_PRICE,I_IM_ID,I_DATA --file ~/4224-project-files/data-files/item.csv --drop
mongoimport --host $IP -d warehouse --ignoreBlanks -c order_line --type csv --fields OL_W_ID,OL_D_ID,OL_O_ID,OL_NUMBER,OL_I_ID,OL_DELIVERY_D,OL_AMOUNT,OL_SUPPLY_W_ID,OL_QUANTITY,OL_DIST_INFO --file ~/4224-project-files/data-files/order-line.csv --drop
mongoimport --host $IP -d warehouse --ignoreBlanks -c stock --type csv --fields S_W_ID,S_I_ID,S_QUANTITY,S_YTD,S_ORDER_CNT,S_REMOTE_CNT,S_DIST_01,S_DIST_02,S_DIST_03,S_DIST_04,S_DIST_05,S_DIST_06,S_DIST_07,S_DIST_08,S_DIST_09,S_DIST_10,S_DATA --file ~/4224-project-files/data-files/stock.csv --drop

echo "use warehouse;
db.warehouse.createIndex({W_ID:1}, {unique:true});
db.district.createIndex({D_W_ID:1, D_ID:1}, {unique:true});
db.customer.createIndex({C_W_ID:1, C_D_ID:1, C_ID:1}, {unique:true});
db.customer.createIndex({C_BALANCE:-1});
db.order.createIndex({O_W_ID:1, O_D_ID:1, O_ID:1}, {unique:true});
db.item.createIndex({I_ID:1}, {unique:true});
db.order_line.createIndex({OL_W_ID:1, OL_D_ID:1, OL_O_ID:1, OL_NUMBER:1}, {unique:true});
db.order_line.createIndex({OL_W_ID:1, OL_D_ID:1, OL_O_ID:1}, {});
db.stock.createIndex({S_W_ID:1, S_I_ID:1}, {unique:true});
db.order_line.createIndex({OL_QUANTITY:-1},{});
db.order.createIndex({O_C_ID:1, O_ENTRY_D:-1},{});
sh.shardCollection('warehouse.warehouse', { W_ID : 1} );
sh.shardCollection('warehouse.district', { D_W_ID : 1} );
sh.shardCollection('warehouse.customer', { C_W_ID : 1} );
sh.shardCollection('warehouse.order', { O_W_ID : 1} );
sh.shardCollection('warehouse.stock', { S_W_ID : 1} );
" | mongo $IP

for i in $(seq 1 $NC); do
	ssh $CLUSTER_NAME@${array[$(( ($i - 1) % 5 ))]} "java -jar ~/app.jar $CONSISTENCY < $XACT/$i.txt > ~/$i.out && tail $i.out >> stats.out && echo 'Transaction $i done'" &
done

wait

for i in $(seq 1 $NC); do
	ssh $CLUSTER_NAME@$MASTER "cat ~/$i.out | grep '#!#!STATS:''"
done
#4a
echo 'use warehouse
db.warehouse.aggregate([ { 
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
mv *.out $CONSISTENCY$NC
#end