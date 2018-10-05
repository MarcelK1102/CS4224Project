package app;

import com.datastax.driver.core.Session;

import org.apache.log4j.BasicConfigurator;

public class App {   
    static Connector c = new Connector();
    static Session s = c.connect();

    public static void main(String args[]){
        BasicConfigurator.configure();
        try{
            // Transaction.handleInput();
            // Transaction.handleOutput();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            c.close();
        }
        return;
    }

}