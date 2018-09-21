package app;

import java.math.BigDecimal;
import java.sql.Date;

import java.time.Instant;
import java.util.List;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;

public class Delivery {

    Connector cn = new Connector();

    public class TransactionException extends Exception {
        public TransactionException(String message) { super(message); }
    }

    public void processDelivery(int wid, int carrierid) throws TransactionException{
        Session s = cn.connect();
        Wrapper w = new Wrapper(s);
        for (int districtNo = 1; districtNo <= 10; districtNo++){
            //a)
            int N = s.execute(QueryBuilder
            .select().min("O_ID")
            .from(Connector.keyspace, "orders")
            .where(QueryBuilder.eq("O_W_ID", wid))
            .and(QueryBuilder.eq("D_ID", districtNo))).one().getInt(0);

            Row X = w.findOrder(wid, districtNo, N).orElseThrow(() -> new TransactionException("Unable to find order with id:" + N));
            int cid = X.getInt("O_C_ID");
            Row C = w.findCustomer(wid, districtNo, cid).orElseThrow(() -> new TransactionException("Unable to find customer with id:" + cid));

            //b)
            s.execute(QueryBuilder.update(Connector.keyspace, "orders")
            .with(QueryBuilder.set("O_CARRIER_ID", carrierid))
            .where(QueryBuilder.eq("O_W_ID", wid))
            .and(QueryBuilder.eq("O_D_ID", districtNo))
            .and(QueryBuilder.eq("O_ID", N)));

            //c)
            s.execute(QueryBuilder.update(Connector.keyspace, "order_line")
            .with(QueryBuilder.set("OL_DELIVERY_D", QueryBuilder.now()))
            .where(QueryBuilder.eq("OL_W_ID",wid))
            .and(QueryBuilder.eq("OL_O_ID", N)));

            //d)
            int delivery_cnt = C.getInt("C_DELIVERY_CNT");
            BigDecimal c_balance = C.getDecimal("C_BALANCE");
            BigDecimal B = s.execute(QueryBuilder.select()
            .sum("OL_AMOUNT")
            .from("order_line")
            .where(QueryBuilder.eq("OL_W_ID",wid))
            .and(QueryBuilder.eq("OL_O_ID", N))).one().getDecimal(0);

            s.execute(QueryBuilder.update(Connector.keyspace, "costumer")
            .with(QueryBuilder.set("C_BALANCE", c_balance.add(B)))
            .and(QueryBuilder.set("C_DELIVERY_CNT", delivery_cnt+1))
            .where(QueryBuilder.eq("C_W_ID", wid))
            .and(QueryBuilder.eq("C_D_ID", districtNo))
            .and(QueryBuilder.eq("C_ID", cid)));
        }
    }
}
