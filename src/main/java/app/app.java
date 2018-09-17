package app;

import com.datastax.driver.core.Session;

public class app {
    public static void main(String args[]){
        Connector cn = new Connector();
        Session s = cn.connect();
        s.execute("describe tables");
    }
}