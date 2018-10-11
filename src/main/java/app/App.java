package app;

import com.datastax.driver.core.ConsistencyLevel;

public class App {   

    public static void main(String args[]){
        if(args.length > 0 && "quorum".equals(args[0].toLowerCase()))
            Connector.connect(ConsistencyLevel.QUORUM);
        else
            Connector.connect(ConsistencyLevel.ONE);
        try{
            Client.handleInput(args.length > 1 ? Integer.parseInt(args[1]) : -1);
        } finally {
            Connector.close();
            Client.printStats();
        }
        return;
    }

}