package app;

import java.util.Arrays;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

public class App {

    public static void main(String args[]) {
        //Disable logging:
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver").setLevel(Level.ERROR);

        Connector.connect();
        System.out.println("connected");
        //Client.handleInput(Integer.parseInt(args[0]));
        // Transaction.newOrder(1, 1, 1, Arrays.asList(1,2,3), Arrays.asList(1,1,1), Arrays.asList(123L,321L,312L)); 
        // Transaction.stockLevel(1, 5, 1, 1);
        Transaction.relatedCustomer(1, 1, 1);
        Connector.close();
    }

}

//todo
//set up a mongodb shared cluster: https://docs.mongodb.com/manual/tutorial/deploy-shard-cluster/