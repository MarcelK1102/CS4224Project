package app;

import com.mongodb.ReadConcern;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

public class Connector {
    public static String database = "warehouse";
    private static String contactPoints[] = {
        "192.168.48.249",
        "192.168.48.250",
        "192.168.48.251",
        "192.168.48.252",
        "192.168.48.253"
    };

    public static String connectorString = "mongodb://" + String.join(",", contactPoints);

    public static MongoCollection<Document> warehouse;
    public static MongoCollection<Document> district;
    public static MongoCollection<Document> item;
    public static MongoCollection<Document> order;
    public static MongoCollection<Document> stock;
    public static MongoCollection<Document> orderLine;
    public static MongoCollection<Document> customer;
    public static com.mongodb.async.client.MongoCollection<Document> warehouseAsync;
    public static com.mongodb.async.client.MongoCollection<Document> districtAsync;
    public static com.mongodb.async.client.MongoCollection<Document> itemAsync;
    public static com.mongodb.async.client.MongoCollection<Document> orderAsync;
    public static com.mongodb.async.client.MongoCollection<Document> stockAsync;
    public static com.mongodb.async.client.MongoCollection<Document> orderLineAsync;
    public static com.mongodb.async.client.MongoCollection<Document> customerAsync;

    private static MongoClient mongoclient;
    private static com.mongodb.async.client.MongoClient mongoClientAsync;

    public static void connect(ReadConcern rc, WriteConcern wc){
        mongoclient = MongoClients.create(connectorString);
        MongoDatabase db = mongoclient.getDatabase(database).withReadConcern(rc).withWriteConcern(wc);
        warehouse = db.getCollection("warehouse");
        district = db.getCollection("district");
        item = db.getCollection("item");
        order = db.getCollection("order");
        stock = db.getCollection("stock");
        orderLine = db.getCollection("order_line");
        customer = db.getCollection("customer");
        
        mongoClientAsync = com.mongodb.async.client.MongoClients.create(connectorString);
        com.mongodb.async.client.MongoDatabase dbAsync = mongoClientAsync.getDatabase(database).withReadConcern(rc).withWriteConcern(wc);;
        warehouseAsync = dbAsync.getCollection("warehouse");
        districtAsync = dbAsync.getCollection("district");
        itemAsync = dbAsync.getCollection("item");
        orderAsync = dbAsync.getCollection("order");
        stockAsync = dbAsync.getCollection("stock");
        orderLineAsync = dbAsync.getCollection("order_line");
        customerAsync = dbAsync.getCollection("customer");
    }

    public static void close() {
        mongoclient.close();
        mongoClientAsync.close();
    }

}