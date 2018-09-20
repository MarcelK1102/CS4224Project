package app;

import java.util.Arrays;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class Wrapper {
    Session s;
    public Wrapper(Session s){
        this.s = s;
    }

    public Row findWarehouse(int wid, String attr){
        return s.execute(s.prepare(
            "select " + attr + " from warehouse where S_W_ID = ?;")
            .bind(wid)).one();
    }

    public Row findDistrict(int wid, int did, String attr){
        return s.execute(s.prepare(
            "select " + attr + " from district where D_W_ID = ? and D_ID = ?;")
            .bind(wid,did)).one();
    }

    public Row findCustomer(int wid, int did, int cid, String attr){
        return s.execute(s.prepare(
            "select " + attr + " from customer where C_W_ID = ? and C_D_ID = ? and C_ID = ?"
            ).bind()
                .setList(0, Arrays.asList(attr), String.class)
                .bind(wid, did, cid)).one();
    }

    public Row findOrder(int wid, int did, int oid, String attr){
        return s.execute(s.prepare(
            "select " + attr + " from order where O_W_ID = ? and O_D_ID = ? and O_ID = ?"
            ).bind(wid,did,oid)).one();
    }

    public Row findItem(int iid, String attr){
        return s.execute(s.prepare(
            "select " + attr + " from item where I_ID = ?;"
            ).bind(iid)).one();
    }

    public Row findOrderLine(int wid, int did, int oid, int olid, String attr){
        return s.execute(s.prepare(
            "select " + attr + " from order_line where OL_W_ID = ? and OL_D_ID = ? and OL_O_ID = ? and OL_NUMBER = ?;"
            ).bind(wid, did, oid, olid)).one();
    }

    public Row findStock(int wid, int iid, String attr){
        return s.execute(s.prepare(
            "select " + attr + " from stock where S_W_ID = ? and S_I_ID = ?;"
            ).bind(wid, iid)).one();
    }
}