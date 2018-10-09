package app;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.QueryBuilder;

public class Wrapper {

    public static Row findWarehouse(int wid, String ... attr){
        return Connector.s.execute(
            (attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select().all())
                .from("warehouse")
                .where(QueryBuilder.eq("W_ID", wid)))
            .one();
    }
    public static Row findDistrict(int wid, int did, String ... attr){
        return Connector.s.execute(
            (attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select().all())
                .from("district")
                .where(QueryBuilder.eq("D_W_ID", wid))
                .and(QueryBuilder.eq("D_ID", did))
        ).one();
    }

    public static Row findCustomer(int wid, int did, int cid, String ... attr){
        return Connector.s.execute(
            (attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select().all())
            .from("customer")
            .where(QueryBuilder.eq("C_W_ID", wid))
            .and(QueryBuilder.eq("C_D_ID", did))
            .and(QueryBuilder.eq("C_ID", cid))
        ).one();
    }

    public static Row findOrder(int wid, int did, int oid, String ... attr){
        return Connector.s.execute(
            (attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select().all())
                .from("orders")
                .where(QueryBuilder.eq("O_W_ID", wid))
                .and(QueryBuilder.eq("O_D_ID", did))
                .and(QueryBuilder.eq("O_ID", oid))
        ).one();
    }

    public static Row findItem(int iid, String ... attr){
        return Connector.s.execute(
            (attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select().all())
                .from("item")
                .where(QueryBuilder.eq("I_ID", iid))
        ).one();
    }

    public static Row findOrderLine(int wid, int did, int oid, int olid, String ... attr){
        return Connector.s.execute(
            (attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select().all())
                .from("order_line")
                .where(QueryBuilder.eq("OL_W_ID", wid))
                .and(QueryBuilder.eq("OL_D_ID", did))
                .and(QueryBuilder.eq("OL_O_ID", oid))
                .and(QueryBuilder.eq("OL_NUMBER", olid))
        ).one();    
    }

    public static Row findStock(int wid, int iid, String ... attr){
        return Connector.s.execute(
            (attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select().all())
                .from("stock")
                .where(QueryBuilder.eq("S_W_ID", wid))
                .and(QueryBuilder.eq("S_I_ID", iid))
        ).one();
    }
}