package app;

import com.datastax.driver.core.Session;

import java.math.BigDecimal;

public class App {   
    static Session s = new Connector().connect();

    public static void main(String args[]){
        Transaction.set(s);
        try{
            Transaction.popularItem(5,1,5);

        }catch (Exception e){
            System.err.println(e);
        }
        /*try{
            Transaction.handleInput();
            Transaction.handleOutput();
        } finally {
            s.close();
        }*/
    }

}