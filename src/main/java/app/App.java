package app;

import com.datastax.driver.core.ConsistencyLevel;

public class App {   

    public static void main(String args[]){
        if(args.length > 0 && "quorum".equals(args[0].toLowerCase()))
            Connector.connect(ConsistencyLevel.QUORUM);
        else
            Connector.connect(ConsistencyLevel.ONE);
        // BasicConfigurator.configure();
        try{
            Transaction.handleInput();
        } finally {
            Connector.close();
            double timeSeconds = (Transaction.endTime - Transaction.startTime) / 1000.0;
            System.out.println("#!#!STATS: number of transactions : " + Transaction.nxact + ", total transaction execution time : " + timeSeconds + " seconds, transaction throughput : " + ( (Transaction.nxact)/timeSeconds) );
        }
        return;
    }

}