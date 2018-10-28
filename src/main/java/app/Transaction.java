package app;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.bson.Document;
import org.bson.types.MaxKey;

public class Transaction {

    static MongoDatabase db = Connector.mongoclient.getDatabase("warehouse");

    // private static void checkTopCustomer(customer_cnts cc){
        // List<Row> customers = Connector.s.execute(QueryBuilder.select().all().from("customer_top_ten"))
        //     .all().stream()
        //     .sorted( (r1, r2) -> Long.compare(r1.getLong("C_BALANCE"), r2.getLong("C_BALANCE")))
        //     .collect(Collectors.toList());
        // if(customers.isEmpty() || cc.balance() > customers.get(0).getLong("C_BALANCE")){
        //     Connector.s.execute(QueryBuilder.insertInto("customer_top_ten")
        //         .value("C_W_ID", cc.wid())
        //         .value("C_D_ID", cc.did())
        //         .value("C_ID", cc.id())
        //         .value("C_BALANCE", cc.balance()));
        //     if(customers.size() >= 10) {
        //         for(int i = 0; i < customers.size() - 9; i++) {
        //             Connector.s.execute(QueryBuilder
        //                 .delete()
        //                 .from("customer_top_ten")
        //                 .where()
        //                 .and(QueryBuilder.eq("C_W_ID", customers.get(i).getInt("C_W_ID")))
        //                 .and(QueryBuilder.eq("C_D_ID", customers.get(i).getInt("C_D_ID")))
        //                 .and(QueryBuilder.eq("C_ID", customers.get(i).getInt("C_ID"))));
        //         }
        //     }
        // }
    // }

    //Transaction 1
    public static void newOrder(int wid, int did, int cid, List<Integer> ids, List<Integer> wids, List<Long> quantities ) throws NoSuchElementException{
        
    }

    //Transaction 2
    public static void paymentTransaction(int cwid, int cdid, int cid, BigDecimal payment2) {
        Integer payment = payment2.intValue();
        MongoCollection<Document> warehouse = db.getCollection("warehouse");
        MongoCollection<Document> district = db.getCollection("district");
        MongoCollection<Document> customer = db.getCollection("customer");
        Document C = customer.find(new BasicDBObject()
        .append("C_ID", cid)
        .append("C_D_ID", cdid)
        .append("C_W_ID", cwid)
        ).first();
        Document D = district.find(new BasicDBObject()
            .append("D_ID", cdid)
            .append("D_W_ID", cwid)
        ).first();
        Document W = warehouse.find(new BasicDBObject()
            .append("W_ID", cwid)
        ).first();
        warehouse.updateOne(
            new BasicDBObject()
            .append("W_ID", cwid),
            new Document("$inc", new Document("W_YTD", payment)));
        //Step 2
        district.updateOne(D,new Document("$inc", new Document("D_YTD", payment)));

        //Step 3
        customer.updateOne(C, new Document("$inc", new Document("C_YTD_PAYMENT", payment).append("C_BALANCE", -payment).append("C_PAYMENT_CNT",1)));   
        System.out.println("C_W_ID: " + cwid + " C_D_ID: " + cdid + " C_ID: " + cid );
        System.out.println("Name: " + C.getString("C_FIRST") +" " + C.getString("C_MIDDLE") + " "+ C.getString("C_LAST"));
        System.out.println("Adress: " + C.getString("C_STREET_1") +" "+ C.getString("C_STREET_2") + " "+ C.getString("C_CITY") + " " +
                                        C.getString("C_STATE") +" " + C.getString("C_ZIP") );
        System.out.println("Phone: " + C.getString("C_PHONE"));
        System.out.println("Since: " + C.getString("C_SINCE"));
        System.out.println("Credit Information: "+ C.getString("C_CREDIT") + " Limit: " + C.getInteger("C_CREDIT_LIM") + " Discount: " + C.getInteger("C_DISCOUNT") + " Balance: " + C.getInteger("C_BALANCE") );
        
        System.out.println("Warehouse: " + W.getString("W_STREET_1") + " " + W.getString("W_STREET_2") +" " + W.getString("W_CITY") +" "+ W.getString("W_STATE") +" "+ W.getString("W_ZIP"));
        System.out.println("District: " + D.getString("D_STREET_1") + " "+ D.getString("D_STREET_2") +" "+ D.getString("D_CITY") +" "+ D.getString("D_STATE") +" "+ D.getString("D_ZIP"));
        System.out.println("Payment: " + payment);

     }

