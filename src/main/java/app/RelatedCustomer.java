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

public class RelatedCustomer {
    Connector cn = new Connector();

    public long RelatedCustomer(int cwid, int cdid, int cid){
        Session s = cn.connect();
        // s.execute("start transaction;");
        ResultSet O = s.execute(s.prepare("select * from Order where O_W_ID = ? and O_D_ID = ? and O_C_ID = ?").bind(cwid, cdid, cid));
        Iterator it = O.iterator();
        while (it.hasNext()){
            System.out.println(it.next());
        }

        return 0;
    }
}