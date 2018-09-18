package app;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class app {
    public static void main(String args[]){
        Connector cn = new Connector();
        Session s = cn.connect();
        ResultSet res = s.execute("select * from warehouse");
        for(Row p : res.all()){
            System.out.println(p);
        }
    }
}