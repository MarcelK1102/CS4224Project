package app;

import java.math.BigDecimal;
import java.sql.Blob;
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
        System.out.println(String.format("Name: %s %s %s",C.getString("C_FIRST"), C.getString("C_MIDDLE"), C.getString("C_LAST")));
        System.out.println("Balance: " + C.getDecimal("C_BALANCE"));

        //2.
        java.util.Date lastOrderDate = s.execute(QueryBuilder.select()
        .min("O_ENTRY_D")
        .from(Connector.keyspace, "orders")
        .where(QueryBuilder.eq("O_W_ID", c_wid))
        .and(QueryBuilder.eq("O_D_ID", c_did))
        .and(QueryBuilder.eq("O_C_ID", cid))
        .allowFiltering()).one().getTimestamp(0);
        if (lastOrderDate == null){
            throw new TransactionException("No Order with valid timestamp found");
        }

        //get Order Row
        Row lastOrder = s.execute(QueryBuilder.select().all()
        .from(Connector.keyspace, "orders")
        .where(QueryBuilder.eq("O_W_ID", c_wid))
        .and(QueryBuilder.eq("O_D_ID", c_did))
        .and(QueryBuilder.eq("O_ENTRY_D", lastOrderDate))
        .allowFiltering()).one();

        int oid = lastOrder.getInt("O_ID");
        System.out.println("O_ID: " + oid);
        System.out.println("O_ENTRY_ID: " + lastOrder.getTimestamp("O_ENTRY_D"));
        System.out.println("O_CARRIER_ID: " + lastOrder.getInt("O_CARRIER_ID"));

        //3.
        ResultSet orderlines = s.execute(QueryBuilder.select().all()
            .from(Connector.keyspace, "order_line")
            .where(QueryBuilder.eq("OL_W_ID",c_wid))
            .and(QueryBuilder.eq("OL_D_ID", c_did))
            .and(QueryBuilder.eq("OL_O_ID", oid)));

        Iterator<Row> it = orderlines.iterator();
        while(it.hasNext()){
            Row currOL = it.next();
            System.out.println("O_I_ID: " + currOL.getInt("O_I_ID"));
            System.out.println("OL_SUPPLY_W_ID: " + currOL.getInt("OL_SUPPLY_W_ID"));
            System.out.println("OL_QUANTITY:"  + currOL.getInt("OL_QUANTITY"));
            System.out.println("OL_AMOUNT:"  + currOL.getInt("OL_AMOUNT"));
            System.out.println("OL_DELIVERY_D:"  + currOL.getTimestamp("OL_DELIVERY_D"));
        }

    }
}