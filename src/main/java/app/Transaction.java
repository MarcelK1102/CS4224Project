package app;

import java.math.BigDecimal;
import java.sql.Date;

import java.time.Instant;
import java.util.List;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;

public class Transaction {

    public class TransactionException extends Exception {
        public TransactionException(String message) { super(message); }
    }
    Connector cn = new Connector();

    //Transaction 1
    public void newOrder(int wid, int did, int cid, List<Integer> ids, List<Integer> wids, List<Integer> quantities ) throws TransactionException{
        Session s = cn.connect();
        try{
            Wrapper w = new Wrapper(s);
            //Step 1
            int N = w.findDistrict(wid, did, "D_NEXT_O_ID")
                .orElseThrow(() -> new TransactionException("Unable to find wid:"+wid+" did:" + did))
                .getInt(0) + 1;
            
            // https://stackoverflow.com/questions/3935915/how-to-create-auto-increment-ids-in-cassandra/29391877#29391877
            //Step 2
            while(!s.execute(QueryBuilder.update("district")
                    .with(QueryBuilder.set("D_NEXT_O_ID", N))
                    .where(QueryBuilder.eq("D_W_ID", wid))
                    .and(QueryBuilder.eq("D_ID", did))
                    .onlyIf(QueryBuilder.eq("D_NEXT_O_ID", N-1))
                ).one().getBool(0)){N++;}

            //Step 3
            s.execute((QueryBuilder.insertInto("orders")
                    .value("O_ID", N)
                    .value("O_D_ID", did)
                    .value("O_W_ID ", wid) 
                    .value("O_C_ID", cid) 
                    .value("O_ENTRY_D", Date.from(Instant.now()))
                    .value("O_OL_CNT", BigDecimal.valueOf( ids.size())) 
                    .value("O_ALL_LOCAL", BigDecimal.valueOf(wids.stream().allMatch(i -> i == wid) ? 1 : 0))
            ));

            //Step 4
            int totalAmount = 0;
            
            //Step 5
            for(int i = 0; i < ids.size(); i++){
                Row r;
                int iid = ids.get(i);
                while(true){
                    //Step a
                    r = w.findStock(wid, iid, "S_QUANTITY"," S_YTD"," S_ORDER_CNT"," S_REMOTE_CNT"," S_DIST_" + String.format("%02d", did))
                        .orElseThrow(() -> new TransactionException("Unable to find stock for item:" + ids.get(0) + " in warehouse:" + wid));
                    if(r == null){
                        System.out.println("Unable to find stock for wid:" + wid + " iID = " + iid);
                        continue;
                    }
                    //Step b
                    BigDecimal quantity = r.getDecimal("S_QUANTITY").subtract(BigDecimal.valueOf(quantities.get(i)));
                    
                    //Step c
                    quantity.add(BigDecimal.valueOf(quantity.doubleValue() < 10.0 ? 100.0 : 0.0));

                    //Step f
                    r = s.execute(QueryBuilder.update("stock")
                        .with(QueryBuilder.set("S_QUANTITY", quantity))
                        .and(QueryBuilder.set("S_YTD", r.getDecimal("S_YTD").add(BigDecimal.valueOf(quantities.get(i)))))
                        .and(QueryBuilder.set("S_ORDER_CNT", r.getInt("S_ORDER_CNT") + 1))
                        .and(QueryBuilder.set("S_REMOTE_CNT", r.getInt("S_REMOTE_CNT") + (wid != wids.get(i) ? 1 : 0)))
                        .where(QueryBuilder.eq("S_I_ID", iid))
                        .and(QueryBuilder.eq("S_W_ID", wid))
                        .onlyIf(QueryBuilder.eq("S_QUANTITY", r.getDecimal(0)))
                    ).one();
                    if(r.getBool(0))
                        break;
                }
                //Step e
                int itemAmount = w.findItem(iid, "I_PRICE").orElseThrow(() -> new TransactionException("Unable to find item with id:" + iid)).getInt(0);
                
                //Step f
                totalAmount += itemAmount;
                
                //Step g
                s.execute(QueryBuilder.insertInto("order_line")
                    .value("OL_O_ID", N)
                    .value("OL_D_ID", did)
                    .value("OL_W_ID", wid)
                    .value("OL_NUMBER", i)
                    .value("OL_I_ID", iid)
                    .value("OL_SUPPLY_W_ID", wids.get(i))
                    .value("OL_QUANTITY", quantities.get(i))
                    .value("OL_AMOUNT", itemAmount)
                    .value("OL_DIST_INFO","S_DIST_" + String.format("%02d", did))
                );
            }
        } finally{
            s.close();
        }
    }

    //Transaction 5
    public long stockLevel(int wid, int did, BigDecimal t, int l){
        Session s = cn.connect();
        Wrapper w = new Wrapper(s);
        // w.findDistrict(wid, did, "D_NEXT_O_ID").getInt(0);
        // s.execute("start transaction;");

        // int n = s.execute(
        //     s.prepare("select D_NEXT_O_ID from district where D_W_ID = ? and D_ID = ?;")
        //     .bind(wid, did)
        //     ).all()
        //     .get(0)
        //     .getInt(0);

        // ResultSet S = s.execute( s.prepare("select OL_I_ID from order_line where OL_D_ID = ? and OL_W_ID = ? and OL_O_ID < ? and OL_O_ID >= ? allow filtering;")
        //     .bind(did, wid, n, n-l));
        
        long sum = 0;
        // Iterator<Row> rows = S.iterator();
        // while(rows.hasNext()){
        //     int sid = rows.next().getInt(0);
        //     System.out.println(sid);
        //     s.execute(s.prepare("select count(S_I_ID) from stock where S_I_ID = ? and S_QUANTITY < ? allow filtering;")
        //     .bind(sid, t));
        // }
        // s.close();

        return sum;
    }

    //Transaction 7
    public void topBalance(){
        Session s = cn.connect();
        //step 1
        ResultSet rs = s.execute(QueryBuilder.select()
            .from("customer_by_balance")
            .orderBy(QueryBuilder.desc("C_BALANCE"))
            .limit(10)
        );
    }
}