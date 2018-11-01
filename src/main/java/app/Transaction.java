package app;

import static app.Table.*;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Projections.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;

public class Transaction {
    static Block<Document> printBlock = new Block<Document>() {
        @Override
        public void apply(final Document document) {
            System.out.println(document.toJson());
        }
    };

    //Transaction 1
    public static void newOrder(int wid, int did, int cid, List<Integer> ids, List<Integer> wids, List<Long> quantities ) {
        Document output = new Document();
        Document warehouse = Connector.warehouse.find(w_id.eq(wid)).first();
        Document customer = Connector.customer.find(and(c_w_id.eq(wid), c_d_id.eq(did), c_id.eq(cid))).first();
        //Processing 1 and 2
        Document district = Connector.district.findOneAndUpdate(and(d_w_id.eq(wid), d_id.eq(did)), d_next_o_id.inc(1));
        int N = d_next_o_id.from(district).intValue();
        //Processing 3
        Document order = new Document();
        order.put(o_id.s, N);
        order.put(o_d_id.s, did);
        order.put(o_w_id.s, wid);
        order.put(o_c_id.s, cid);
        order.put(o_entry_d.s, new Date());
        order.put(o_carrier_id.s, null);
        order.put(o_ol_cnt.s, ids.size()); 
        order.put(o_all_local.s, wids.stream().allMatch(i -> i == wid) ? 1 : 0);
        Connector.orderAsync.insertOne(order, (r, t) -> {});
        
        //Output 1
        output.put(w_id.s, wid);
        output.put(d_id.s, did);
        output.put(c_id.s, cid);
        output.put(c_last.s, c_last.from(customer));
        output.put(c_credit.s, c_credit.from(customer));
        //Output 2
        output.put(w_tax.s, w_tax.from(warehouse));
        output.put(d_tax.s, d_tax.from(district));
        //Output 3
        output.put(o_id.s, o_id.from(order));
        output.put(o_entry_d.s, o_entry_d.from(order));
        //Processing 4
        double totalAmount = 0;
        //Processing 5
        List<Document> orderLines = new ArrayList<>();
        for(int i = 0; i < ids.size(); i++){
            //Processing d
            Document stock = Connector.stock.findOneAndUpdate(
                and(s_w_id.eq(wid), s_i_id.eq(ids.get(i))), 
                combine(
                    s_quantity.inc(-quantities.get(i)),
                    s_ytd.inc(quantities.get(i)),
                    s_order_cnt.inc(1),
                    s_remote_cnt.inc(wids.get(i) != wid ? 1 : 0)
            ));
            //Processing a + b
            Long quantity = s_quantity.from(stock) - quantities.get(i);
            //Processing c
            if(quantity < 10){
                Connector.stock.updateOne(and(s_w_id.eq(wid), s_i_id.eq(ids.get(i))), s_quantity.inc(100));
                quantity += 100;
            }
            //Processing e
            Document item = Connector.item.find(i_id.eq(ids.get(i))).first();
            double itemAmount = quantities.get(i) + i_price.from(item).doubleValue();
            //Processing f
            totalAmount += itemAmount;

            Document orderLine = new Document();
            orderLine.put(ol_o_id.s, N);
            orderLine.put(ol_d_id.s, did);
            orderLine.put(ol_w_id.s, wid);
            orderLine.put(ol_number.s, i);
            orderLine.put(ol_i_id.s, ids.get(i));
            orderLine.put(ol_supply_w_id.s, wids.get(i));
            orderLine.put(ol_quantity.s, quantities.get(i));
            orderLine.put(ol_amount.s, itemAmount);
            orderLine.put(ol_delivery_d.s, null);
            orderLine.put(ol_dist_info.s, "S_DIST_" + String.format("%02d", did));
            orderLines.add(orderLine);
            //Output 5
            Document suboutput = new Document();
            //Output a
            suboutput.put(i_id.s, ids.get(i));
            //Output b
            suboutput.put(i_name.s, i_name.from(item));
            //Output c
            suboutput.put(w_id.s, wids.get(i));
            //Output d
            suboutput.put("QUANTITY", quantities.get(i));
            //Output e
            suboutput.put(ol_amount.s, ol_amount.from(orderLine));
            //Output f
            suboutput.put(s_quantity.s, quantity);
            output.put(""+i, suboutput);
        }
        Connector.orderLineAsync.insertMany(orderLines, (r,t) -> {});
        totalAmount *= (1 + d_tax.from(district).doubleValue() + w_tax.from(warehouse).doubleValue()) * (1 - c_discount.from(customer).doubleValue());
        //output 4
        output.put("NUM_ITEMS", ids.size());
        output.put("TOTAL_AMOUNT", totalAmount);
        System.out.println(output.toJson());
    }

