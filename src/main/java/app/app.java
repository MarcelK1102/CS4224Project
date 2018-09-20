package app;

import java.math.BigDecimal;
import java.util.Arrays;

public class App {
    public static void main(String args[]){
        Transaction t = new Transaction();
        BigDecimal T = new BigDecimal(1000.0);
        t.newOrder(1, 5, 10, Arrays.asList(1,2,3), Arrays.asList(1,1,1), Arrays.asList(5,6,7));
        return;
    }
}