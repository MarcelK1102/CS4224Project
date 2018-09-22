package app;

import java.math.BigDecimal;
import java.sql.Date;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;

public class OrderStatus{

    Connector cn = new Connector();

    public class TransactionException extends Exception {
        public TransactionException(String message) { super(message); }
    }

    public void getOrderStatus(int c_wid, int c_did, int cid) throws TransactionException {
        Session s = cn.connect();
        Wrapper w = new Wrapper(s);

        Row C = w.findCustomer(c_wid, c_did, cid).orElseThrow(() -> new TransactionException("Unable to find customer with id:" + cid));
        System.out.println(String.format("%s %s %s",C.getString("C_FIRST"), C.getString("C_MIDDLE"), C.getString("C_LAST")));

    }
}