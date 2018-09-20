package app;

import java.math.BigDecimal;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class App {
    public static void main(String args[]){
        /*Transaction t = new Transaction();
        BigDecimal T = new BigDecimal(1000.0);
        System.out.println(t.stockLevel(5, 1, T, 100));*/

        RelatedCustomer r = new RelatedCustomer();
        r.RelatedCustomer(5,1,1);

        return;
    }
}