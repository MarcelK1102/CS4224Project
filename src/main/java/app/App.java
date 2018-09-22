package app;

import java.math.BigDecimal;
import java.util.Arrays;

import app.Transaction.TransactionException;

public class App {
    public static void main(String args[]){
        Transaction t = new Transaction();
        BigDecimal T = new BigDecimal(1000.0);
        try{t.newOrder(1, 5, 10, Arrays.asList(1,2,3), Arrays.asList(1,1,1), Arrays.asList(5,6,7));} 
        catch(TransactionException te){System.out.println(te);}*/
        //System.out.println(t.stockLevel(5, 1, T, 100));

        // RelatedCustomer r = new RelatedCustomer();
        // r.RelatedCustomer(5,1,1);

        System.out.println("delivery: ");
        try{t.processDelivery(1, 1);} 
        catch(app.Delivery.TransactionException te){System.out.println(te);}

        System.out.println("oder Status: ");
        try{t.getOrderStatus(1, 5, 1);}
        catch(app.OrderStatus.TransactionException te){System.out.println(te);}
        
        return;
    }
}