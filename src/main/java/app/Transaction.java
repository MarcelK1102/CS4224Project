package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;

import app.wrapper.customer;
import app.wrapper.district;
import app.wrapper.item;
import app.wrapper.order_line;
import app.wrapper.orders;
import app.wrapper.stock;
import app.wrapper.warehouse;

public class Transaction {
    public static int nxact = 0;
    public static long startTime;
    public static long endTime;
    //Task 2 take input
    public static void handleInput() {
        try{
            char []buf = new char[2];
            startTime = System.currentTimeMillis();
            while(bi.read(buf, 0, 2) > 0){
                System.out.println(buf[0]);
                fs.get(buf[0]).run();
                nxact++;
            }
            endTime = System.currentTimeMillis();
        }catch(IOException ioe){System.out.println("Unable to handle input: "); ioe.printStackTrace();}
        System.out.println("Ended input");
    }

    //Transaction 1
    private static void newOrder(int wid, int did, int cid, List<Integer> ids, List<Integer> wids, List<Integer> quantities ) throws NoSuchElementException{
        //Processing
        //Step 1
        district d = new district(wid, did, "D_NEXT_O_ID");
        int N; 
        
        //https://stackoverflow.com/questions/3935915/how-to-create-auto-increment-ids-in-cassandra/29391877#29391877
        //Step 2
        do{
            N = d.nextoid() + 1;
            d.set_nextoid(N);
        } while(!d.update(QueryBuilder.eq("D_NEXT_O_ID", N-1)));

        //Step 3
        Connector.s.execute((QueryBuilder.insertInto("orders")
                .value("O_ID", N)
                .value("O_D_ID", did)
                .value("O_W_ID ", wid) 
                .value("O_C_ID", cid) 
                .value("O_ENTRY_D", Date.from(Instant.now()))
                .value("O_OL_CNT", BigDecimal.valueOf( ids.size())) 
                .value("O_ALL_LOCAL", BigDecimal.valueOf(wids.stream().allMatch(i -> i == wid) ? 1 : 0))
        ));

        //Step 4
        BigDecimal totalAmount = BigDecimal.ZERO, oldquantity;
        
        
        //Step 5
        for(int i = 0; i < ids.size(); i++){
            int iid = ids.get(i);
            //step a
            stock stk = new stock(wid, iid,  "S_QUANTITY"," S_YTD"," S_ORDER_CNT"," S_REMOTE_CNT"," S_DIST_" + String.format("%02d", did)); 
            do{
                //Step b
                oldquantity = stk.quantity();
                stk.set_quantity(oldquantity.subtract(BigDecimal.valueOf(quantities.get(i))));
                
                //Step c
                stk.set_quantity(stk.quantity().add(BigDecimal.valueOf(stk.quantity().doubleValue() < 10.0 ? 100.0 : 0.0)));

                //Step d  
                stk.set_ytd(stk.ytd().add(BigDecimal.valueOf(quantities.get(i))));
                stk.set_ordercnt(stk.ordercnt() + 1);
                stk.set_remotecnt(stk.remotecnt() + (wid != wids.get(i) ? 1 : 0));
            } while(!stk.update(QueryBuilder.eq("S_QUANTITY", oldquantity)));
            //Step e
            BigDecimal itemAmount = new item(iid, "I_PRICE").price();
            
            //Step f
            totalAmount = totalAmount.add(itemAmount);
            
            //Step g
            Connector.s.execute(QueryBuilder.insertInto("order_line")
                .value("OL_O_ID", N)
                .value("OL_D_ID", did)
                .value("OL_W_ID", wid)
                .value("OL_NUMBER", i + 1)
                .value("OL_I_ID", iid)
                .value("OL_SUPPLY_W_ID", wids.get(i))
                .value("OL_QUANTITY", quantities.get(i))
                .value("OL_AMOUNT", itemAmount)
                .value("OL_DIST_INFO","S_DIST_" + String.format("%02d", did))
            );
        }

        //Step 6
        warehouse w = new warehouse(wid, "W_TAX");
        d = new district(wid, did, "D_TAX");
        customer c = new customer(wid, did, cid, "C_W_ID" , "C_W_ID", "C_ID", "C_LAST", "C_CREDIT", "C_DISCOUNT");
        totalAmount = totalAmount.multiply(BigDecimal.ONE.add(d.tax()).add(w.tax())).multiply(BigDecimal.ONE.subtract(c.discount()));
        
        //Output:
        //Step 1
        System.out.println(c);

        //Step 2
        System.out.println(w);
        System.out.println(d);

        //Step 3
        System.out.println(new orders(wid, did, N, "O_ID", "O_ENTRY_D"));

        //Step 4
        System.out.println("num_items : " + ids.size() + ", total_amount : " + totalAmount);

        //Step 5
        for(int i = 0; i < ids.size(); i++){
            System.out.println(new item(ids.get(i), "I_NAME"));
            System.out.println(new order_line(wid, did, N, i+1, "OL_AMOUNT"));
            System.out.println(new stock(wid, ids.get(i), "S_QUANTITY"));
        }
    }
    //Transaction 2
    public static void paymentTransaction(int cwid, int cdid, int cid, BigDecimal payment) {
        warehouse w = new warehouse(cwid,"W_STREET_1","W_STREET_2","W_CITY","W_STATE","W_ZIP");
        BigDecimal oldYtd, oldBal;
        Float oldYtdPayment;
        Integer oldPayment;
        w.tax(); 
        //Step 1      
        do {
            oldYtd = w.ytd();
            w.set_ytd(payment.add(w.ytd()));
        } while(!w.update(QueryBuilder.eq("W_YTD", oldYtd)));

        district d = new district(cwid, cdid,"D_STREET_1","D_STREET_2","D_CITY","D_STATE","D_ZIP");
        
        //Step 2
        do{
            oldYtd = d.ytd();
            d.set_ytd(payment.add(oldYtd));
        } while(!d.update(QueryBuilder.eq("D_YTD", oldYtd)));

        //Step 3
        customer c = new customer(cwid, cdid, cid,"C_FIRST","C_MIDDLE","C_LAST","C_STREET_1","C_STREET_2","C_CITY","C_STATE","C_ZIP","C_PHONE","C_SINCE","C_CREDIT","C_CREDIT_LIM","C_DISCOUNT","C_BALANCE" );
        
        do {
            oldYtdPayment = c.ytdpayment();
            oldBal = c.balance();
            oldPayment = c.paymentcnt();
            c.set_balance(oldBal.subtract(payment));
            c.set_ytdpayment(oldYtdPayment + payment.floatValue());
            c.set_paymentcnt(oldPayment + 1);
        } while(!c.update(QueryBuilder.eq("C_BALANCE", oldBal), QueryBuilder.eq("C_YTD_PAYMENT", oldYtdPayment), QueryBuilder.eq("C_PAYMENT_CNT", oldPayment)));
        
        System.out.println(w);
        System.out.println(d);
        System.out.println(c);
        System.out.println("Payment: " + payment);
    }
    //Transaction 3
    public static void processDelivery(int wid, int carrierid) {
        for (int districtNo = 1; districtNo <= 10; districtNo++){
            
            ArrayList<Integer> oids = new ArrayList<>();

            ResultSet S = Connector.s.execute(QueryBuilder
            .select().all()
            .from(Connector.keyspace, "orders")
            .where(QueryBuilder.eq("O_W_ID", wid))
            .and(QueryBuilder.eq("O_D_ID", districtNo)));

            Iterator<Row> it = S.iterator();
            while(it.hasNext()){
                Row curr = it.next();
                if (curr.getInt("O_CARRIER_ID")==0){
                    oids.add(curr.getInt("O_ID"));
                }
            }
            int N;
            if (!oids.isEmpty())
                N = Collections.min(oids);
            else
                continue;

            Row X;
            try {
                X = Wrapper.findOrder(wid, districtNo, N);
            } catch (NullPointerException e) {
                //skip if there is no order with id N
                continue;
            }
            int cid = X.getInt("O_C_ID");
            customer c = new customer(wid, districtNo, cid);
            // Row C = Wrapper.findCustomer(wid, districtNo, cid);

            //b)
            Connector.s.execute(QueryBuilder.update(Connector.keyspace, "orders")
            .with(QueryBuilder.set("O_CARRIER_ID", carrierid))
            .where(QueryBuilder.eq("O_W_ID", wid))
            .and(QueryBuilder.eq("O_D_ID", districtNo))
            .and(QueryBuilder.eq("O_ID", N)));

            //c)
            ResultSet orderlines = Connector.s.execute(QueryBuilder.select().all()
            .from(Connector.keyspace, "order_line")
            .where(QueryBuilder.eq("OL_W_ID",wid))
            .and(QueryBuilder.eq("OL_D_ID", districtNo))
            .and(QueryBuilder.eq("OL_O_ID", N)));

            Iterator<Row> it2 = orderlines.iterator();
            while(it2.hasNext()){
                Row currOL = it2.next();
                int OL_Number = currOL.getInt("OL_NUMBER");

                Connector.s.execute(QueryBuilder.update(Connector.keyspace, "order_line")
                .with(QueryBuilder.set("OL_DELIVERY_D", Date.from(Instant.now())))
                .where(QueryBuilder.eq("OL_W_ID",wid))
                .and(QueryBuilder.eq("OL_D_ID", districtNo))
                .and(QueryBuilder.eq("OL_O_ID", N))
                .and(QueryBuilder.eq("OL_NUMBER", OL_Number)));
            }

            //d)
            int old_cnt = c.deliverycnt();
            BigDecimal c_balance = c.balance();


            BigDecimal B = Connector.s.execute(QueryBuilder.select()
            .sum("OL_AMOUNT")
            .from("order_line")
            .where(QueryBuilder.eq("OL_W_ID",wid))
            .and(QueryBuilder.eq("OL_O_ID", N))
            .and(QueryBuilder.eq("OL_D_ID", districtNo))
            ).one().getDecimal(0);

            do{
                c_balance = c.balance();
                old_cnt = c.deliverycnt();
                c.set_balance(c_balance.add(B));
                c.set_deliverycnt(old_cnt + 1);
            } while(!c.update(QueryBuilder.eq("C_DELIVER_CNT", old_cnt)));
        }
    }
    //Transaction 4
    public static void getOrderStatus(int c_wid, int c_did, int cid) throws InvalidKeyException{
        //1.
        Row C = Wrapper.findCustomer(c_wid, c_did, cid);
        System.out.println(String.format("Name: %s %s %s",C.getString("C_FIRST"), C.getString("C_MIDDLE"), C.getString("C_LAST")));
        System.out.println("Balance: " + C.getDecimal("C_BALANCE"));

        //2.
        java.util.Date lastOrderDate = Connector.s.execute(QueryBuilder.select()
        .min("O_ENTRY_D")
        .from(Connector.keyspace, "orders")
        .where(QueryBuilder.eq("O_W_ID", c_wid))
        .and(QueryBuilder.eq("O_D_ID", c_did))
        .and(QueryBuilder.eq("O_C_ID", cid))
        .allowFiltering()).one().getTimestamp(0);
        if (lastOrderDate == null){
            throw new InvalidKeyException("No Order with valid timestamp found");
        }

        //get Order Row
        Row lastOrder = Connector.s.execute(QueryBuilder.select().all()
        .from(Connector.keyspace, "orders")
        .where(QueryBuilder.eq("O_W_ID", c_wid))
        .and(QueryBuilder.eq("O_D_ID", c_did))
        .and(QueryBuilder.eq("O_ENTRY_D", lastOrderDate))
        .allowFiltering()).one();

        int oid = lastOrder.getInt("O_ID");
        System.out.println("O_ID: " + oid);
        System.out.println("O_ENTRY_D: " + lastOrder.getTimestamp("O_ENTRY_D"));
        System.out.println("O_CARRIER_ID: " + lastOrder.getInt("O_CARRIER_ID"));

        //3.
        ResultSet orderlines = Connector.s.execute(QueryBuilder.select().all()
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
    //Transaction 5
    private static void stockLevel(int wid, int did, BigDecimal t, int l) {
        //Step 1
        int N = Wrapper.findDistrict(wid, did, "D_NEXT_O_ID").getInt(0);

        //Step 2
        List<Integer> itemids = Connector.s.execute(QueryBuilder
                .select("OL_I_ID")
                .from("order_line")
                .where(QueryBuilder.eq("OL_W_ID", wid))
                .and(QueryBuilder.eq("OL_D_ID", did))
                .and(QueryBuilder.gt("OL_O_ID", N-l))
                .and(QueryBuilder.lte("OL_O_ID", N))
            ).all().stream().mapToInt(r -> r.getInt(0)).boxed().collect(Collectors.toList());

        //Step 3
        System.out.println(Connector.s.execute(QueryBuilder
            .select(QueryBuilder.count("S_I_ID"))
            .from("stock")
            .where(QueryBuilder.eq("S_W_ID", wid))
            .and(QueryBuilder.in("S_I_ID", itemids))
            .and(QueryBuilder.lt("S_QUANTITY", t))
        ).one());
    }
    //Transaction 6
    public static void popularItem(int wid, int did, int L) {
        //Row district = Wrapper.findDistrict(wid,did).orElseThrow(() -> new TransactionException("Unable to find customer with id:" + wid));
        //P:Step 1
        Row district = Connector.s.execute(QueryBuilder
                .select().all()
                .from(Connector.keyspace, "district")
                .where(QueryBuilder.eq("D_ID",did))
                .and(QueryBuilder.eq("D_W_ID",wid)))
                .one();
        int N = district.getInt("D_NEXT_O_ID");
        
        //P:Step 2
        ResultSet S = Connector.s.execute(QueryBuilder
                .select().all()
                .from(Connector.keyspace, "orders")
                .where(QueryBuilder.eq("O_D_ID",did))
                .and(QueryBuilder.eq("O_W_ID",wid))
                .and(QueryBuilder.gte("O_ID",N-L))
                .and(QueryBuilder.lte("O_ID",N))
        );

        //O:step 1
        System.out.println("District:  W_ID = " + wid + " D_ID = " + did);
        System.out.println("Number of last order to be examined: " + L);

        //P:Step 3
        Iterator<Row> it = S.iterator();
        ArrayList<Integer> orders = new ArrayList<>();
        //orderNumber -> allItems
        HashMap<Integer, HashSet<Integer>> allItems = new HashMap<>();
        //orderNumber -> popularItems
        HashMap<Integer, HashSet<Integer>> popularItems = new HashMap<>();
        //itemID -> Quantity
        HashMap<Pair,BigDecimal> popItemQuantity = new HashMap<>();
        while(it.hasNext()) {
            Row currentOrder = it.next();
            int O_ID = currentOrder.getInt("O_ID");

            //get max popularity
            BigDecimal max = Connector.s.execute(QueryBuilder
                    .select().max("OL_QUANTITY")
                    .from(Connector.keyspace, "order_line")
                    .where(QueryBuilder.eq("OL_D_ID", did))
                    .and(QueryBuilder.eq("OL_W_ID", wid))
                    .and(QueryBuilder.eq("OL_O_ID", O_ID))
                    .allowFiltering()
            ).one().getDecimal(0);
            if(max == null) {
                max = BigDecimal.valueOf(0);
            }

            //"Select * from order_line_by_quantity where ..., QUANITYT >= MAX limit L" 

            ResultSet Items = Connector.s.execute(QueryBuilder
                    .select().all()
                    .from(Connector.keyspace, "order_line")
                    .where(QueryBuilder.eq("OL_D_ID", did))
                    .and(QueryBuilder.eq("OL_W_ID", wid))
                    .and(QueryBuilder.eq("OL_O_ID", O_ID))
                    .allowFiltering()
            );

            Iterator<Row> it2 = Items.iterator();
            HashSet<Integer> items = new HashSet<>();
            HashSet<Integer> popItems = new HashSet<>();
            while(it2.hasNext()){
                Row Item = it2.next();
                items.add(Item.getInt("OL_I_ID"));
                if(Item.getDecimal("OL_QUANTITY").equals(max)) {
                    popItemQuantity.put(new Pair(O_ID,Item.getInt("OL_I_ID")),Item.getDecimal("OL_QUANTITY"));
                    popItems.add(Item.getInt("OL_I_ID"));
                }
            }
            //get just popular items
            allItems.put(O_ID,items);
            popularItems.put(O_ID,popItems);
            orders.add(O_ID);
        }
        //O:step 3+4
        for(Integer o : orders ){
            //3.a
        
            Row Order = Wrapper.findOrder(wid,did,o);
            
            System.out.println("Order ID: " + o + " Date " + Order.getTimestamp("O_ENTRY_D"));
            //3.b
            Row Customer = Wrapper.findCustomer(wid,did,Order.getInt("O_C_ID"));
            System.out.println("CName: " + Customer.getString("C_FIRST") + " " + Customer.getString("C_MIDDLE") + " " +Customer.getString("C_LAST"));
            for(Integer i : popularItems.get(o)){
                //4.a
                Row I = Wrapper.findItem(i);
                System.out.println("Popular Item: " + I.getString("I_NAME") + " Quantity: " + popItemQuantity.get(new Pair(o,i)));
                int counter = 0;
                for(Integer t : orders){
                    if(allItems.get(t)==null)
                    continue;
                    if(allItems.get(t).contains(i))
                    counter++;
                }
                System.out.println(100*(float)counter / (float)orders.size());

            }

        }
    }
    //Transaction 7
    private static void topBalance(){
        List<Row> rows = new ArrayList<>(100);
        //processing: step 1
        IntStream.rangeClosed(1, 10).forEach(i -> {
            rows.addAll(
                Connector.s.execute(QueryBuilder
                    .select()
                    .from("customer_by_balance")
                    .where(QueryBuilder.eq("C_D_ID", i))
                    .limit(10)
                ).all()
            );
        });

        //Output: step 1
        rows.stream()
            .sorted((r1, r2) -> r2.getDecimal("C_BALANCE").compareTo(r1.getDecimal("C_BALANCE")))
            .limit(10)
            .forEach(r ->{
                //a
                Row c = Wrapper.findCustomer(r.getInt("C_W_ID"), r.getInt("C_D_ID"), r.getInt("C_ID"), "C_FIRST", "C_MIDDLE", "C_LAST");
                System.out.printf("Name: %s %s %s, ", c.getString(0), c.getString(1), c.getString(2));

                //b
                System.out.printf("Balance: %s, ", r.getDecimal("C_BALANCE"));

                //c
                c = Wrapper.findWarehouse(r.getInt("C_W_ID"), "W_NAME");
                System.out.printf("Warehouse name: %s, ", c.getString(0));

                //d
                c = Wrapper.findDistrict(r.getInt("C_W_ID"), r.getInt("C_D_ID"), "D_NAME");
                System.out.printf("District name: %s\n",  c.getString(0));

            });

    }
    
    //Transaction 8
    public static void relatedCustomer(int cwid, int cdid, int cid) {
        //Find all tuples in orders for this customer
        System.out.println("C_W_ID: " + cwid + " C_D_ID: " + cdid + " C_ID: "+ cid);

        Iterator<Row> orders = Connector.s.execute(QueryBuilder.select().all()
            .from("order_by_customer")
            .where(QueryBuilder.eq("O_C_ID", cid))
            .and(QueryBuilder.eq("O_W_ID", cwid))
            .and(QueryBuilder.eq("O_D_ID", cdid))).iterator();

        while(orders.hasNext()){
            Row order = orders.next();
            //Find all items for that order
            Iterator<Row> items = Connector.s.execute(QueryBuilder.select("OL_I_ID")
                .from("order_line")
                .where(QueryBuilder.eq("OL_W_ID", cwid))
                .and(QueryBuilder.eq("OL_D_ID", cdid))
                .and(QueryBuilder.eq("OL_O_ID", order.getInt("O_ID")))
            ).iterator();
            Map<Integer, Pair> oids = new HashMap<>();
            while(items.hasNext()){
                Row item = items.next();
                int itemId = item.getInt(0);
                // System.out.println(item);
                //for this item we need to find OL_O_ID that has the same item
                List<Iterator<Row>> otherorders = new ArrayList<Iterator<Row>>();
                otherorders.add(Connector.s.execute(QueryBuilder.select("OL_O_ID", "OL_W_ID", "OL_D_ID")
                .from("order_line_by_item")
                .where(QueryBuilder.eq("OL_D_ID", cdid))
                .and(QueryBuilder.eq("OL_I_ID", item.getInt("OL_I_ID")))
                .and(QueryBuilder.gt("OL_W_ID", cwid))
                ).iterator());
                otherorders.add(Connector.s.execute(QueryBuilder.select("OL_O_ID")
                    .from("order_line_by_item")
                    .where(QueryBuilder.eq("OL_D_ID", cdid))
                    .and(QueryBuilder.eq("OL_I_ID", item.getInt("OL_I_ID")))
                    .and(QueryBuilder.lt("OL_W_ID", cwid))
                    ).iterator());
                for(Iterator<Row> otherorder : otherorders){
                    while(otherorder.hasNext()){
                        Row orderO = otherorder.next();
                        Integer oloid = orderO.getInt("OL_O_ID");
                        if(!oids.containsKey(oloid))
                            oids.put(oloid, new Pair(itemId, -1));
                        else {
                            Pair p = oids.get(oloid);
                            if(p.a != itemId && p.b < 0){
                                p.b = itemId;
                                System.out.println("C_ID: " + Connector.s.execute(QueryBuilder.select("O_C_ID")
                                    .from("orders")
                                    .where(QueryBuilder.eq("O_W_ID", orderO.getInt("OL_W_ID")))
                                    .and(QueryBuilder.eq("O_D_ID", orderO.getInt("OL_D_ID")))
                                    .and(QueryBuilder.eq("O_ID", oloid))).one().getInt(0));
                            }
                        }
                    }
                }
            }
        }
    }

    private static BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));
    private static final Map<Character, Runnable> fs = new HashMap<>();
    static {
        //Transaction (a)
        fs.put('N', () -> {
            String []input;
            try{input = bi.readLine().split(",");} catch(IOException ioe) {ioe.printStackTrace(); return;}
            int cid = Integer.parseInt(input[0]);
            int wid = Integer.parseInt(input[1]);
            int did = Integer.parseInt(input[2]);
            int m = Integer.parseInt(input[3]);
            List<Integer> ids = new ArrayList<>(m);
            List<Integer> wids = new ArrayList<>(m);
            List<Integer> quantities = new ArrayList<>(m);
            for(int i = 0; i < m; i++){
                try{input = bi.readLine().split(",");} catch(IOException ioe) {ioe.printStackTrace(); return;}
                ids.add(i, Integer.parseInt(input[0]));
                wids.add(i, Integer.parseInt(input[1]));
                quantities.add(i, Integer.parseInt(input[2]));
            }
            try { newOrder(wid, did, cid, ids, wids, quantities); }
            catch(Exception e){ System.out.println("Unable to perform newOrder transaction failed with code: " + e); }
        });

        // //Transaction (b)
        fs.put('P', () -> {
            String []input;
            try{input = bi.readLine().split(",");} catch(IOException ioe) {ioe.printStackTrace(); return;}
            int wid = Integer.parseInt(input[0]);
            int did = Integer.parseInt(input[1]);
            int cid = Integer.parseInt(input[2]);
            BigDecimal payment = new BigDecimal(input[3]);
            try { paymentTransaction(wid, did, cid, payment); }
            catch(Exception e){ System.out.println("Unable to perform payment transaction failed with code: " + e); }
        });

        // //Transaction (c)
        fs.put('D', () -> {
            String []input;
            try{input = bi.readLine().split(",");} catch(IOException ioe) {ioe.printStackTrace(); return;}
            int wid = Integer.parseInt(input[0]);
            int carrierid = Integer.parseInt(input[1]);
            try{ processDelivery(wid, carrierid); }
            catch(Exception e) { System.out.println("Unable to perform Delivery transaction transaction failed with code: " + e); }
        });

        // //Transaction (d)
        fs.put('O', () -> {
            String []input;
            try{input = bi.readLine().split(",");} catch(IOException ioe) {ioe.printStackTrace(); return;}
            int wid = Integer.parseInt(input[0]);
            int did = Integer.parseInt(input[1]);
            int cid = Integer.parseInt(input[2]);
            try{ getOrderStatus(wid, did, cid); }
            catch(Exception e) { System.out.println("Unable to perform Order-Status transaction failed with code: " + e); }
        });

        // //Transaction (e)
        fs.put('S', () -> {
            String []input;
            try{input = bi.readLine().split(",");} catch(IOException ioe) {ioe.printStackTrace(); return;}
            int wid = Integer.parseInt(input[0]);
            int did = Integer.parseInt(input[1]);
            BigDecimal T = new BigDecimal(input[2]);
            int L = Integer.parseInt(input[3]);
            try{ stockLevel(wid, did, T, L); }
            catch(Exception e) { System.out.println("Unable to perform Stock-Level transaction, failed with code: " + e); }
        });

        // //Transaction (f)
        fs.put('I', () -> {
            String []input;
            try{input = bi.readLine().split(",");} catch(IOException ioe) {ioe.printStackTrace(); return;}
            int wid = Integer.parseInt(input[0]);
            int did = Integer.parseInt(input[1]);
            int L = Integer.parseInt(input[2]);
            try{ popularItem(wid, did, L); }
            catch(Exception e) { System.out.println("Unable to perform Popular-Item transaction, failed with code: " + e); }
        });

        // //Transaction (g)
        fs.put('T', () -> {
            topBalance();
        });

        // //Transaction (h)
        fs.put('R', () -> {
            String []input;
            try{input = bi.readLine().split(",");} catch(IOException ioe) {ioe.printStackTrace(); return;}
            int wid = Integer.parseInt(input[0]);
            int did = Integer.parseInt(input[1]);
            int cid = Integer.parseInt(input[2]);
            try{ relatedCustomer(wid, did, cid); }
            catch(Exception e) { System.out.println("Unable to perform Related-Customer transaction, failed with code: " + e); }
        });
    }
}
