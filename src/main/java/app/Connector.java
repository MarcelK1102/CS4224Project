package app;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public class Connector {
    private Cluster cluster;
    private int port = 9042;
    private String keysapce = "Warehouse";
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
            .build();
    }

    public Session connect(){
        return cluster.connect();
    }

}