    //Transaction 3
    public static void processDelivery(int wid, int carrierid) {

        MongoCollection<Document> orders = db.getCollection("order");
        MongoCollection<Document> orderlines = db.getCollection("order_lines");
        for (int did = 1; did <= 10; did++){
            Document order = orders.find(new BasicDBObject()
            .append("O_W_ID", wid)
            .append("O_D_ID", did)
            .append("O_CARRIER_ID", "null"))
            .sort(new BasicDBObject("O_ID", 1))
            .limit(1)
            .first();

            if (order == null){
                continue;
            }
            System.out.println(order);
            int N = order.getInteger("O_ID");

            //b
            orders.updateOne(new BasicDBObject()
            .append("O_W_ID", wid)
            .append("O_D_ID",  did)
            .append("O_ID", N), 
            new Document("$set", new Document().append("O_CARRIER_ID", carrierid)));

            //c
            Date date = Date.from(Instant.now());
            orderlines.updateMany(
            new BasicDBObject()
            .append("O_W_ID", wid)
            .append("OL_D_ID", did)
            .append("OL_O_ID", N),
            new Document("$set", new Document("OL_DELIVERY_D", date)));
            

            //d
            //Double B = orderlines
                    /*
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
        }    */

        }


    }
    //Transaction 4
    public static void getOrderStatus(int c_wid, int c_did, int cid) throws InvalidKeyException{

        //Step 1
        MongoCollection<Document> customers = db.getCollection("customer");
        Document customer =  customers.find(new BasicDBObject()
        .append("C_W_ID", c_wid)
        .append("C_D_ID", c_did)
        .append("C_ID", cid)).first();
        System.out.println("Name: " + customer.getString("C_FIRST") + " " + customer.getString("C_MIDDLE") + " " + customer.getString("C_LAST"));
        System.out.println("Balance: " + customer.getDouble("C_BALANCE"));

        //Step 2
        MongoCollection<Document> orders = db.getCollection("order");
        Document lastOrder = orders.find(new BasicDBObject()
        .append("O_W_ID", c_wid)
        .append("O_D_ID", c_did)
        .append("O_C_ID", cid))
        .sort(new BasicDBObject("O_ENTRY_D", -1))
        .limit(1)
        .first();
        if (lastOrder == null){
            throw new InvalidKeyException("No Order with valid timestamp found");
        }
        int oid =  lastOrder.getInteger("O_ID");
        System.out.println("O_ID: " +oid);
        System.out.println("O_ENTRY_D: " + lastOrder.getString("O_ENTRY_D")); //TODO: Type Date
        System.out.println("O_CARRIER_ID: " + lastOrder.getInteger("O_CARRIER_ID"));

        //Step 3
        MongoCollection<Document> orderlines = db.getCollection("order_line");
        Iterator<Document> ol_it = orderlines.find(new BasicDBObject()
        .append("OL_W_ID", c_wid)
        .append("OL_D_ID", c_did)
        .append("OL_O_ID", oid))
        .iterator();

        while(ol_it.hasNext()){
            Document curr = ol_it.next();
            System.out.println("OL_I_ID: " + curr.getInteger("OL_I_ID"));
            System.out.println("OL_SUPPLY_W_ID: " + curr.getInteger("OL_SUPPLY_W_ID"));
            System.out.println("OL_QUANTITY:"  + curr.getInteger("OL_QUANTITY"));
            System.out.println("OL_AMOUNT:"  + curr.getDouble("OL_AMOUNT"));
            System.out.println("OL_DELIVERY_D:"  + curr.getString("OL_DELIVERY_D")); //TODO date
        }
        
    }

