package app;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;

import java.util.Iterator;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class Transaction {
    Connector cn = new Connector();

    public long stockLevel(int wid, int did, BigDecimal t, int l){
        Session s = cn.connect();
        // s.execute("start transaction;");
        int n = s.execute(
            s.prepare("select D_NEXT_O_ID from district where D_W_ID = ? and D_ID = ?;")
            .bind(wid, did)
            ).all()
            .get(0)
            .getInt(0);

        ResultSet S = s.execute( s.prepare("select OL_I_ID from order_line where OL_D_ID = ? and OL_W_ID = ? and OL_O_ID < ? and OL_O_ID >= ? allow filtering;")
            .bind(did, wid, n, n-l));
        
        long sum = 0;
        Iterator<Row> rows = S.iterator();
        while(rows.hasNext()){
            int sid = rows.next().getInt(0);
            System.out.println(sid);
            s.execute(s.prepare("select count(S_I_ID) from stock where S_I_ID = ? and S_QUANTITY < ? allow filtering;")
            .bind(sid, t));
        }
        s.close();

        return sum;
    }
}