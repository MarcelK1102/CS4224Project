package app;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SocketOptions;

public class Connector {
    private Cluster cluster;
    private int port = 9042;
    public static String keyspace = "Warehouse";
    private String clusterName = "cs4224g";
    private String contactPoints[] = {
        "192.168.48.249",
        "192.168.48.250",
        "192.168.48.251",
        "192.168.48.252",
        "192.168.48.253"
    };

    public Connector(){
        cluster = Cluster.builder()
            .withClusterName(clusterName)
            .addContactPoints(contactPoints)
            .withPort(port)
            .withSocketOptions(new SocketOptions().setReadTimeoutMillis(65000))
            .build();
    }

    public Session connect(){
        Session s = cluster.connect();
        s.execute("use " + keyspace + ";");
        return s;
    }

}