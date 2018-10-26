package app;

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
    
    // public static Session s;
    // private static Cluster cluster;
    public static void connect(){
        // cluster = Cluster.builder()
        //     .withClusterName(clusterName)
        //     .addContactPoints(contactPoints)
        //     .withPort(port)
        //     .withSocketOptions(new SocketOptions().setReadTimeoutMillis(600000))
        //     .withQueryOptions(new QueryOptions().setConsistencyLevel(qo))
        //     .build();;
        // s = cluster.connect();
        // s.execute("use " + keyspace + ";");
        // return s;
    }

    public static void close() {
        // if(s != null){
        //     s.close();
        //     s = null;
        // }
        // cluster.close();
    }

}