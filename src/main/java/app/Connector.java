package app;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class Connector {
    private static int port = 9042;
    public static String keyspace = "warehouse";
    private static String clusterName = "cs4224g";
    private static String contactPoints[] = {
        "192.168.48.249",
        "192.168.48.250",
        "192.168.48.251",
        "192.168.48.252",
        "192.168.48.253"
    };

    static MongoClient mongoclient;
    
    // public static Session s;
    // private static Cluster cluster;
    public static void connect(){
       // Connector.mongoclient = MongoClients.create("mongodb://" + String.join(",",contactPoints));
       Connector.mongoclient = MongoClients.create("mongodb://" + contactPoints[0]);

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