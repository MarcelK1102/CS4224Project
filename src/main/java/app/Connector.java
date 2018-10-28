package app;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public class Connector {
    public static String database = "warehouse";
    private static String contactPoints[] = {
        "192.168.48.249",
        "192.168.48.250",
        "192.168.48.251",
        "192.168.48.252",
        "192.168.48.253"
    };

    public static MongoDatabase db;
    public static MongoCollection<Document> warehouse;
    public static MongoCollection<Document> district;
    public static MongoCollection<Document> item;
    public static MongoCollection<Document> order;
    public static MongoCollection<Document> stock;
    public static MongoCollection<Document> order_line;
    public static MongoCollection<Document> customer;

    private static MongoClient mongoclient;
    
    // public static Session s;
    // private static Cluster cluster;
    public static void connect(){
       // Connector.mongoclient = MongoClients.create("mongodb://" + String.join(",",contactPoints));
       //ClusterSettings clusterSettings = ClusterSettings.builder().hosts(asList(new ServerAddress("localhost"))).build();
        //MongoClientSettings settings = MongoClientSettings.builder().clusterSettings(clusterSettings).build();
        mongoclient = MongoClients.create("mongodb://" + String.join(",", contactPoints));
        db = Connector.mongoclient.getDatabase(Connector.database);
        warehouse = db.getCollection("warehouse");
        district = db.getCollection("district");
        item = db.getCollection("item");
        order = db.getCollection("order");
        stock = db.getCollection("stock");
        order_line = db.getCollection("order_line");
        customer = db.getCollection("customer");
    }

    public static void close() {
        // if(s != null){
        //     s.close();
        //     s = null;
        // }
        // cluster.close();
        mongoclient.close();
    }

}