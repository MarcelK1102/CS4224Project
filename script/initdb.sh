#!/bin/bash
CASSANDRA=apache-cassandra-$1
MASTER=$2
rm -rf 4224-project-files*
wget https://www.comp.nus.edu.sg/~cs4224/4224-project-files.zip
unzip 4224-project-files.zip
cd 4224-project-files/data-files/
echo -e "W_ID, W_NAME, W_STREET_1, W_STREET_2, W_CITY, W_STATE, W_ZIP, W_TAX, W_YTD\n$(cat warehouse.csv)" > warehouse.csv
echo -e "D_W_ID, D_ID, D_NAME, D_STREET_1, D_STREET_2, D_CITY, D_STATE, D_ZIP, D_TAX, D_YTD, D_NEXT_O_ID\n$(cat district.csv)" > district.csv
echo -e "C_W_ID, C_D_ID, C_ID, C_FIRST, C_MIDDLE, C_LAST, C_STREET_1, C_STREET_2, C_CITY, C_STATE, C_ZIP, C_PHONE, C_SINCE, C_CREDIT, C_CREDIT_LIM, C_DISCOUNT, C_BALANCE, C_YTD_PAYMENT, C_PAYMENT_CNT, C_DELIVERY_CNT, C_DATA\n$(cat customer.csv)" > customer.csv
echo -e "O_W_ID, O_D_ID, O_ID, O_C_ID, O_CARRIER_ID, O_OL_CNT, O_ALL_LOCAL, O_ENTRY_D\n$(cat order.csv)" > order.csv
echo -e "I_ID, I_NAME, I_PRICE, I_IM_ID, I_DATA\n$(cat item.csv)" > item.csv
echo -e "OL_W_ID, OL_D_ID, OL_O_ID, OL_NUMBER, OL_I_ID, OL_DELIVERY_D, OL_AMOUNT, OL_SUPPLY_W_ID, OL_QUANTITY, OL_DIST_INFO\n$(cat order-line.csv)" > order-line.csv
echo -e "S_W_ID, S_I_ID, S_QUANTITY, S_YTD, S_ORDER_CNT, S_REMOTE_CNT, S_DIST_01, S_DIST_02, S_DIST_03, S_DIST_04, S_DIST_05, S_DIST_06, S_DIST_07, S_DIST_08, S_DIST_09, S_DIST_10, S_DATA\n$(cat stock.csv)" > stock.csv

awk 'BEGIN{FS=OFS=","} {print $1,$2,$3,$4,$5,$6}' stock.csv > stock_cnts.csv
awk 'BEGIN{FS=OFS=","} {print $1,$2,$7,$8,$9,$10,$11,$12,$13,$14,$15,$16,$17}' stock.csv > stock_data.csv

awk 'BEGIN{FS=OFS=","} {print $1,$9}' warehouse.csv > warehouse_cnts.csv
awk 'BEGIN{FS=OFS=","} {print $1,$2,$3,$4,$5,$6,$7,$8}' warehouse.csv > warehouse_data.csv

awk 'BEGIN{FS=OFS=","} {print $1,$2,$10,$11}' district.csv > district_cnts.csv
awk 'BEGIN{FS=OFS=","} {print $1,$2,$3,$4,$5,$6,$7,$8,$9}' district.csv > district_data.csv

awk 'BEGIN{FS=OFS=","} {print $1,$2,$3,$17,$18,$19,$20}' customer.csv > customer_cnts.csv
awk 'BEGIN{FS=OFS=","} {print $1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12,$13,$14,$15,$16,$21}' customer.csv > customer_data.csv

sed -i -e 's/\.0//g' stock_cnts.csv
sed -i -e 's/\.0//g' warehouse_cnts.csv
sed -i -e 's/\.0//g' district_cnts.csv
sed -i -e 's/\.0//g' customer_cnts.csv
sed -i -e 's/,null,/,,/g' *.csv
/temp/$CASSANDRA/bin/cqlsh $MASTER -f ~/createDB.cql
/temp/$CASSANDRA/bin/cqlsh $MASTER -f ~/insertDB.cql

