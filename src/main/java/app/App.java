package app;

import com.datastax.driver.core.Session;

import org.apache.log4j.BasicConfigurator;

import java.math.BigDecimal;

public class App {   
    static Session s = new Connector().connect();

    public static void main(String args[]){
        try{
            Transaction.paymentTransaction(5,5,5, BigDecimal.valueOf(13));
        }
        catch (Exception e ){
            System.out.println(e.toString());
        }
        BasicConfigurator.configure();
        try{
            Transaction.handleInput();
            Transaction.handleOutput();
        } finally {
            s.close();
        }
    }

}