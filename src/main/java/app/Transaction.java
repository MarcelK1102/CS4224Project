package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
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
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;

public class Transaction {

    private static Session s = Connector.s;
    private static Wrapper w;

    //Task 2 take input
    public static void handleInput() {
        try{
            char []buf = new char[2];
            while(bi.read(buf, 0, 2) > 0){
                System.out.println(buf[0]);
                fs.get(buf[0]).run();
            }
        }catch(IOException ioe){System.out.println("Unable to handle input: "); ioe.printStackTrace();}
        System.out.println("Ended input");
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
    private static void newOrder(int wid, int did, int cid, List<Integer> ids, List<Integer> wids, List<Integer> quantities ) throws NoSuchElementException{
        //Processing
        //Step 1
        Row r = w.findDistrict(wid, did, "D_NEXT_O_ID").get();
        int N;

        //https://stackoverflow.com/questions/3935915/how-to-create-auto-increment-ids-in-cassandra/29391877#29391877
        //Step 2
        do{
            N = r.getInt("D_NEXT_O_ID") + 1;
            r = s.execute(QueryBuilder
                .update("district")
                .with(QueryBuilder.set("D_NEXT_O_ID", N))
                .where(QueryBuilder.eq("D_W_ID", wid))
                .and(QueryBuilder.eq("D_ID", did))
                .onlyIf(QueryBuilder.eq("D_NEXT_O_ID", N-1))
            ).one();
        } while(!r.getBool(0));
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
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        //Step 5
        for(int i = 0; i < ids.size(); i++){
            int iid = ids.get(i);
            while(true){
                //Step a
                r = w.findStock(wid, iid, "S_QUANTITY"," S_YTD"," S_ORDER_CNT"," S_REMOTE_CNT"," S_DIST_" + String.format("%02d", did)).get();
                if(r == null){
                    System.out.println("Unable to find stock for wid:" + wid + " iID = " + iid);
                    continue;
                }
                //Step b
                BigDecimal quantity = r.getDecimal("S_QUANTITY").subtract(BigDecimal.valueOf(quantities.get(i)));
                
                //Step c
                quantity.add(BigDecimal.valueOf(quantity.doubleValue() < 10.0 ? 100.0 : 0.0));

                //Step d
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
            BigDecimal itemAmount = w.findItem(iid, "I_PRICE").get().getDecimal(0);
            
            //Step f
            totalAmount = totalAmount.add(itemAmount);
            
            //Step g
            s.execute(QueryBuilder.insertInto("order_line")
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
        BigDecimal wTax = w.findWarehouse(wid, "W_TAX").get().getDecimal(0);
        BigDecimal dTax = w.findDistrict(wid, did, "D_TAX").get().getDecimal(0);
        BigDecimal cDisc = w.findCustomer(wid, did, cid, "C_DISCOUNT").get().getDecimal(0);
        totalAmount = totalAmount.multiply(BigDecimal.ONE.add(dTax).add(wTax)).multiply(BigDecimal.ONE.subtract(cDisc));
        
        //Output:
        //Step 1
        System.out.println(w.findCustomer(wid, did, cid, "C_W_ID" , "C_W_ID", "C_ID", "C_LAST", "C_CREDIT", "C_DISCOUNT").get());

        //Step 2
        System.out.println("W_TAX: " + wTax + ", D_TAX: " + dTax);

        //Step 3
        r = w.findOrder(wid, did, N, "O_ID", "O_ENTRY_D").get();
        System.out.println(r);

        //Step 4
        System.out.println("NUM_ITEMS: " + ids.size() + ", TOTAL_AMOUNT: " + totalAmount);

        //Step 5
        for(int i = 0; i < ids.size(); i++){
            String iName = w.findItem(ids.get(i), "I_NAME").get().getString(0);
            System.out.println(iName);
            BigDecimal olAmount = w.findOrderLine(wid, did, N, i+1, "OL_AMOUNT").get().getDecimal(0);
            System.out.println(olAmount);
            BigDecimal sQuantity = w.findStock(wid, ids.get(i), "S_QUANTITY").get().getDecimal(0);
            System.out.println(sQuantity);
            System.out.printf("%d %s %d %s %s %s\n", ids.get(i), iName, wids.get(0), quantities.get(0), olAmount, sQuantity);
        }
    }

    //Transaction 5
    private static void stockLevel(int wid, int did, BigDecimal t, int l) throws TransactionException{
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
        System.out.println(s.execute(QueryBuilder
            .select(QueryBuilder.count("S_I_ID"))
            .from("stock")
            .where(QueryBuilder.eq("S_W_ID", wid))
            .and(QueryBuilder.in("S_I_ID", itemids))
            .and(QueryBuilder.lt("S_QUANTITY", t))
        ).one());
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
    public static void processDelivery(int wid, int carrierid) throws TransactionException {
        for (int districtNo = 1; districtNo <= 10; districtNo++){
            
            ArrayList<Integer> oids = new ArrayList<>();

            ResultSet S = s.execute(QueryBuilder
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
                X = w.findOrder(wid, districtNo, N)
                        .orElseThrow(() -> new TransactionException("Unable to find order with id:" + N));
            } catch (TransactionException e) {
                //skip if there is no order with id N
                continue;
            }
            int cid = X.getInt("O_C_ID");
            Row C = w.findCustomer(wid, districtNo, cid).orElseThrow(() -> new TransactionException("Unable to find customer with id:" + cid));

            //b)
            s.execute(QueryBuilder.update(Connector.keyspace, "orders")
            .with(QueryBuilder.set("O_CARRIER_ID", carrierid))
            .where(QueryBuilder.eq("O_W_ID", wid))
            .and(QueryBuilder.eq("O_D_ID", districtNo))
            .and(QueryBuilder.eq("O_ID", N)));

            //c)
            ResultSet orderlines = s.execute(QueryBuilder.select().all()
            .from(Connector.keyspace, "order_line")
            .where(QueryBuilder.eq("OL_W_ID",wid))
            .and(QueryBuilder.eq("OL_D_ID", districtNo))
            .and(QueryBuilder.eq("OL_O_ID", N)));

            Iterator<Row> it2 = orderlines.iterator();
            while(it2.hasNext()){
                Row currOL = it2.next();
                int OL_Number = currOL.getInt("OL_NUMBER");

                s.execute(QueryBuilder.update(Connector.keyspace, "order_line")
                .with(QueryBuilder.set("OL_DELIVERY_D", Date.from(Instant.now())))
                .where(QueryBuilder.eq("OL_W_ID",wid))
                .and(QueryBuilder.eq("OL_D_ID", districtNo))
                .and(QueryBuilder.eq("OL_O_ID", N))
                .and(QueryBuilder.eq("OL_NUMBER", OL_Number)));
            }

            //d)
            int delivery_cnt = C.getInt("C_DELIVERY_CNT");
            BigDecimal c_balance = C.getDecimal("C_BALANCE");

            BigDecimal B = s.execute(QueryBuilder.select()
            .sum("OL_AMOUNT") //Static column
            .from("order_line")
            .where(QueryBuilder.eq("OL_W_ID",wid))
            .and(QueryBuilder.eq("OL_O_ID", N))
            .and(QueryBuilder.eq("OL_D_ID", districtNo))
            ).one().getDecimal(0);

            s.execute(QueryBuilder.update(Connector.keyspace, "customer")
            .with(QueryBuilder.set("C_BALANCE", c_balance.add(B)))
            .and(QueryBuilder.set("C_DELIVERY_CNT", delivery_cnt+1))
            .where(QueryBuilder.eq("C_W_ID", wid))
            .and(QueryBuilder.eq("C_D_ID", districtNo))
            .and(QueryBuilder.eq("C_ID", cid)));//.onlyIf("TODO"));
        }
    }

    //Transaction 4
    public static void getOrderStatus(int c_wid, int c_did, int cid) throws TransactionException {
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
        System.out.println("O_ENTRY_D: " + lastOrder.getTimestamp("O_ENTRY_D"));
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
        Row r = s.execute(QueryBuilder.select()
                .from(Connector.keyspace, "warehouse")
                .where(QueryBuilder.eq("W_ID", cwid))).one();       
        do {
            BigDecimal currentWarehouse = r.getDecimal("W_YTD");
            r = s.execute(QueryBuilder.update(Connector.keyspace, "warehouse")
                    .with(QueryBuilder.set("W_YTD", payment.add(currentWarehouse)))
                    .where(QueryBuilder.eq("W_ID", cwid))
                    .onlyIf(QueryBuilder.eq("W_YTD", currentWarehouse))
            ).one();
        } while(!r.getBool(0));

        //TODO: add same structure as above
        BigDecimal currentDistrict = s.execute(QueryBuilder.select()
                .from(Connector.keyspace, "district")
                .where(QueryBuilder.eq("D_W_ID", cwid))
                .and(QueryBuilder.eq("D_ID",cdid))).one().getDecimal("D_YTD");
        s.execute(QueryBuilder.update(Connector.keyspace, "district")
                .with(QueryBuilder.set("D_YTD", payment.add(currentDistrict)))
                .where(QueryBuilder.eq("D_W_ID", cwid))
                .and(QueryBuilder.eq("D_ID",cdid))
        );

        //TODO: Add only if clause for each of the values to set
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
        System.out.println("Name: " + C.getString("C_FIRST") +" " + C.getString("C_MIDDLE") + " "+ C.getString("C_LAST"));
        System.out.println("Adress: " + C.getString("C_STREET_1") +" "+ C.getString("C_STREET_2") + " "+ C.getString("C_CITY") + " " +
                             C.getString("C_STATE") +" " + C.getString("C_ZIP") );
        System.out.println("Phone: " + C.getString("C_PHONE"));
        System.out.println("Since: " + C.getTimestamp("C_SINCE"));
        System.out.println("Credit Information: "+ C.getString("C_CREDIT") + " Limit: " + C.getDecimal("C_CREDIT_LIM") + " Discount: " + C.getDecimal("C_DISCOUNT") + " Balance: " + C.getDecimal("C_BALANCE") );

        System.out.println("Warehouse: " + wa.getString("W_STREET_1") + " " + wa.getString("W_STREET_2") +" " + wa.getString("W_CITY") +" "+ wa.getString("W_STATE") +" "+ wa.getString("W_ZIP"));
        System.out.println("District: " + district.getString("D_STREET_1") + " "+ district.getString("D_STREET_2") +" "+ district.getString("D_CITY") +" "+ district.getString("D_STATE") +" "+ district.getString("D_ZIP"));
        System.out.println("Payment: " + payment);
    }

    //Transaction 6
    public static void popularItem(int wid, int did, int L)throws TransactionException{
        //Row district = w.findDistrict(wid,did).orElseThrow(() -> new TransactionException("Unable to find customer with id:" + wid));
        //P:Step 1
        Row district = s.execute(QueryBuilder
                .select().all()
                .from(Connector.keyspace, "district")
                .where(QueryBuilder.eq("D_ID",did))
                .and(QueryBuilder.eq("D_W_ID",wid)))
                .one();
        int N = district.getInt("D_NEXT_O_ID");
        
        //P:Step 2
        ResultSet S = s.execute(QueryBuilder
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
        HashMap<Integer,BigDecimal> popItemQuantity = new HashMap<>();
        while(it.hasNext()) {
            Row currentOrder = it.next();
            int O_ID = currentOrder.getInt("O_ID");

            //get max popularity
            BigDecimal max = s.execute(QueryBuilder
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

            ResultSet Items = s.execute(QueryBuilder
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
                    popItemQuantity.put(Item.getInt("OL_I_ID"),Item.getDecimal("OL_QUANTITY"));
                    popItems.add(Item.getInt("OL_I_ID"));
                }
            }
            //get just popular items
            allItems.put(O_ID,items);
            popularItems.put(O_ID,popItems);
            orders.add(O_ID);
        }
        //O:step 3+4
        //berechnung andern, namen von item und customer holenSSSS
        for(Integer o : orders ){
            //3.a
            Row Order = w.findOrder(wid,did,o).orElseThrow(() -> new TransactionException("Unable to find Order with id:" + o));
            System.out.println("Order ID: " + o + " Date " + Order.getTimestamp("O_ENTRY_D"));
            //3.b
            Row Customer = w.findCustomer(wid,did,Order.getInt("O_C_ID")).orElseThrow(() -> new TransactionException("Unable to find Customer" ));
            System.out.println("CName: " + Customer.getString("C_FIRST") + " " + Customer.getString("C_MIDDLE") + " " +Customer.getString("C_LAST"));
            for(Integer i : popularItems.get(o)){
                //4.a
                Row I = w.findItem(i).orElseThrow(() -> new TransactionException("Unable to find item with id:" + i));
                System.out.println("Popular Item: " + I.getString("I_NAME") + " Quantity: " + popItemQuantity.get(i));
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

    //transaction 8
    public static void relatedCustomer(int cwid, int cdid, int cid) throws TransactionException {
        //Find all tuples in orders for this customer
        System.out.println("C_W_ID: " + cwid + " C_D_ID: " + cdid + " C_ID: "+ cid);

        Iterator<Row> orders = s.execute(QueryBuilder.select().all()
            .from("order_by_customer")
            .where(QueryBuilder.eq("O_C_ID", cid))
            .and(QueryBuilder.eq("O_W_ID", cwid))
            .and(QueryBuilder.eq("O_D_ID", cdid))).iterator();

        while(orders.hasNext()){
            Row order = orders.next();
            //Find all items for that order
            Iterator<Row> items = s.execute(QueryBuilder.select("OL_I_ID")
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
                otherorders.add(s.execute(QueryBuilder.select("OL_O_ID", "OL_W_ID", "OL_D_ID")
                .from("order_line_by_item")
                .where(QueryBuilder.eq("OL_D_ID", cdid))
                .and(QueryBuilder.eq("OL_I_ID", item.getInt("OL_I_ID")))
                .and(QueryBuilder.gt("OL_W_ID", cwid))
                ).iterator());
                otherorders.add(s.execute(QueryBuilder.select("OL_O_ID")
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
                                System.out.println("C_ID: " + s.execute(QueryBuilder.select("O_C_ID")
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
            //TODO: implement Related-Customer Transaction
            //t.relatedCustomer(wid, did, cid);
        });
    }

    private static Select outputQueries[] = new Select[] {
        //4.a
        QueryBuilder
            .select(QueryBuilder.sum("W_YTD"))
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
            
        //4.e - Times out at the moment...  
        QueryBuilder
                .select(QueryBuilder.sum("OL_AMOUNT"), QueryBuilder.sum("OL_QUANTITY"))
                .from("order_line"),
            
        //4.f - Times out at the moment...
        QueryBuilder
                .select(QueryBuilder.sum("S_QUANTITY"), QueryBuilder.sum("S_YTD"), QueryBuilder.sum("S_ORDER_CNT"))
                .from("stock")
    };  
}
