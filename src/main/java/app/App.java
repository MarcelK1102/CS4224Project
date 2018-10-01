package app;

import com.datastax.driver.core.Session;

public class App {   
    static Session s = new Connector().connect();

    public static void main(String args[]){
        Transaction.set(s);
        Transaction.paymentTransaction(5,5,5,10);

        try{
            Transaction.handleInput();
            Transaction.handleOutput();
        } finally {
            s.close();
        }
    }

}