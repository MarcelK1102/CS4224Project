package app;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SocketOptions;

import app.wrapper.tablebase;

public class Connector {
    private Cluster cluster;
    private int port = 9042;
    public static String keyspace = "Warehouse";
    private String clusterName = "cs4224g";
    public static Session s;
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
        s = cluster.connect();
        s.execute("use " + keyspace + ";");
        return s;
    }

    public void close() {
        if(s != null){
            s.close();
            s = null;
        }
        cluster.close();
    }

}