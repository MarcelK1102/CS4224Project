package app;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.BatchStatement.Type;
import com.datastax.driver.core.querybuilder.QueryBuilder;

import app.wrapper.customer;
import app.wrapper.customer_cnts;
import app.wrapper.district;
import app.wrapper.district_cnts;
import app.wrapper.item;
import app.wrapper.stock_cnts;
import app.wrapper.warehouse;
import app.wrapper.warehouse_cnts;

public class Transaction {

    private static void checkTopCustomer(customer_cnts cc){
        List<Row> customers = Connector.s.execute(QueryBuilder.select().all().from("customer_top_ten"))
            .all().stream()
            .sorted( (r1, r2) -> Long.compare(r1.getLong("C_BALANCE"), r2.getLong("C_BALANCE")))
            .collect(Collectors.toList());
        if(customers.isEmpty() || cc.balance() > customers.get(0).getLong("C_BALANCE")){
            Connector.s.execute(QueryBuilder.insertInto("customer_top_ten")
                .value("C_W_ID", cc.wid())
                .value("C_D_ID", cc.did())
                .value("C_ID", cc.id())
                .value("C_BALANCE", cc.balance()));
            if(customers.size() >= 10) {
                for(int i = 0; i < customers.size() - 9; i++) {
                    Connector.s.execute(QueryBuilder
                        .delete()
                        .from("customer_top_ten")
                        .where()
                        .and(QueryBuilder.eq("C_W_ID", customers.get(i).getInt("C_W_ID")))
                        .and(QueryBuilder.eq("C_D_ID", customers.get(i).getInt("C_D_ID")))
                        .and(QueryBuilder.eq("C_ID", customers.get(i).getInt("C_ID"))));
                }
            }
        }
    }

