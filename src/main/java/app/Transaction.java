package app;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.util.List;
import java.util.NoSuchElementException;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class Transaction {

    static MongoDatabase db =Connector.mongoclient.getDatabase("warehouse");

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

        MongoCollection<Document> districts = db.getCollection("district");
        BasicDBObject query = new BasicDBObject();
        query.put("W_ID", wid);
        query.put("D_ID", did);
        FindIterable<Document> it = districts.find(query);
        Document district = it.first();
        long N = district.getLong("D NEXT O ID");
        System.out.println("N: " + N);
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
