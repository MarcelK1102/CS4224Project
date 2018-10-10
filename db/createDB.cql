drop keyspace if exists warehouse;
CREATE KEYSPACE Warehouse WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 3};
USE Warehouse;

CREATE TABLE warehouse ( 
    W_ID INT, 
    W_NAME VARCHAR, 
    W_STREET_1 VARCHAR, 
    W_STREET_2 VARCHAR, 
    W_CITY VARCHAR, 
    W_STATE VARCHAR, 
    W_ZIP VARCHAR,
    W_TAX DECIMAL,
    W_YTD DECIMAL,
    PRIMARY KEY(W_ID)
);

CREATE TABLE district ( 
    D_W_ID INT, 
    D_ID INT, 
    D_NAME VARCHAR, 
    D_STREET_1 VARCHAR, 
    D_STREET_2 VARCHAR, 
    D_CITY VARCHAR, 
    D_STATE VARCHAR, 
    D_ZIP VARCHAR,
    D_TAX DECIMAL,
    D_YTD DECIMAL,
    D_NEXT_O_ID INT,
    PRIMARY KEY( (D_ID), D_W_ID )
);

CREATE TABLE customer ( 
    C_W_ID INT, 
    C_D_ID INT, 
    C_ID INT, 
    C_FIRST VARCHAR, 
    C_MIDDLE VARCHAR, 
    C_LAST VARCHAR, 
    C_STREET_1 VARCHAR, 
    C_STREET_2 VARCHAR, 
    C_CITY VARCHAR, 
    C_STATE VARCHAR, 
    C_ZIP VARCHAR,
    C_PHONE VARCHAR,
    C_SINCE TIMESTAMP,
    C_CREDIT VARCHAR,
    C_CREDIT_LIM DECIMAL,
    C_DISCOUNT DECIMAL,
    C_BALANCE DECIMAL,
    C_YTD_PAYMENT FLOAT,
    C_PAYMENT_CNT INT,
    C_DELIVERY_CNT INT,
    C_DATA VARCHAR,
    PRIMARY KEY( (C_D_ID), C_W_ID, C_ID)
);

create materialized view customer_by_balance 
as select C_BALANCE, C_ID, C_W_ID, C_D_ID
from customer
where C_BALANCE is not null
and C_ID is not null
and C_W_ID is not null
and C_D_ID is not null
primary key ((C_D_ID), C_BALANCE, C_ID, C_W_ID)
with clustering order by (C_BALANCE DESC);

CREATE TABLE orders ( 
    O_W_ID INT, 
    O_D_ID INT, 
    O_ID INT, 
    O_C_ID INT, 
    O_CARRIER_ID INT, 
    O_OL_CNT DECIMAL, 
    O_ALL_LOCAL DECIMAL, 
    O_ENTRY_D TIMESTAMP,
    PRIMARY KEY((O_D_ID, O_W_ID), O_ID)
) WITH CLUSTERING ORDER BY (O_ID asc);

CREATE MATERIALIZED VIEW order_by_customer 
as select O_W_ID, O_D_ID, O_ID, O_C_ID
from orders
where O_W_ID is not null
and O_D_ID is not null
and O_ID is not null
and O_C_ID is not null
primary key ((O_W_ID, O_D_ID, O_C_ID), O_ID);

CREATE TABLE item ( 
    I_ID INT, 
    I_NAME VARCHAR, 
    I_PRICE DECIMAL, 
    I_IM_ID INT, 
    I_DATA VARCHAR,
    PRIMARY KEY(I_ID)
);

CREATE TABLE order_line ( 
    OL_W_ID INT, 
    OL_D_ID INT, 
    OL_O_ID INT, 
    OL_NUMBER INT, 
    OL_I_ID INT, 
    OL_DELIVERY_D TIMESTAMP, 
    OL_AMOUNT DECIMAL, 
    OL_SUPPLY_W_ID INT, 
    OL_QUANTITY DECIMAL, 
    OL_DIST_INFO VARCHAR,
    PRIMARY KEY((OL_D_ID, OL_W_ID) , OL_O_ID, OL_NUMBER)
);


CREATE MATERIALIZED VIEW order_line_by_item 
as select OL_W_ID, OL_D_ID, OL_O_ID, OL_NUMBER, OL_I_ID
from order_line
where OL_W_ID is not null
and OL_D_ID is not null
and OL_O_ID is not null
and OL_NUMBER is not null
and OL_I_ID is not null
primary key ((OL_D_ID, OL_I_ID), OL_W_ID, OL_NUMBER, OL_O_ID);

CREATE TABLE stock (
    S_W_ID INT, 
    S_I_ID INT, 
    S_QUANTITY DECIMAL, 
    S_YTD DECIMAL, 
    S_ORDER_CNT INT,
    S_REMOTE_CNT INT,
    S_DIST_01 VARCHAR,
    S_DIST_02 VARCHAR,
    S_DIST_03 VARCHAR,
    S_DIST_04 VARCHAR,
    S_DIST_05 VARCHAR,
    S_DIST_06 VARCHAR,
    S_DIST_07 VARCHAR,
    S_DIST_08 VARCHAR,
    S_DIST_09 VARCHAR,
    S_DIST_10 VARCHAR,
    S_DATA VARCHAR,
    PRIMARY KEY((S_W_ID), S_I_ID)
);

CREATE MATERIALIZED VIEW stock_by_quantity 
as select S_W_ID, S_I_ID, S_QUANTITY
from stock 
where S_W_ID is not null
and S_I_ID is not null
and S_QUANTITY is not null
PRIMARY KEY ((S_W_ID, S_I_ID), S_QUANTITY);

