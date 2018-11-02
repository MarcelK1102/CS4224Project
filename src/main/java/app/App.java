package app;

import java.util.Arrays;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

public class App {

    public static void main(String args[]) {
        //Disable logging:
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver").setLevel(Level.OFF  );

        Connector.connect();
        System.out.println("connected");
        //Client.handleInput(Integer.parseInt(args[0]));
        Transaction.processDelivery(1, 77);
        //Client.printStats();
        Connector.close();
    }

}

//todo
//set up a mongodb shared cluster: https://docs.mongodb.com/manual/tutorial/deploy-shard-cluster/