    //Transaction 5
    public static void stockLevel(int wid, int did, long t, int l) {
        // //Step 1
        // long N = new district_cnts(wid, did, "D_NEXT_O_ID").nextoid();

        // //Step 2
        // List<Integer> itemids = Connector.s.execute(QueryBuilder
        //         .select("OL_I_ID")
        //         .from("order_line")
        //         .where(QueryBuilder.eq("OL_W_ID", wid))
        //         .and(QueryBuilder.eq("OL_D_ID", did))
        //         .and(QueryBuilder.gt("OL_O_ID", N-l))
        //         .and(QueryBuilder.lte("OL_O_ID", N))
        //     ).all().stream().mapToInt(r -> r.getInt(0)).boxed().collect(Collectors.toList());
        // //Step 3
        // System.out.println(Connector.s.execute(QueryBuilder
        //     .select("S_QUANTITY")
        //     .from("stock_cnts")
        //     .where(QueryBuilder.eq("S_W_ID", wid))
        //     .and(QueryBuilder.in("S_I_ID", itemids))
        // ).all().stream().mapToLong(r -> r.getLong(0)).map(q -> q <= t ? 1 : 0).count());

        MongoCollection<Document> districts = db.getCollection("district");
        BasicDBObject query = new BasicDBObject();
        query.put("D_W_ID", wid);
        query.put("D_ID", did);     
        FindIterable<Document> d =  districts.find(query);
        Iterator<Document> it = d.iterator();
        Document district = it.next();        
        
        long N = district.getLong("D NEXT O ID");        
        System.out.println("N: " + N);


        
    }
    public static int max(FindIterable<Document> set, String object){
        int maximum = 0;
        Iterator<Document> it = set.iterator();
        while(it.hasNext()){
            Document tmp = it.next();
            
            if(maximum<tmp.getInteger(object))
                maximum = tmp.getInteger(object);
        }
        return maximum;
    }
    //Transaction 6
    public static void popularItem(int wid, int did, int L) {
        //P Step 1
        MongoCollection<Document> districts = db.getCollection("district");
        Document district = districts.find(
            new BasicDBObject()
            .append("D_W_ID",wid)
            .append("D_ID", did)
        ).first();        
        //falscher datentyp
        Integer N = (int) (0 + district.getDouble("D_NEXT_O_ID"));
        //P Step 2
        //MongoCollection<Document> S = db.getCollection("order");
        FindIterable<Document> S =  db.getCollection("order").find(
            new BasicDBObject()
            .append("O_W_ID", wid)
            .append("O_D_ID", did)
            .append("O_ID", new BasicDBObject("$gte",N-L).append("$lt",N))
        );
        //First Print
        Iterator<Document> it = S.iterator();
        ArrayList<Integer> orders = new ArrayList<Integer>();
        //orderNumber -> allItems
        HashMap<Integer, HashSet<Integer>> allItems = new HashMap<>();
        //orderNumber -> popularItems
        HashMap<Integer, HashSet<Integer>> popularItems = new HashMap<>();
        //itemID -> Quantity
        HashMap<Pair,Integer> popItemQuantity = new HashMap<>();
        MongoCollection<Document> order_line = db.getCollection("order_line");
        BasicDBObject query = new BasicDBObject().append("OL_D_ID",did).append("OL_W_ID",wid);
        FindIterable<Document> tmp = order_line.find(query);
        while(it.hasNext()){
            Document currentOrder = it.next();
            int O_ID = currentOrder.getInteger("O_ID");
            FindIterable<Document> tmp2 = tmp.filter(query.append("OL_O_ID", O_ID));
            Integer max = tmp2.sort(new BasicDBObject("OL_QUANTITY",-1)).first().getInteger("OL_QUANTITY");            
            FindIterable<Document> Items =  tmp2.filter(query.append("OL_QUANTITY", max).append("OL_O_ID",O_ID));
            Iterator<Document> it2 = Items.iterator();
            HashSet<Integer> items = new HashSet<>();
            HashSet<Integer> popItems = new HashSet<>();
            while(it2.hasNext()){
                Document Item = it2.next();
                items.add(Item.getInteger("OL_I_ID"));
                popItemQuantity.put(new Pair(O_ID,Item.getInteger("OL_I_ID")),Item.getInteger("OL_QUANTITY"));
                popItems.add(Item.getInteger("OL_I_ID"));
            }
            allItems.put(O_ID,items);
            popularItems.put(O_ID,popItems);
            orders.add(O_ID);
        }
        for(Integer o : orders ){
            //3.a
            
            Document Order = db.getCollection("order").find(
                new BasicDBObject()
                .append("O_W_ID", wid)
                .append("O_D_ID", did)
                .append("O_ID",o)
            ).first();
            
            System.out.println("Order ID: " + o + " Date " + Order.getString("O_ENTRY_D"));
            //3.b
            Document Customer = db.getCollection("customer").find(
                new BasicDBObject()
                .append("C_W_ID", wid)
                .append("C_D_ID", did)
                .append("C_ID",Order.getInteger("O_C_ID"))
            ).first();
            //Wrapper.findCustomer(wid,did,Order.getInt("O_C_ID"));
            System.out.println("CName: " + Customer.getString("C_FIRST") + " " + Customer.getString("C_MIDDLE") + " " +Customer.getString("C_LAST"));
            for(Integer i : popularItems.get(o)){
                //4.a
                Document I = db.getCollection("item").find(
                    new BasicDBObject()
                    .append("I_ID", i)
                ).first();
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
        

    }
    
    //Transaction 8
    public static void relatedCustomer(int cwid, int cdid, int cid) {
        
    }
}
