package app;

import java.util.Optional;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;

public class Wrapper {
    Session s;
    public Wrapper(Session s){
        this.s = s;
    }

    public abstract class tablebase {
        String names[];
        Object values[];
        int nkeys;
    }  

    public Optional<Row> findWarehouse(int wid, String ... attr){
        return Optional.ofNullable(s.execute(
            (attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select().all())
                .from("warehouse")
                .where(QueryBuilder.eq("W_ID", wid)))
            .one());
    }
    public Optional<Row> findDistrict(int wid, int did, String ... attr){
        return Optional.ofNullable(s.execute(
            (attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select().all())
                .from(Connector.keyspace, "district")
                .where(QueryBuilder.eq("D_W_ID", wid))
                .and(QueryBuilder.eq("D_ID", did))
        ).one());
    }

    public Optional<Row> findCustomer(int wid, int did, int cid, String ... attr){
        return Optional.ofNullable(s.execute(
            (attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select().all())
            .from(Connector.keyspace, "customer")
            .where(QueryBuilder.eq("C_W_ID", wid))
            .and(QueryBuilder.eq("C_D_ID", did))
            .and(QueryBuilder.eq("C_ID", cid))
        ).one());
    }

    public Optional<Row> findOrder(int wid, int did, int oid, String ... attr){
        return Optional.ofNullable(s.execute(
            (attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select().all())
                .from(Connector.keyspace, "orders")
                .where(QueryBuilder.eq("O_W_ID", wid))
                .and(QueryBuilder.eq("O_D_ID", did))
                .and(QueryBuilder.eq("O_ID", oid))
        ).one());
    }

    public Optional<Row> findItem(int iid, String ... attr){
        return Optional.ofNullable(s.execute(
            (attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select().all())
                .from(Connector.keyspace, "item")
                .where(QueryBuilder.eq("I_ID", iid))
        ).one());
    }

    public Optional<Row> findOrderLine(int wid, int did, int oid, int olid, String ... attr){
        return Optional.ofNullable(s.execute(
            (attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select().all())
                .from(Connector.keyspace, "order_line")
                .where(QueryBuilder.eq("OL_W_ID", wid))
                .and(QueryBuilder.eq("OL_D_ID", did))
                .and(QueryBuilder.eq("OL_O_ID", oid))
                .and(QueryBuilder.eq("OL_NUMBER", olid))
        ).one());    
    }

    public Optional<Row> findStock(int wid, int iid, String ... attr){
        return Optional.ofNullable(s.execute(
            (attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select().all())
                .from(Connector.keyspace, "stock")
                .where(QueryBuilder.eq("S_W_ID", wid))
                .and(QueryBuilder.eq("S_I_ID", iid))
        ).one());
    }
}