    //Transaction 2
    public static void paymentTransaction(int cwid, int cdid, int cid, BigDecimal payment2) {
        Integer payment = payment2.intValue();
        Document C = Connector.customer.find(new BasicDBObject()
        .append("C_ID", cid)
        .append("C_D_ID", cdid)
        .append("C_W_ID", cwid)
        ).first();
        Document D = Connector.district.find(new BasicDBObject()
            .append("D_ID", cdid)
            .append("D_W_ID", cwid)
        ).first();
        Document W = Connector.warehouse.find(new BasicDBObject()
            .append("W_ID", cwid)
        ).first();
        Connector.warehouse.updateOne(W,new Document("$inc", new Document("W_YTD", payment)));
        Connector.district.updateOne(D,new Document("$inc", new Document("D_YTD", payment)));
        Connector.customer.updateOne(C, new Document("$inc", new Document("C_YTD_PAYMENT", payment).append("C_BALANCE", -payment).append("C_PAYMENT_CNT",1)));   
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
        for (int did = 1; did <= 10; did++){

            FindIterable<Document> orders_curr = Connector.order.find(and(o_w_id.eq(wid), o_d_id.eq(did)));

            Document order = orders_curr.filter(new BasicDBObject("O_CARRIER_ID", "null"))
            .sort(new BasicDBObject("O_ID", 1))
            .limit(1)
            .first();

            if (order == null){
                continue;
            }
            int N = o_id.from(order);
            int cid = o_c_id.from(order);

            //b
            Connector.order.updateOne(orders_curr.filter(o_id.eq(N)).first(), 
            new Document("$set", new Document().append("O_CARRIER_ID", carrierid)));

            //c
            String date = Date.from(Instant.now()).toString();

            Bson ol_query = and(ol_w_id.eq(wid), ol_d_id.eq(did), ol_o_id.eq(N));
            Connector.orderLine.updateMany(ol_query,
            new Document("$set", new Document("OL_DELIVERY_D", date))); 
            //d
            Iterator<Document> toAdd = Connector.orderLine.find(ol_query).iterator();

            double B = 0;
            while(toAdd.hasNext()){
                B += ol_amount.from(toAdd.next());
            }

            Connector.customer.updateOne(and(c_w_id.eq(wid), c_d_id.eq(did), c_id.eq(cid)),
            and(c_balance.inc(B), c_delivery_cnt.inc(1)));
        }
    }
    //Transaction 4
    public static void getOrderStatus(int c_wid, int c_did, int cid) throws InvalidKeyException{

        //Step 1
        Document customer =  Connector.customer.find(and(c_w_id.eq(c_wid), c_d_id.eq(c_did), c_id.eq(cid))).first();
        System.out.println("Name: " + c_first.from(customer) + " " + c_middle.from(customer) + " " + c_last.from(customer));
        System.out.println("Balance: " + c_balance.from(customer));

        //Step 2
        Document lastOrder = Connector.order.find(and(o_w_id.eq(c_wid), o_d_id.eq(c_did), o_c_id.eq(cid)))
        .sort(new BasicDBObject("O_ENTRY_D", -1)) //index created!
        .limit(1)
        .first();
        if (lastOrder == null){
            throw new InvalidKeyException("No Order with valid timestamp found");
        }
        int oid = o_id.from(lastOrder);
        System.out.println("O_ID: " +oid); 
        System.out.println("O_ENTRY_D: " + o_entry_d.from(lastOrder));
        System.out.println("O_CARRIER_ID: " + o_carrier_id.from(lastOrder));

        //Step 3
        Iterator<Document> ol_it = Connector.orderLine.find(
            and(ol_w_id.eq(c_wid), ol_d_id.eq(c_did), ol_o_id.eq(oid)))
            .iterator();

        while(ol_it.hasNext()){
            Document curr = ol_it.next();
            System.out.println("OL_I_ID: " + ol_i_id.from(curr));
            System.out.println("OL_SUPPLY_W_ID: " + ol_supply_w_id.from(curr));
            System.out.println("OL_QUANTITY:"  + ol_quantity.from(curr));
            System.out.println("OL_AMOUNT:"  + ol_amount.from(curr));
            System.out.println("OL_DELIVERY_D:"  + o_entry_d.from(curr));
        } 
    }

