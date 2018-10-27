db.warehouse.drop()
db.district.drop()
db.customer.drop()
db.order.drop()
db.item.drop()
db.order.drop()
db.stock.drop()
mongoimport --host 192.168.48.249 -d warehouse -c warehouse --type csv --fields W_ID,W_NAME,W_STREET_1,W_STREET_2,W_CITY,W_STATE,W_ZIP,W_TAX,W_YTD --file /temp/4224-project-files/data-files/warehouse.csv
mongoimport --host 192.168.48.249 -d warehouse -c district --type csv --fields D_W_ID,D_ID,D_NAME,D_STREET_1,D_STREET_2,D_CITY,D_STATE,D_ZIP,D_TAX,D_YTD,D_NEXT_O_ID --file /temp/4224-project-files/data-files/district.csv
mongoimport --host 192.168.48.249 -d warehouse -c customer --type csv --fields C_W_ID,C_D_ID,C_ID,C_FIRST,C_MIDDLE,C_LAST,C_STREET_1,C_STREET_2,C_CITY,C_STATE,C_ZIP,C_PHONE,C_SINCE,C_CREDIT,C_CREDIT_LIM,C_DISCOUNT,C_BALANCE,C_YTD_PAYMENT,C_PAYMENT_CNT,C_DELIVERY_CNT,C_DATA --file /temp/4224-project-files/data-files/customer.csv
mongoimport --host 192.168.48.249 -d warehouse -c order --type csv --fields O_W_ID,O_D_ID,O_ID,O_C_ID,O_CARRIER_ID,O_OL_CNT,O_ALL_LOCAL,O_ENTRY_D --file /temp/4224-project-files/data-files/order.csv
mongoimport --host 192.168.48.249 -d warehouse -c item --type csv --fields I_ID,I_NAME,I_PRICE,I_IM_ID,I_DATA --file /temp/4224-project-files/data-files/item.csv
mongoimport --host 192.168.48.249 -d warehouse -c order-line --type csv --fields OL_W_ID,OL_D_ID,OL_O_ID,OL_NUMBER,OL_I_ID,OL_DELIVERY_D,OL_AMOUNT,OL_SUPPLY_W_ID,OL_QUANTITY,OL_DIST_INFO --file /temp/4224-project-files/data-files/order-line.csv
mongoimport --host 192.168.48.249 -d warehouse -c stock --type csv --fields S_W_ID,S_I_ID,S_QUANTITY,S_YTD,S_ORDER_CNT,S_REMOTE_CNT,S_DIST_01,S_DIST_02,S_DIST_03,S_DIST_04,S_DIST_05,S_DIST_06,S_DIST_07,S_DIST_08,S_DIST_09,S_DIST_10,S_DATA --file /temp/4224-project-files/data-files/stock.csv
