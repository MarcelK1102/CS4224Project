package app;

import org.apache.log4j.BasicConfigurator;

public class App {   

    public static void main(String args[]){
        Connector.connect();
        BasicConfigurator.configure();
        try{
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