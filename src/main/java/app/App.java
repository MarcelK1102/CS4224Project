package app;

import com.datastax.driver.core.Session;

import org.apache.log4j.BasicConfigurator;

public class App {   
    static Session s = new Connector().connect();

    public static void main(String args[]){
        BasicConfigurator.configure();
        try{
            Transaction.handleInput();
            Transaction.handleOutput();
        } finally {
            s.close();
        }
    }

}