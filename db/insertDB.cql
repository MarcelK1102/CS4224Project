USE Warehouse;

COPY warehouse FROM 'warehouse_data.csv'  WITH HEADER = TRUE;
COPY warehouse_cnts FROM 'warehouse_cnts.csv'  WITH HEADER = TRUE;

COPY district FROM 'district_data.csv'  WITH HEADER = TRUE;
COPY district_cnts FROM 'district_cnts.csv'  WITH HEADER = TRUE;

COPY customer ( 
    C_W_ID, 
    C_D_ID, 
    C_ID, 
    C_FIRST, 
    C_MIDDLE, 
    C_LAST, 
    C_STREET_1, 
    C_STREET_2, 
    C_CITY, 
    C_STATE, 
    C_ZIP,
    C_PHONE,
    C_SINCE,
    C_CREDIT,
    C_CREDIT_LIM,
    C_DISCOUNT,
    C_DATA
) FROM 'customer_data.csv'  WITH HEADER = TRUE;
COPY customer_cnts FROM 'customer_cnts.csv'  WITH HEADER = TRUE;

COPY orders ( 
    O_W_ID, 
    O_D_ID, 
    O_ID, 
    O_C_ID, 
    O_CARRIER_ID, 
    O_OL_CNT, 
    O_ALL_LOCAL, 
    O_ENTRY_D
) FROM 'order.csv'  WITH HEADER = TRUE;

COPY item ( 
    I_ID, 
    I_NAME, 
    I_PRICE, 
    I_IM_ID, 
    I_DATA
) FROM 'item.csv'  WITH HEADER = TRUE;

COPY order_line ( 
    OL_W_ID, 
    OL_D_ID, 
    OL_O_ID, 
    OL_NUMBER, 
    OL_I_ID, 
    OL_DELIVERY_D, 
    OL_AMOUNT, 
    OL_SUPPLY_W_ID, 
    OL_QUANTITY, 
    OL_DIST_INFO
) FROM 'order-line.csv' WITH HEADER = TRUE ;

COPY stock FROM 'stock_data.csv' WITH HEADER = TRUE ;

COPY stock_cnts FROM 'stock_cnts.csv' WITH HEADER = TRUE ;