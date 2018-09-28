package app;

import java.math.BigDecimal;
import java.sql.Date;

import java.time.Instant;
import java.util.Iterator;
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

    public void newOrder(int wid, int did, int cid, List<Integer> ids, List<Integer> wids, List<Integer> quantities ) throws TransactionException{
        Session s = cn.connect();
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
    }

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

    public void processDelivery(int wid, int carrierid) throws TransactionException {
        Session s = cn.connect();
        Wrapper w = new Wrapper(s);
        for (int districtNo = 1; districtNo <= 10; districtNo++){
            //System.out.println("district: " + districtNo);
            //a)
            int N = s.execute(QueryBuilder
            .select().min("O_ID")
            .from(Connector.keyspace, "orders")
            .where(QueryBuilder.eq("O_W_ID", wid))
            .and(QueryBuilder.eq("O_D_ID", districtNo))
            .and(QueryBuilder.eq("O_CARRIER_ID", -1))
            .allowFiltering())
            .one().getInt(0);

            //System.out.println("N: " +N);

            Row X;
            try {
                X = w.findOrder(wid, districtNo, N)
                        .orElseThrow(() -> new TransactionException("Unable to find order with id:" + N));
            } catch (TransactionException e) {
                //skip if there is no order with id N
                System.out.println("skipped");
                continue;
            }
            int cid = X.getInt("O_C_ID");
            Row C = w.findCustomer(wid, districtNo, cid).orElseThrow(() -> new TransactionException("Unable to find customer with id:" + cid));

            //b)
            //System.out.println("district: " + districtNo + " :b");
            s.execute(QueryBuilder.update(Connector.keyspace, "orders")
            .with(QueryBuilder.set("O_CARRIER_ID", carrierid))
            .where(QueryBuilder.eq("O_W_ID", wid))
            .and(QueryBuilder.eq("O_D_ID", districtNo))
            .and(QueryBuilder.eq("O_ID", N)));

            //c)
            //System.out.println("district: " + districtNo + " :c");

            ResultSet orderlines = s.execute(QueryBuilder.select().all()
            .from(Connector.keyspace, "order_line")
            .where(QueryBuilder.eq("OL_W_ID",wid))
            .and(QueryBuilder.eq("OL_D_ID", districtNo))
            .and(QueryBuilder.eq("OL_O_ID", N)));

            Iterator<Row> it = orderlines.iterator();
            while(it.hasNext()){
                Row currOL = it.next();
                int OL_Number = currOL.getInt("OL_NUMBER");

                s.execute(QueryBuilder.update(Connector.keyspace, "order_line")
                .with(QueryBuilder.set("OL_DELIVERY_D", Date.from(Instant.now())))
                .where(QueryBuilder.eq("OL_W_ID",wid))
                .and(QueryBuilder.eq("OL_D_ID", districtNo))
                .and(QueryBuilder.eq("OL_O_ID", N))
                .and(QueryBuilder.eq("OL_NUMBER", OL_Number)));
            }

            //d)
            //System.out.println("district: " + districtNo + " :d");

            int delivery_cnt = C.getInt("C_DELIVERY_CNT");
            BigDecimal c_balance = C.getDecimal("C_BALANCE");

            //System.out.println("delivercnt:" + delivery_cnt);
            //System.out.println("c_balance:" + c_balance.toString());

            BigDecimal B = s.execute(QueryBuilder.select()
            .sum("OL_AMOUNT")
            .from("order_line")
            .where(QueryBuilder.eq("OL_W_ID",wid))
            .and(QueryBuilder.eq("OL_O_ID", N))
            .allowFiltering())
            .one().getDecimal(0);

            //System.out.println("B:" + B.toString());

            s.execute(QueryBuilder.update(Connector.keyspace, "customer")
            .with(QueryBuilder.set("C_BALANCE", c_balance.add(B)))
            .and(QueryBuilder.set("C_DELIVERY_CNT", delivery_cnt+1))
            .where(QueryBuilder.eq("C_W_ID", wid))
            .and(QueryBuilder.eq("C_D_ID", districtNo))
            .and(QueryBuilder.eq("C_ID", cid)));
            
            //System.out.println("district: " + districtNo + " finished");
        }
    }

    public void getOrderStatus(int c_wid, int c_did, int cid) throws TransactionException {
        Session s = cn.connect();
        Wrapper w = new Wrapper(s);

        //1.
        Row C = w.findCustomer(c_wid, c_did, cid).orElseThrow(() -> new TransactionException("Unable to find customer with id:" + cid));
        System.out.println(String.format("Name: %s %s %s",C.getString("C_FIRST"), C.getString("C_MIDDLE"), C.getString("C_LAST")));
        System.out.println("Balance: " + C.getDecimal("C_BALANCE"));

        //2.
        java.util.Date lastOrderDate = s.execute(QueryBuilder.select()
        .min("O_ENTRY_D")
        .from(Connector.keyspace, "orders")
        .where(QueryBuilder.eq("O_W_ID", c_wid))
        .and(QueryBuilder.eq("O_D_ID", c_did))
        .and(QueryBuilder.eq("O_C_ID", cid))
        .allowFiltering()).one().getTimestamp(0);
        if (lastOrderDate == null){
            throw new TransactionException("No Order with valid timestamp found");
        }

        //get Order Row
        Row lastOrder = s.execute(QueryBuilder.select().all()
        .from(Connector.keyspace, "orders")
        .where(QueryBuilder.eq("O_W_ID", c_wid))
        .and(QueryBuilder.eq("O_D_ID", c_did))
        .and(QueryBuilder.eq("O_ENTRY_D", lastOrderDate))
        .allowFiltering()).one();

        int oid = lastOrder.getInt("O_ID");
        System.out.println("O_ID: " + oid);
        System.out.println("O_ENTRY_ID: " + lastOrder.getTimestamp("O_ENTRY_D"));
        System.out.println("O_CARRIER_ID: " + lastOrder.getInt("O_CARRIER_ID"));

        //3.
        ResultSet orderlines = s.execute(QueryBuilder.select().all()
            .from(Connector.keyspace, "order_line")
            .where(QueryBuilder.eq("OL_W_ID",c_wid))
            .and(QueryBuilder.eq("OL_D_ID", c_did))
            .and(QueryBuilder.eq("OL_O_ID", oid)));

        Iterator<Row> it = orderlines.iterator();
        while(it.hasNext()){
            Row currOL = it.next();
            System.out.println("OL_I_ID: " + currOL.getInt("OL_I_ID"));
            System.out.println("OL_SUPPLY_W_ID: " + currOL.getInt("OL_SUPPLY_W_ID"));
            System.out.println("OL_QUANTITY:"  + currOL.getDecimal("OL_QUANTITY"));
            System.out.println("OL_AMOUNT:"  + currOL.getDecimal("OL_AMOUNT"));
            System.out.println("OL_DELIVERY_D:"  + currOL.getTimestamp("OL_DELIVERY_D"));
        }
    }
    public void popularItem(int wid, int did, int L)throws TransactionException{

        Session s = cn.connect();
        Wrapper w = new Wrapper(s);
        Row tmp = s.execute(QueryBuilder
                .select().all()
                .from(Connector.keyspace, "district")
                .where(QueryBuilder.eq("D_W_ID", wid))
                .and(QueryBuilder.eq("D_ID", did))
                .allowFiltering()).one();
        int N = tmp.getInt("D_NEXT_O_ID");
        System.out.println("Hier:" + N);
        //N = 3003;
        ResultSet tmp2 = s.execute(QueryBuilder
                .select().all()
                .from(Connector.keyspace, "orders")
                .where(QueryBuilder.eq("O_D_ID",did))
                .and(QueryBuilder.eq("O_W_ID",wid))
                .and(QueryBuilder.gte("O_ID",N-L))
                .and(QueryBuilder.lte("O_ID",N))
        );
        System.out.println("District:  W_ID = " + wid + " D_ID = " + did);
        System.out.println("Number of last order to be examined: " + L);
        Iterator<Row> it = tmp2.iterator();
        BigDecimal max = BigDecimal.ZERO;
        while(it.hasNext()){
            Row tmp3 = it.next();
            BigDecimal tmp4 = s.execute(QueryBuilder
                    .select().max("OL_QUANTITY")
                    .from(Connector.keyspace, "order_line")
                    .where(QueryBuilder.eq("OL_D_ID",did))
                    .and(QueryBuilder.eq("OL_W_ID",wid))
                    .and(QueryBuilder.gte("OL_O_ID",tmp3.getInt("O_ID")))

            ).one().getDecimal(0);
            if (max.compareTo(tmp4)==-1)
                max = tmp4;
        }/*
        while(it.hasNext()) {
            Row tmp3 = it.next();
            System.out.print("Order Number: " + tmp3.getInt("O_ID") + " entry date and time: " + tmp3.getTimestamp("O_ENTRY_D"));
        }*/
        System.out.println("Max: " + max);

    }
}