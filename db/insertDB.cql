USE Warehouse;

COPY warehouse ( 
    W_ID, 
    W_NAME, 
    W_STREET_1, 
    W_STREET_2, 
    W_CITY, 
    W_STATE, 
    W_ZIP,
    W_TAX,
    W_YTD
) FROM 'warehouse.csv';

COPY district ( 
    D_W_ID, 
    D_ID, 
    D_NAME, 
    D_STREET_1, 
    D_STREET_2, 
    D_CITY, 
    D_STATE, 
    D_ZIP,
    D_TAX,
    D_YTD,
    D_NEXT_O_ID
) FROM 'district.csv';

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
    C_BALANCE,
    C_YTD_PAYMENT,
    C_PAYMENT_CNT,
    C_DELIVERY_CNT,
    C_DATA
) FROM 'customer.csv';

COPY orders ( 
    O_W_ID, 
    O_D_ID, 
    O_ID, 
    O_C_ID, 
    O_CARRIER_ID, 
    O_OL_CNT, 
    O_ALL_LOCAL, 
    O_ENTRY_D
) FROM 'order.csv';

COPY item ( 
    I_ID, 
    I_NAME, 
    I_PRICE, 
    I_IM_ID, 
    I_DATA
) FROM 'item.csv';

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
) FROM 'order-line.csv';

COPY stock FROM 'stock_data.csv' WITH HEADER = TRUE ;

COPY stock_cnts FROM 'stock_cnts.csv' WITH HEADER = TRUE ;