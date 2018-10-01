package app;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;

public class Transaction {

    private static Session s;
    private static Wrapper w;
    public static void set(Session s){
        Transaction.s = s;
        w = new Wrapper(s);
    }

    public static void handleInput() {
        while(sc.hasNext()){
            fs.get((char) sc.nextByte()).run();
        }
    }

    //Task 4 output DB state:
    public static void handleOutput(){
       for(Select q : outputQueries){
            Row r = s.execute(q).one();
            System.out.println(r.getColumnDefinitions());
            System.out.println(r + "\n");
       }
    }

    //Transaction 1
    private static void newOrder(int wid, int did, int cid, List<Integer> ids, List<Integer> wids, List<Integer> quantities ) throws TransactionException{
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

    //Transaction 5
    private static long stockLevel(int wid, int did, BigDecimal t, int l) throws TransactionException{
        //Step 1
        int N = w.findDistrict(wid, did, "D_NEXT_O_ID")
            .orElseThrow(() -> new TransactionException(String.format("Unable to find district with W_ID = %d and D_ID = %d", wid, did)))
            .getInt(0);

        //Step 2
        List<Integer> itemids = s.execute(QueryBuilder
                .select("OL_I_ID")
                .from("order_line")
                .where(QueryBuilder.eq("OL_W_ID", wid))
                .and(QueryBuilder.eq("OL_D_ID", did))
                .and(QueryBuilder.gt("OL_O_ID", N-l))
                .and(QueryBuilder.lte("OL_O_ID", N))
            ).all().stream().mapToInt(r -> r.getInt(0)).boxed().collect(Collectors.toList());

        //Step 3
        return s.execute(QueryBuilder
            .select(QueryBuilder.count("S_I_ID"))
            .from("stock")
            .where(QueryBuilder.eq("S_W_ID", wid))
            .and(QueryBuilder.in("S_I_ID", itemids))
            .and(QueryBuilder.lt("S_QUANTITY", t))
        ).one().getLong(0);
    }

    //Transaction 7
    private static void topBalance(){
        List<Row> rows = new ArrayList<>(100);
        //processing: step 1
        IntStream.rangeClosed(1, 10).forEach(i -> {
            rows.addAll(
                s.execute(QueryBuilder
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
                w.findCustomer(r.getInt("C_W_ID"), r.getInt("C_D_ID"), r.getInt("C_ID"), "C_FIRST", "C_MIDDLE", "C_LAST")
                    .ifPresent(c -> System.out.printf("Name: %s %s %s, ", c.getString(0), c.getString(1), c.getString(2)));

                //b
                System.out.printf("Balance: %s, ", r.getDecimal("C_BALANCE"));

                //c
                w.findWarehouse(r.getInt("C_W_ID"), "W_NAME").ifPresent(c -> System.out.printf("Warehouse name: %s, ", c.getString(0)));

                //d
                w.findDistrict(r.getInt("C_W_ID"), r.getInt("C_D_ID"), "D_NAME").ifPresent(c -> System.out.printf("District name: %s\n",  c.getString(0)));

            });

    }

    //transaction 3
    private static void processDelivery(int wid, int carrierid) throws TransactionException {
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

    //Transaction 4
    private static void getOrderStatus(int c_wid, int c_did, int cid) throws TransactionException {
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
    //Transaction 2
    public static void paymentTransaction(int cwid, int cdid, int cid, BigDecimal payment) throws TransactionException {
        BigDecimal currentWarehouse = s.execute(QueryBuilder.select()
                .from(Connector.keyspace, "warehouse")
                .where(QueryBuilder.eq("W_ID", cwid))).one().getDecimal("W_YTD");

        s.execute(QueryBuilder.update(Connector.keyspace, "warehouse")
                .with(QueryBuilder.set("W_YTD", payment.add(currentWarehouse)))
                .where(QueryBuilder.eq("W_ID", cwid))
        );
        BigDecimal currentDistrict = s.execute(QueryBuilder.select()
                .from(Connector.keyspace, "district")
                .where(QueryBuilder.eq("D_W_ID", cwid))
                .and(QueryBuilder.eq("D_ID",cdid))).one().getDecimal("D_YTD");
        s.execute(QueryBuilder.update(Connector.keyspace, "district")
                .with(QueryBuilder.set("D_YTD", payment.add(currentDistrict)))
                .where(QueryBuilder.eq("D_W_ID", cwid))
                .and(QueryBuilder.eq("D_ID",cdid))
        );
        Row C = w.findCustomer(cwid, cdid, cid).orElseThrow(() -> new TransactionException("Unable to find customer with id:" + cid));
        s.execute(QueryBuilder.update(Connector.keyspace, "customer")
                .with(QueryBuilder.set("C_BALANCE", C.getDecimal("C_BALANCE").subtract(payment)))
                .and(QueryBuilder.set("C_YTD_PAYMENT", C.getFloat("C_YTD_PAYMENT")+payment.floatValue()))
                .and(QueryBuilder.set("C_PAYMENT_CNT",C.getInt("C_PAYMENT_CNT")+1))
                .where(QueryBuilder.eq("C_W_ID", cwid))
                .and(QueryBuilder.eq("C_D_ID",cdid))
                .and(QueryBuilder.eq("C_ID",cid))
        );
        Row wa = w.findWarehouse(cwid).orElseThrow(() -> new TransactionException("Unable to find customer with id:" + cid));
        Row district = w.findDistrict(cwid,cdid).orElseThrow(() -> new TransactionException("Unable to find customer with id:" + cid));

        System.out.println("C_W_ID: " + cwid + " C_D_ID: " + cdid + " C_ID: " + cid );
        System.out.println("Name: " + C.getString("C_FIRST") + C.getString("C_MIDDLE") + C.getString("C_LAST"));
        System.out.println("Adress: " + C.getString("C_STREET_1") + C.getString("C_STREET_2") + C.getString("C_CITY")
                            + C.getString("C_STATE") + C.getString("C_ZIP") + C.getString("C_PHONE"));
        //restliche Customer informationen einfügen
        //
        //
        //
        System.out.println("Warehouse: " + wa.getString("W_STREET_1") + wa.getString("W_STREET_2") + wa.getString("W_CITY") + wa.getString("W_STATE") + wa.getString("W_ZIP"));
        System.out.println("District: " + district.getString("D_STREET_1") + district.getString("D_STREET_2") + district.getString("D_CITY") + district.getString("D_STATE") + district.getString("D_ZIP"));
        System.out.println("Payment: " + payment);
    }
    //Transaction 6
    public static void popularItem(int wid, int did, int L)throws TransactionException{
        Row tmp = s.execute(QueryBuilder
                .select().all()
                .from(Connector.keyspace, "district")
                .where(QueryBuilder.eq("D_W_ID", wid))
                .and(QueryBuilder.eq("D_ID", did))
                .allowFiltering()).one();
        int N = tmp.getInt("D_NEXT_O_ID");
        System.out.println("Hier:" + N);
        ResultSet S = s.execute(QueryBuilder
                .select().all()
                .from(Connector.keyspace, "orders")
                .where(QueryBuilder.eq("O_D_ID",did))
                .and(QueryBuilder.eq("O_W_ID",wid))
                .and(QueryBuilder.gte("O_ID",N-L))
                .and(QueryBuilder.lte("O_ID",N))
                .allowFiltering()
        );

        System.out.println("District:  W_ID = " + wid + " D_ID = " + did);
        System.out.println("Number of last order to be examined: " + L);

        Iterator<Row> it = S.iterator();
        ArrayList<Integer> orders = new ArrayList<>();
        //orderNumber -> popularItems
        HashMap<Integer, HashSet<Integer>> popularItems = new HashMap<>();
        //itemID -> Quantity
        HashMap<Integer,BigDecimal> popItemQuantity = new HashMap<>();
        while(it.hasNext()) {
            Row tmp3 = it.next();
            int O_ID = tmp3.getInt("O_ID");
            String CName = "" + tmp3.getInt("O_C_ID");
            String timeEntry = tmp3.getTimestamp("O_ENTRY_D").toString();
            //get max popularity
            BigDecimal max = s.execute(QueryBuilder
                    .select().max("OL_QUANTITY")
                    .from(Connector.keyspace, "order_line")
                    .where(QueryBuilder.eq("OL_D_ID", did))
                    .and(QueryBuilder.eq("OL_W_ID", wid))
                    .and(QueryBuilder.gte("OL_O_ID", tmp3.getInt("O_ID")))
                    .allowFiltering()
            ).one().getDecimal(0);
            order p = new order(O_ID,timeEntry,CName,null);
            if(max == null)
                max = BigDecimal.valueOf(0);
            ResultSet popItems = s.execute(QueryBuilder
                    .select().all()
                    .from(Connector.keyspace, "order_line")
                    .where(QueryBuilder.eq("OL_D_ID", did))
                    .and(QueryBuilder.eq("OL_W_ID", wid))
                    .and(QueryBuilder.gte("OL_O_ID", tmp3.getInt("O_ID")))
                    .and(QueryBuilder.eq("OL_QUANTITY", max))
                    .allowFiltering()
            );
            Iterator<Row> it2 = popItems.iterator();
            HashSet<Integer> item = new HashSet<>();
            while(it2.hasNext()){
                Row popItem = it2.next();
                item.add(popItem.getInt("OL_I_ID"));
                popItemQuantity.put(popItem.getInt("OL_I_ID"),popItem.getDecimal("OL_QUANTITY"));
            }
            //get just popular items
            popularItems.put(p.O_ID,item);
            orders.add(O_ID);
        }
        //berechnung andern, namen von item und customer holenSSSS
        for(Integer o : orders ){
            Row Order = w.findOrder(wid,did,o).orElseThrow(() -> new TransactionException("Unable to find Order with id:" + o));
            System.out.println("Order ID: " + o + " Date " + Order.getTimestamp("O_ENTRY_D"));
            System.out.println("CName: " + Order.getInt("O_C_ID"));
            for(Integer i : popularItems.get(o)){
                System.out.println("Popular Item: " + i + " Quantity: " + popItemQuantity.get(i));
                int counter = 0;
                for(Integer t : orders){
                    if(popularItems.get(t).contains(i))
                        counter++;
                }
                System.out.println(100*(float)counter / (float)orders.size());

            }

        }
    }

    private static final Scanner sc = new Scanner(System.in);
    private static final Map<Character, Runnable> fs = new HashMap<>();
    static {
        //Transaction (a)
        fs.put('N', () -> {
            int cid = sc.nextInt();
            int wid = sc.nextInt();
            int did = sc.nextInt();
            int m = sc.nextInt();
            List<Integer> ids = new ArrayList<>(m);
            List<Integer> wids = new ArrayList<>(m);
            List<Integer> quantities = new ArrayList<>(m);
            for(int i = 0; i < m; i++){
                ids.add(i, sc.nextInt());
                wids.add(i, sc.nextInt());
                quantities.add(i, sc.nextInt());
            }
            try { newOrder(wid, did, cid, ids, wids, quantities); }
            catch(Exception e){ System.out.println("Unable to perform newOrder transaction failed with code: " + e); }
        });

        //Transaction (b)
        fs.put('P', () -> {
            int wid = sc.nextInt();
            int did = sc.nextInt();
            int cid = sc.nextInt();
            BigDecimal payment = sc.nextBigDecimal();
            //TODO: implement payment
            // t.payment(wid, did, cid, payment);
        });

        //Transaction (c)
        fs.put('D', () -> {
            int wid = sc.nextInt();
            int carrierid = sc.nextInt();
            try{ processDelivery(wid, carrierid); }
            catch(Exception e) { System.out.println("Unable to perform Delivery transaction transaction failed with code: " + e); }
        });

        //Transaction (d)
        fs.put('O', () -> {
            int wid = sc.nextInt();
            int did = sc.nextInt();
            int cid = sc.nextInt();
            try{ getOrderStatus(wid, did, cid); }
            catch(Exception e) { System.out.println("Unable to perform Order-Status transaction failed with code: " + e); }
        });

        //Transaction (e)
        fs.put('S', () -> {
            int wid = sc.nextInt();
            int did = sc.nextInt();
            BigDecimal T = sc.nextBigDecimal();
            int L = sc.nextInt();
            try{ stockLevel(wid, did, T, L); }
            catch(Exception e) { System.out.println("Unable to perform Stock-Level transaction, failed with code: " + e); }
        });

        //Transaction (f)
        fs.put('I', () -> {
            int wid = sc.nextInt();
            int did = sc.nextInt();
            int L = sc.nextInt();
            try{ popularItem(wid, did, L); }
            catch(Exception e) { System.out.println("Unable to perform Popular-Item transaction, failed with code: " + e); }
        });

        //Transaction (g)
        fs.put('T', () -> {
            topBalance();
        });

        //Transaction (h)
        fs.put('R', () -> {
            int wid = sc.nextInt();
            int did = sc.nextInt();
            int cid = sc.nextInt();
            //TODO: implement Related-Customer Transaction
            //t.relatedCustomer(wid, did, cid);
        });
    }

    private static Select outputQueries[] = new Select[] {
        //4.a
        QueryBuilder
            .select(QueryBuilder.sum("W_YTS"))
            .from("warehouse"),

        //4.b
        QueryBuilder
            .select(QueryBuilder.sum("D_YTD"), QueryBuilder.sum("D_NEXT_O_ID"))
            .from("district"),

        //4.c        
        QueryBuilder
            .select(QueryBuilder.sum("C_BALANCE"), QueryBuilder.sum("C_YTD_PAYMENT"), QueryBuilder.sum("C_PAYMENT_CNT"), QueryBuilder.sum("C_DELIVERY_CNT"))
            .from("customer"),
        
        //4.d
        QueryBuilder
                .select(QueryBuilder.max("O_ID"), QueryBuilder.sum("O_OL_CNT"))
                .from("orders"),
            
        //4.e
        QueryBuilder
                .select(QueryBuilder.sum("OL_AMOUNT"), QueryBuilder.sum("OL_QUANTITY"))
                .from("order_line"),
            
        //4.f
        QueryBuilder
                .select(QueryBuilder.sum("S_QUANTITY"), QueryBuilder.sum("S_YTD"), QueryBuilder.sum("S_ORDER_CNT"))
                .from("stock")
    };
}