    //Transaction 5
    public static void stockLevel(int wid, int did, long T, int L) {
        //Processing 1
        Document district = Connector.district.find(and(d_w_id.eq(wid), d_id.eq(did))).first();
        int N = d_next_o_id.from(district).intValue();
        //Processing 2
        FindIterable<Document> S = Connector.orderLine.find(and(ol_d_id.eq(did), ol_w_id.eq(wid), ol_o_id.gt(N-L))).projection(include(ol_i_id.s));
        //Processing 3
        S.forEach(new Block<Document>() {
            @Override
            public void apply(final Document s) {
                Connector.stock.find(and(s_w_id.eq(wid), s_i_id.eq(ol_i_id.from(s)), s_quantity.lt(T))).forEach(printBlock);
            }
        });
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
        MongoCollection<Document> districts = Connector.district;
        Document district = districts.find(
            new BasicDBObject()
            .append("D_W_ID",wid)
            .append("D_ID", did)
        ).first();        
        //falscher datentyp
        Integer N = (int) (0 + district.getDouble("D_NEXT_O_ID"));
        //P Step 2
        //MongoCollection<Document> S = Connector.order;
        FindIterable<Document> S =  Connector.order.find(
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
        MongoCollection<Document> orderLine = Connector.orderLine;
        BasicDBObject query = new BasicDBObject().append("OL_D_ID",did).append("OL_W_ID",wid);
        FindIterable<Document> tmp = orderLine.find(query);
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
            
            Document Order = Connector.order.find(
                new BasicDBObject()
                .append("O_W_ID", wid)
                .append("O_D_ID", did)
                .append("O_ID",o)
            ).first();
            
            System.out.println("Order ID: " + o + " Date " + Order.getString("O_ENTRY_D"));
            //3.b
            Document Customer = Connector.customer.find(
                new BasicDBObject()
                .append("C_W_ID", wid)
                .append("C_D_ID", did)
                .append("C_ID",Order.getInteger("O_C_ID"))
            ).first();
            //Wrapper.findCustomer(wid,did,Order.getInt("O_C_ID"));
            System.out.println("CName: " + Customer.getString("C_FIRST") + " " + Customer.getString("C_MIDDLE") + " " +Customer.getString("C_LAST"));
            for(Integer i : popularItems.get(o)){
                //4.a
                Document I = Connector.item.find(
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
        final Document output = new Document();
        Connector.customer.find().sort(Sorts.descending(c_balance.s)).limit(10).forEach(new Block<Document>() {
            @Override
            public void apply(final Document customer) {
                Document warehouse = Connector.warehouse.find(w_id.eq(c_w_id.from(customer))).first(); 
                Document district = Connector.district.find(and(d_w_id.eq(c_w_id.from(customer)), d_id.eq(c_d_id.from(customer)))).first(); 
                Document suboutput = new Document();
                suboutput.put(c_first.s, c_first.from(customer));
                suboutput.put(c_middle.s, c_middle.from(customer));
                suboutput.put(c_last.s, c_last.from(customer));
                suboutput.put(c_balance.s, c_balance.from(customer));
                suboutput.put(w_name.s, w_name.from(warehouse));
                suboutput.put(d_name.s, d_name.from(district));
                output.put(c_w_id.from(customer).toString(), suboutput);
            }
        });
        System.out.println(output.toJson());
    }
    
    //Transaction 8
    public static void relatedCustomer(int cwid, int cdid, int cid) {
        
    //Find all tuples in orders for this customer
    System.out.println("C_W_ID: " + cwid + " C_D_ID: " + cdid + " C_ID: "+ cid);

    Iterator<Document> orders = Connector.order.find(new BasicDBObject()
    .append("O_C_ID", cid)
    .append("O_W_ID", cwid)
    .append("O_D_ID", cdid)).iterator();

    while(orders.hasNext()){
        Document order = orders.next();
        //Find all items for that order
        Iterator<Document> items = Connector.orderLine.find(new BasicDBObject()
            .append("OL_W_ID", cwid)
            .append("OL_D_ID", cdid)
            .append("OL_O_ID",order.getInteger("O_ID"))
        ).iterator();
        
        Map<Integer, Pair> oids = new HashMap<>();
        while(items.hasNext()){
            Document item = items.next();
            int itemId = item.getInteger("OL_I_ID");
            // System.out.println(item);
            //for this item we need to find OL_O_ID that has the same item
            List<Iterator<Document>> otherorders = new ArrayList<Iterator<Document>>();

            otherorders.add(
                Connector.orderLine.find(
                    new BasicDBObject()
                        .append("OL_D_ID", cdid)
                        .append("OL_I_ID", item.getInteger("OL_I_ID"))
                        .append("OL_W_ID",  new BasicDBObject("$lt",cwid))
                ).iterator()
            );

            otherorders.add(
                Connector.orderLine.find(
                    new BasicDBObject()
                        .append("OL_D_ID", cdid)
                        .append("OL_I_ID", item.getInteger("OL_I_ID"))
                        .append("OL_W_ID",  new BasicDBObject("$gt",cwid))
                ).iterator()
            );

            for(Iterator<Document> otherorder : otherorders){
                while(otherorder.hasNext()){
                    Document orderO = otherorder.next();
                    Integer oloid = orderO.getInteger("OL_O_ID");
                    if(!oids.containsKey(oloid))
                        oids.put(oloid, new Pair(itemId, -1));
                    else {
                        Pair p = oids.get(oloid);
                        if(p.a != itemId && p.b < 0){
                            p.b = itemId;
                            System.out.println("C_ID: " + Connector.order.find(new BasicDBObject()
                                .append("O_W_ID", orderO.getInteger("OL_W_ID"))
                                .append("O_D_ID", orderO.getInteger("OL_D_ID"))
                                .append("O_ID", oloid)).first().getInteger("O_C_ID"));
                        }
                    }
                }
            }
        }
    }
}
}
