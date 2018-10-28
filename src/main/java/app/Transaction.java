package app;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Updates.combine;

import static app.Table.*;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.util.Date;
import java.util.List;

import com.mongodb.client.MongoCollection;

import org.bson.Document;

public class Transaction {
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
        Connector.order.insertOne(order);
        
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
            Connector.order_line.insertOne(orderLine);

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
        totalAmount *= (1 + d_tax.from(district).doubleValue() + w_tax.from(warehouse).doubleValue()) * (1 - c_discount.from(customer).doubleValue());
        //output 4
        output.put("NUM_ITEMS", ids.size());
        output.put("TOTAL_AMOUNT", totalAmount);
        System.out.println(output.toJson());
    }

    //Transaction 2
    public static void paymentTransaction(int cwid, int cdid, int cid, BigDecimal payment) {
        
    }

    //Transaction 3
    public static void processDelivery(int wid, int carrierid) {
        
    }

    //Transaction 4
    public static void getOrderStatus(int c_wid, int c_did, int cid) throws InvalidKeyException{
        
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

        System.out.println("1");
        MongoCollection<Document> districts = Connector.db.getCollection("district");
        System.out.println("2");
        Document district = districts.find().first();
        System.out.println("3");
        System.out.println(district.toJson());
        System.out.println("4");
        /*BasicDBObject query = new BasicDBObject();
        query.put("W_ID", wid);
        query.put("D_ID", did);
        FindIterable<Document> district = districts.findOne();
        
        System.out.println("stock level5");*/
        
    }

    //Transaction 6
    public static void popularItem(int wid, int did, int L) {
        
    }
    
    //Transaction 7
    public static void topBalance(){
        

    }
    
    //Transaction 8
    public static void relatedCustomer(int cwid, int cdid, int cid) {
        
    }
}
