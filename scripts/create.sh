#!/bin/bash

echo "Creating tables..."

./apache-cassandra-3.11.0/bin/cqlsh $1 $2 <<EOF

use ws;

DROP TABLE IF EXISTS warehouse_district;

DROP MATERIALIZED VIEW IF EXISTS customer_by_balance;

DROP TABLE IF EXISTS customer;

DROP TABLE IF EXISTS orders;

DROP TABLE IF EXISTS order_line;

DROP TABLE IF EXISTS item_stock;

DROP KEYSPACE IF EXISTS ws;

CREATE KEYSPACE ws WITH REPLICATION = {'class':'SimpleStrategy', 'replication_factor': 3};

use ws;

CREATE TABLE warehouse_district (W_ID int, W_NAME text static, W_STREET_1 text static, W_STREET_2 text static, W_CITY text static, W_STATE text static, W_ZIP text static, W_TAX double static, W_YTD double static, D_ID int, D_NAME text, D_STREET_1 text, D_STREET_2 text, D_CITY text, D_STATE text, D_ZIP text, D_TAX double, D_YTD double, D_NEXT_O_ID int, PRIMARY KEY (W_ID, D_ID)) WITH CLUSTERING ORDER BY (D_ID ASC);

CREATE TABLE customer (C_W_ID int, C_D_ID int, C_ID int, C_FIRST text, C_MIDDLE text, C_LAST text, C_STREET_1 text, C_STREET_2 text, C_CITY text, C_STATE text, C_ZIP text, C_PHONE text, C_SINCE timestamp, C_CREDIT text, C_CREDIT_LIM double, C_DISCOUNT double, C_BALANCE double, C_YTD_PAYMENT double, C_PAYMENT_CNT int, C_DELIVERY_CNT int, C_DATA text, C_LAST_O_ID int, PRIMARY KEY ((C_W_ID, C_D_ID), C_ID));

CREATE MATERIALIZED VIEW customer_by_balance AS SELECT C_W_ID, C_D_ID, C_ID, C_BALANCE, C_FIRST, C_MIDDLE, C_LAST FROM customer WHERE C_W_ID IS NOT NULL AND C_D_ID IS NOT NULL AND C_ID IS NOT NULL AND C_BALANCE IS NOT NULL PRIMARY KEY (C_W_ID, C_BALANCE, C_D_ID, C_ID) WITH CLUSTERING ORDER BY (C_BALANCE DESC);

CREATE TABLE orders (O_W_ID int, O_D_ID int, O_ID int, O_C_ID int, O_CARRIER_ID int, O_OL_CNT int, O_ALL_LOCAL int, O_ENTRY_D timestamp, PRIMARY KEY ((O_W_ID, O_D_ID), O_ID)) WITH CLUSTERING ORDER BY (O_ID ASC);

CREATE TABLE order_line (OL_W_ID int, OL_D_ID int, OL_O_ID int, OL_NUMBER int, OL_I_ID int, OL_I_NAME text, OL_DELIVERY_D timestamp static, OL_AMOUNT double, OL_SUPPLY_W_ID int, OL_QUANTITY int, OL_DIST_INFO text, PRIMARY KEY((OL_W_ID, OL_D_ID, OL_O_ID), OL_NUMBER)) WITH CLUSTERING ORDER BY (OL_NUMBER ASC);

CREATE INDEX IF NOT EXISTS o_carrier_id_idx ON orders (O_CARRIER_ID);

CREATE TABLE item_stock (S_I_ID int, S_W_ID int, I_NAME text static, I_PRICE double static, I_IM_ID int static, I_DATA text static, S_QUANTITY int, S_YTD double, S_ORDER_CNT int, S_REMOTE_CNT int, S_DIST_01 text, S_DIST_02 text, S_DIST_03 text, S_DIST_04 text, S_DIST_05 text, S_DIST_06 text, S_DIST_07 text, S_DIST_08 text, S_DIST_09 text, S_DIST_10 text, S_DATA text, PRIMARY KEY (S_I_ID, S_W_ID)) WITH CLUSTERING ORDER BY (S_W_ID asc);

exit

EOF

echo "Finish creating table."