    //Transaction 1
    public static void newOrder(int wid, int did, int cid, List<Integer> ids, List<Integer> wids, List<Long> quantities ) throws NoSuchElementException{
        //Step O:1
        customer c = new customer(wid, did, cid, "C_W_ID" , "C_W_ID", "C_ID", "C_LAST", "C_CREDIT", "C_DISCOUNT");
        System.out.println(c);
        
        //Step P:1
        district_cnts dc = new district_cnts(wid, did, "D_NEXT_O_ID"); 
        long N ;
        
        //Step P:
        do{
            N = dc.nextoid() + 1;
            Connector.s.execute(Connector.s.prepare(
                    "update district_cnts set D_NEXT_O_ID = D_NEXT_O_ID + 1 where D_W_ID = :d and D_ID = :d;"
                ).bind(wid, did));
            dc.find(wid, did, "D_NEXT_O_ID");
        } while(N != dc.nextoid());

        //Step O:2
        district d = new district(wid, did, "D_TAX");
        BigDecimal dtax = d.tax();
        BigDecimal wtax = new warehouse(wid, "W_TAX").tax();
        System.out.println("w_tax : " + wtax + ", d_tax : " + dtax);

        //Step P:3
        Date now = Date.from(Instant.now());
        Connector.s.executeAsync((QueryBuilder.insertInto("orders")
                .value("O_ID", N)
                .value("O_D_ID", did)
                .value("O_W_ID ", wid) 
                .value("O_C_ID", cid) 
                .value("O_ENTRY_D", now)
                .value("O_OL_CNT", BigDecimal.valueOf( ids.size())) 
                .value("O_ALL_LOCAL", BigDecimal.valueOf(wids.stream().allMatch(i -> i == wid) ? 1 : 0))
        ));

        //Step O:3
        System.out.println("o_id : " + N + " o_entry_d : " + now);

        //Step P:4
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        //Step P:5
        BatchStatement batchOL = new BatchStatement(Type.UNLOGGED);
        BatchStatement batchStock = new BatchStatement(Type.COUNTER);
        stock_cnts s = new stock_cnts();
        for(int i = 0; i < ids.size(); i++){
            int iid = ids.get(i);
            //step P:a
            s.find(wid, iid, "S_QUANTITY");
            long adjustedQuantity = quantities.get(0) - (s.quantity() - quantities.get(i) < 10 ? 100 : 0);
            long remote = wid != wids.get(i) ? 1 : 0;
            batchStock.add(Connector.s.prepare(
                    "update stock_cnts set S_QUANTITY = S_QUANTITY - ?, S_YTD = S_YTD + ?, S_ORDER_CNT = S_ORDER_CNT + 1, S_REMOTE_CNT = S_REMOTE_CNT + ? where S_W_ID = ? and S_I_ID = ?;"
                ).bind(adjustedQuantity, quantities.get(i), remote, wid, iid)
            );
            
            //Step P:e
            item it = new item(iid, "I_PRICE", "I_NAME");
            BigDecimal itemAmount = it.price().multiply(BigDecimal.valueOf(quantities.get(i)));
            
            //Step P:f
            totalAmount = totalAmount.add(itemAmount);
            
            //step O:5
            System.out.println("item_number : " + iid);
            System.out.println("i_name : " + it.name());
            System.out.println("supplier_warehouse : " + wids.get(i));
            System.out.println("quantity : " + quantities.get(i));
            System.out.println("ol_amount : " + itemAmount); 
            System.out.println("s_quantity : " + (s.quantity() - adjustedQuantity));

            //Step P:g
            batchOL.add(QueryBuilder.insertInto("order_line")
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
        Connector.s.executeAsync(batchOL);
        System.out.println("EXECTUING BATCH WITH SIZE " + batchStock.size());
        Connector.s.execute(batchStock);

        //Step P:6
        totalAmount = totalAmount.multiply(BigDecimal.ONE.add(dtax).add(wtax)).multiply(BigDecimal.ONE.subtract(c.discount()));
        
        //Step O:4
        System.out.println("num_items : " + ids.size() + ", total_amount : " + totalAmount);
    }

    //Transaction 2
    public static void paymentTransaction(int cwid, int cdid, int cid, BigDecimal payment) {
        //Step 1   
        Connector.s.execute(Connector.s.prepare(
                "update warehouse_cnts set W_YTD = W_YTD + :d where W_ID = :d;"
            ).bind(payment.longValue(), cwid));

        //Step 2
        Connector.s.execute(Connector.s.prepare(
                "update district_cnts set D_YTD = D_YTD + :d where D_W_ID = :d and D_ID = :d;"
            ).bind(payment.longValue(), cwid, cdid));

        //Step 3
        
        Connector.s.execute(Connector.s.prepare(
                "update customer_cnts set C_BALANCE = C_BALANCE - :d, C_YTD_PAYMENT = C_YTD_PAYMENT + :d, C_PAYMENT_CNT = C_PAYMENT_CNT + 1 where C_W_ID = :d and C_D_ID = :d and C_ID = :d;"
            ).bind(payment.longValue(), payment.longValue(), cwid, cdid, cid));
           
        warehouse w = new warehouse(cwid, "W_STREET_1","W_STREET_2","W_CITY","W_STATE","W_ZIP");
        warehouse_cnts wc = new warehouse_cnts(cwid);
        System.out.println(w + ", " + wc);
        district d = new district(cwid, cdid, "D_STREET_1","D_STREET_2","D_CITY","D_STATE","D_ZIP");
        district_cnts dc = new district_cnts(cwid, cdid);
        System.out.println(d + ", " + dc);
        customer c = new customer(cwid, cdid, cid);
        customer_cnts cc = new customer_cnts(cwid, cdid, cid);
        checkTopCustomer(cc);
        System.out.println(c + ", " + cc);
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
            BigDecimal B = Connector.s.execute(QueryBuilder.select()
            .sum("OL_AMOUNT")
            .from("order_line")
            .where(QueryBuilder.eq("OL_W_ID",wid))
            .and(QueryBuilder.eq("OL_O_ID", N))
            .and(QueryBuilder.eq("OL_D_ID", districtNo))
            ).one().getDecimal(0);

            Connector.s.execute(Connector.s.prepare(
                "update customer_cnts set C_BALANCE = C_BALANCE + :d, C_DELIVERY_CNT = C_DELIVERY_CNT + 1 where C_W_ID = :d and C_D_ID = :d and C_ID = :d;"
            ).bind(B.longValue(), wid, districtNo, cid));
            checkTopCustomer(new customer_cnts(wid, districtNo, cid));
        }
    }

    //Transaction 4
    public static void getOrderStatus(int c_wid, int c_did, int cid) throws InvalidKeyException{
        //1.
        customer c = new customer(c_wid, c_did, cid);
        customer_cnts cc = new customer_cnts(c_wid, c_did, cid);
        checkTopCustomer(cc);
        System.out.println(c + ", " +cc);

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
    public static void stockLevel(int wid, int did, long t, int l) {
        //Step 1
        long N = new district_cnts(wid, did, "D_NEXT_O_ID").nextoid();

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
            .select("S_QUANTITY")
            .from("stock_cnts")
            .where(QueryBuilder.eq("S_W_ID", wid))
            .and(QueryBuilder.in("S_I_ID", itemids))
        ).all().stream().mapToLong(r -> r.getLong(0)).map(q -> q <= t ? 1 : 0).count());
    }

    //Transaction 6
    public static void popularItem(int wid, int did, int L) {
        //Row district = Wrapper.findDistrict(wid,did).orElseThrow(() -> new TransactionException("Unable to find customer with id:" + wid));
        //P:Step 1
        long N = new district_cnts(wid, did, "D_NEXT_O_ID").nextoid();
        
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
    public static void topBalance(){
        //processing: step 1
        List<Row> rows = Connector.s.execute(QueryBuilder
            .select().all()
            .from("customer_top_ten")
        ).all();

        //Output: step 1
        rows.stream()
            .sorted((r1, r2) -> Long.compare(r2.getLong("C_BALANCE"), r2.getLong("C_BALANCE")))
            .limit(10)
            .forEach(r ->{
                //a
                customer c = new customer(r.getInt("C_W_ID"), r.getInt("C_D_ID"), r.getInt("C_ID"), "C_FIRST", "C_MIDDLE", "C_LAST");
                System.out.println(c);

                //b
                customer_cnts cc = new customer_cnts(r.getInt("C_W_ID"), r.getInt("C_D_ID"), r.getInt("C_ID"), "C_BALANCE");
                System.out.println(cc);

                //c
                warehouse w = new warehouse(r.getInt("C_W_ID"), "W_NAME");
                System.out.println(w);

                //d
                district  d = new district(r.getInt("C_W_ID"), r.getInt("C_D_ID"), "D_NAME");
                System.out.println(d);

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
                otherorders.add(Connector.s.execute(QueryBuilder.select("OL_O_ID", "OL_W_ID", "OL_D_ID")
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
}
