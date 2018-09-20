package app;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

import java.util.Iterator;
import java.util.List;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class Transaction {
    Connector cn = new Connector();

    public void newOrder(int wid, int did, int cid, List<Integer> ids, List<Integer> wids, List<Integer> quantities ){
        Session s = cn.connect();
        Wrapper w = new Wrapper(s);
        int N = w.findDistrict(wid, did, "D_NEXT_O_ID").getInt(0);
        // https://stackoverflow.com/questions/3935915/how-to-create-auto-increment-ids-in-cassandra/29391877#29391877
        while(!s.execute(
            s.prepare("update district set D_NEXT_O_ID = ? where D_W_ID = ? and D_ID = ? if D_NEXT_O_ID = ?;")
            .bind(++N, wid, did, N-1)).one().getBool(0)){}

        s.execute(
            s.prepare("insert into orders(O_ID, O_D_ID, O_W_ID, O_C_ID, O_ENTRY_D, O_OL_CNT, O_ALL_LOCAL)" 
                      + " values (?, ?, ?, ?, toTimeStamp(now()), ?, ?)")
                      .bind(N, did, wid, cid, ids.size(), wids.stream().allMatch(i -> i==wid) ? 1 : 0)
        );


        int totalAmount = 0;
        for(int i = 0; i < ids.size(); i++){
            Row r;
            // while(true){
                r = w.findStock(wid, ids.get(i), "S_QUANTITY, S_YTD, S_ORDER_CNT, S_REMOTE_CNT, S_DIST_" + String.format("%02d", did));
                if(r == null){
                    System.out.println("Unable to find stock for wid:" + wid + " iID = " + ids.get(i));
                    continue;
                }
                BigDecimal quantity = r.getDecimal("S_QUANTITY").subtract(BigDecimal.valueOf(quantities.get(i)));
                quantity.add(BigDecimal.valueOf(quantity.doubleValue() < 10.0 ? 100.0 : 0.0));
                BigDecimal ytd = r.getDecimal("S_YTD").add(BigDecimal.valueOf(quantities.get(i)));
                int orderCnt = r.getInt("S_ORDER_CNT") + 1;
                int remoteCnt = r.getInt("S_REMOTE_CNT") + (wid != wids.get(i) ? 1 : 0);
                System.out.println(quantity + " " + ytd + " " + orderCnt + " " + remoteCnt);
            }
        //         r = s.execute(s.prepare(
        //             "update stock set S_QUANTITY = ?, S_YTD = ?, S_ORDER_CNT = ?, S_REMOTE_CNT = ? where S_I_ID = ? and S_W_ID = ? if S_QUANTITY = ?"
        //             ).bind(quantity, ytd, orderCnt, remoteCnt, ids.get(i), wid, r.getInt(0))
        //         ).one();
        //         if(r.getBool(0))
        //             break;
        //     }
        //     int itemAmount = quantities.get(i) * s.execute(
        //         s.prepare("select I_PRICE from item where I_ID = ?").bind(ids.get(i))
        //     ).one().getInt(0);
        //     totalAmount += itemAmount;
        //     s.execute(s.prepare(
        //         "insert into order_line (OL_W_ID, OL_D_ID, OL_O_ID, OL_NUMBER, OL_I_ID, OL_AMOUNT, OL_SUPPLY_W_ID, OL_QUANTITY, OL_DIST_INFO) " +
        //         "values  (?, ?, ?, ?, ?, ?, ?, ?, ?)"
        //         ).bind(N, did, wid, i, ids.get(i), wids.get(i), quantities.get(i), itemAmount, r.getInt("S_DIST"))
        //     );
        //     int dTax = s.execute("select D_TAX ")
        // }
    }

    public long stockLevel(int wid, int did, BigDecimal t, int l){
        Session s = cn.connect();
        Wrapper w = new Wrapper(s);
        return w.findDistrict(wid, did, "D_NEXT_O_ID").getInt(0);
        // s.execute("start transaction;");

        // int n = s.execute(
        //     s.prepare("select D_NEXT_O_ID from district where D_W_ID = ? and D_ID = ?;")
        //     .bind(wid, did)
        //     ).all()
        //     .get(0)
        //     .getInt(0);

        // ResultSet S = s.execute( s.prepare("select OL_I_ID from order_line where OL_D_ID = ? and OL_W_ID = ? and OL_O_ID < ? and OL_O_ID >= ? allow filtering;")
        //     .bind(did, wid, n, n-l));
        
        // long sum = 0;
        // Iterator<Row> rows = S.iterator();
        // while(rows.hasNext()){
        //     int sid = rows.next().getInt(0);
        //     System.out.println(sid);
        //     s.execute(s.prepare("select count(S_I_ID) from stock where S_I_ID = ? and S_QUANTITY < ? allow filtering;")
        //     .bind(sid, t));
        // }
        // s.close();

        return sum;
    }
}