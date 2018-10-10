package app;

import com.datastax.driver.core.ConsistencyLevel;

public class App {   

    public static void main(String args[]){
        if(args.length > 0 && "quorum".equals(args[0].toLowerCase()))
            Connector.connect(ConsistencyLevel.QUORUM);
        else
            Connector.connect(ConsistencyLevel.ONE);
        int limit = args.length > 1 ? Integer.parseInt(args[1]) : -1;
        // BasicConfigurator.configure();
        try{
            Transaction.handleInput(limit);
        } finally {
            Connector.close();
            double timeSeconds = (Transaction.endTime - Transaction.startTime) / 1000.0;
            System.out.println("#!#!STATS: number of transactions : " + Transaction.nxact + ", total transaction execution time : " + timeSeconds + " seconds, transaction throughput : " + ( (Transaction.nxact)/timeSeconds) );
            for(char c :Transaction.fs.keySet()){
                timeSeconds = Transaction.times[c]/1000.0;
                System.out.printf("Transaction %c was executed %d times, took a total of %f seconds, average time per transaction was %f\n",
                    c,
                    Transaction.counts[c],
                    timeSeconds,
                    timeSeconds > 0.0 ? Transaction.counts[c]/timeSeconds : 0.0
                    );
            }
            System.out.println("New Order Transaction times:");
            for(int i = 0; i < Transaction.newOrderTimes.length; i++){
                long t = Transaction.newOrderTimes[i];
                System.out.println("point " + i + " took " + (t/1000.0) + " seconds");
            }
        }
        return;
    }

}