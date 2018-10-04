package app;

import java.util.Iterator;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;

public class RelatedCustomer {
    Connector cn = new Connector();

    public long RelatedCustomer(int cwid, int cdid, int cid){
        Session s = cn.connect();
        // s.execute("start transaction;");
        ResultSet Customers = s.execute(s.prepare("select * from customers where C_W_ID != ? allow filtering").bind(cwid));
        ResultSet O = s.execute(s.prepare("select * from orders where O_W_ID = ? and O_D_ID = ? and O_C_ID = ? allow filtering").bind(cwid, cdid, cid));
        
        
        
        
        
        Iterator it = O.iterator();
        while (it.hasNext()){
            System.out.println(it.next());
        }

        return 0;
    }
}