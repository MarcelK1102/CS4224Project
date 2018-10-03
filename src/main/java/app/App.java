package app;

import com.datastax.driver.core.Session;

import org.apache.log4j.BasicConfigurator;

import java.math.BigDecimal;

public class App {   
    static Session s = new Connector().connect();

    public static void main(String args[]){

        BasicConfigurator.configure();
        try{
            Transaction.set(s);
            Transaction.paymentTransaction(5,5,5, BigDecimal.valueOf(13));
            Transaction.processDelivery(5,7);
        }
        catch (Exception e ){
            System.out.println(e.toString());
        }
        try{
            Transaction.handleInput();
            Transaction.handleOutput();
        } finally {
            s.close();
        }
    }

}