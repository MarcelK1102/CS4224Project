package app;

import com.datastax.driver.core.Session;

public class App {   
    static Session s = new Connector().connect();

    public static void main(String args[]){
        Transaction.set(s);
        try{
            Transaction.handleInput();
            Transaction.handleOutput();
        } finally {
            s.close();
        }
    }

}