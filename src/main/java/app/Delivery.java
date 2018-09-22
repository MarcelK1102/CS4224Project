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

public class Delivery {

    Connector cn = new Connector();

    public class TransactionException extends Exception {
        public TransactionException(String message) { super(message); }
    }

    public void processDelivery(int wid, int carrierid) throws TransactionException {
        Session s = cn.connect();
        Wrapper w = new Wrapper(s);
        for (int districtNo = 1; districtNo <= 10; districtNo++){
            System.out.println("district: " + districtNo);
            //a)
            int N = s.execute(QueryBuilder
            .select().min("O_ID")
            .from(Connector.keyspace, "orders")
            .where(QueryBuilder.eq("O_W_ID", wid))
            .and(QueryBuilder.eq("O_D_ID", districtNo))
            .and(QueryBuilder.eq("O_CARRIER_ID", -1))
            .allowFiltering())
            .one().getInt(0);

            System.out.println(N);

            Row X;
            try {
                X = w.findOrder(wid, districtNo, N)
                        .orElseThrow(() -> new TransactionException("Unable to find order with id:" + N));
            } catch (TransactionException e) {
                //skip if there is no order with id N
                continue;
            }
            int cid = X.getInt("O_C_ID");
            Row C = w.findCustomer(wid, districtNo, cid).orElseThrow(() -> new TransactionException("Unable to find customer with id:" + cid));

            //b)
            System.out.println("district: " + districtNo + " :b");
            s.execute(QueryBuilder.update(Connector.keyspace, "orders")
            .with(QueryBuilder.set("O_CARRIER_ID", carrierid))
            .where(QueryBuilder.eq("O_W_ID", wid))
            .and(QueryBuilder.eq("O_D_ID", districtNo))
            .and(QueryBuilder.eq("O_ID", N)));

            //c)
            long timestamp = System.currentTimeMillis();
            System.out.println("district: " + districtNo + " :c");

            ResultSet orderlines = s.execute(QueryBuilder.select().all()
            .from(Connector.keyspace, "order_line")
            .where(QueryBuilder.eq("OL_W_ID",wid))
            .and(QueryBuilder.eq("OL_D_ID", districtNo))
            .and(QueryBuilder.eq("OL_O_ID", N)));

            Iterator<Row> it = orderlines.iterator();
            while(it.hasNext()){
                Row currOL = it.next();
                int OL_Number = currOL.getInt("OL_NUMBER");

                s.execute(QueryBuilder.update(Connector.keyspace, "order_line")
                .with(QueryBuilder.set("OL_DELIVERY_D", Date.from(Instant.now())))
                .where(QueryBuilder.eq("OL_W_ID",wid))
                .and(QueryBuilder.eq("OL_D_ID", districtNo))
                .and(QueryBuilder.eq("OL_O_ID", N))
                .and(QueryBuilder.eq("OL_NUMBER", OL_Number)));
            }

            //d)
            System.out.println("district: " + districtNo + " :d");

            int delivery_cnt = C.getInt("C_DELIVERY_CNT");
            BigDecimal c_balance = C.getDecimal("C_BALANCE");

            System.out.println("delivercnt:" + delivery_cnt);
            System.out.println("c_balance:" + c_balance.toString());

            BigDecimal B = s.execute(QueryBuilder.select()
            .sum("OL_AMOUNT")
            .from("order_line")
            .where(QueryBuilder.eq("OL_W_ID",wid))
            .and(QueryBuilder.eq("OL_O_ID", N))
            .allowFiltering())
            .one().getDecimal(0);

            System.out.println("B:" + B.toString());

            s.execute(QueryBuilder.update(Connector.keyspace, "costumer")
            .with(QueryBuilder.set("C_BALANCE", c_balance.add(B)))
            .and(QueryBuilder.set("C_DELIVERY_CNT", delivery_cnt+1))
            .where(QueryBuilder.eq("C_W_ID", wid))
            .and(QueryBuilder.eq("C_D_ID", districtNo))
            .and(QueryBuilder.eq("C_ID", cid)));
            
            System.out.println("district: " + districtNo + " finished");
        }
    }
}
