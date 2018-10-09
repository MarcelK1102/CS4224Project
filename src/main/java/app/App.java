package app;

import com.datastax.driver.core.ConsistencyLevel;

import org.apache.log4j.BasicConfigurator;

public class App {   

    public static void main(String args[]){
        if(args.length > 0 && "quorum".equals(args[0].toLowerCase()))
            Connector.connect(ConsistencyLevel.QUORUM);
        else
            Connector.connect(ConsistencyLevel.ONE);
        BasicConfigurator.configure();
        
        try{
            Transaction.popularItem(1, 1, 900);
            Transaction.handleInput();
            
            // Transaction.handleOutput();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            Connector.close();
        }
        return;
    }

}