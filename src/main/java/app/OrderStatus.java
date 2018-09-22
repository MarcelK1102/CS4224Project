package app;

import java.math.BigDecimal;
import java.sql.Date;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;

import com.datastax.driver.core.LocalDate;
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

        //1.
        Row C = w.findCustomer(c_wid, c_did, cid).orElseThrow(() -> new TransactionException("Unable to find customer with id:" + cid));
        System.out.println(String.format("%s %s %s",C.getString("C_FIRST"), C.getString("C_MIDDLE"), C.getString("C_LAST")));
        
        //2.
        LocalDate lastOrderDate = s.execute(QueryBuilder.select()
        .min("O_ENTRY_D")
        .from(Connector.keyspace, "orders")
        .where(QueryBuilder.eq("O_W_ID", c_wid))
        .and(QueryBuilder.eq("O_D_ID", c_did))).one().getDate(0);

        //get Order Row
        Row lastOrder = s.execute(QueryBuilder.select().all()
        .from(Connector.keyspace, "orders")
        .where(QueryBuilder.eq("O_W_ID", c_wid))
        .and(QueryBuilder.eq("O_D_ID", c_did))
        .and(QueryBuilder.eq("O_ENTRY_D", lastOrderDate))
        .allowFiltering()).one();

        System.out.println("O_ID: " + lastOrder.getInt("O_ID"));
        System.out.println("O_ENTRY_ID: " + lastOrder.getTime("O_ENTRY_D"));
        System.out.println("O_CARRIER_ID: " + lastOrder.getInt("O_CARRIER_ID"));
    }
}