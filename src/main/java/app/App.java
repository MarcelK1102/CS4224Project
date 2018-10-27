package app;

public class App {   

    public static void main(String args[]){
        // if(args.length > 0 && "quorum".equals(args[0].toLowerCase()))
        //     Connector.connect(ConsistencyLevel.QUORUM);
        // else
        //     Connector.connect(ConsistencyLevel.ONE);
        /*try{   
            Client.handleInput(args.length > 1 ? Integer.parseInt(args[1]) : -1);
        } finally {
            Connector.close();
            Client.printStats();
        }
        return;*/

        Connector.connect();
        System.out.println("connected");
        Transaction.stockLevel(1, 5, 1, 1);
        Connector.close();
    }

}

//todo
//set up a mongodb shared cluster: https://docs.mongodb.com/manual/tutorial/deploy-shard-cluster/