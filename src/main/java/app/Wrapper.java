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
    

    public class warehouse extends tablebase{
        public Integer W_ID = null;
        public java.lang.String W_NAME = null;
        public java.lang.String W_STREET_1 = null;
        public java.lang.String W_STREET_2 = null;
        public java.lang.String W_CITY = null;
        public java.lang.String W_STATE = null;
        public java.lang.String W_ZIP = null;
        public java.math.BigDecimal W_TAX = null;
        public java.math.BigDecimal W_YTD = null;
        super.names = new String[] {"W_ID", "W_NAME", "W_STREET_1", "W_STREET_2", "W_CITY", "W_STATE", "W_ZIP", "W_TAX", "W_YTD"};
        Object values[] = new Object[] {W_ID, W_NAME, W_STREET_1, W_STREET_2, W_CITY, W_STATE, W_ZIP, W_TAX, W_YTD};
        int nkeys = 1;
        public warehouse () {}
        public warehouse(Integer W_ID, java.lang.String W_NAME, java.lang.String W_STREET_1, java.lang.String W_STREET_2, java.lang.String W_CITY, java.lang.String W_STATE, java.lang.String W_ZIP, java.math.BigDecimal W_TAX, java.math.BigDecimal W_YTD) { this.W_ID = W_ID; this.W_NAME = W_NAME; this.W_STREET_1 = W_STREET_1; this.W_STREET_2 = W_STREET_2; this.W_CITY = W_CITY; this.W_STATE = W_STATE; this.W_ZIP = W_ZIP; this.W_TAX = W_TAX; this.W_YTD = W_YTD; }
        public warehouse(Row r) throws NullPointerException { this(r.isNull("W_ID") ? null : r.getInt("W_ID"), r.isNull("W_NAME") ? null : r.getString("W_NAME"), r.isNull("W_STREET_1") ? null : r.getString("W_STREET_1"), r.isNull("W_STREET_2") ? null : r.getString("W_STREET_2"), r.isNull("W_CITY") ? null : r.getString("W_CITY"), r.isNull("W_STATE") ? null : r.getString("W_STATE"), r.isNull("W_ZIP") ? null : r.getString("W_ZIP"), r.isNull("W_TAX") ? null : r.getDecimal("W_TAX"), r.isNull("W_YTD") ? null : r.getDecimal("W_YTD")); }
    }
    
    public class district extends tablebase{
        public Integer D_W_ID = null;
        public Integer D_ID = null;
        public java.lang.String D_NAME = null;
        public java.lang.String D_STREET_1 = null;
        public java.lang.String D_STREET_2 = null;
        public java.lang.String D_CITY = null;
        public java.lang.String D_STATE = null;
        public java.lang.String D_ZIP = null;
        public java.math.BigDecimal D_TAX = null;
        public java.math.BigDecimal D_YTD = null;
        public Integer D_NEXT_O_ID = null;
        String names[] = new String[] {"D_W_ID", "D_ID", "D_NAME", "D_STREET_1", "D_STREET_2", "D_CITY", "D_STATE", "D_ZIP", "D_TAX", "D_YTD", "D_NEXT_O_ID"};
        Object values[] = new Object[] {D_W_ID, D_ID, D_NAME, D_STREET_1, D_STREET_2, D_CITY, D_STATE, D_ZIP, D_TAX, D_YTD, D_NEXT_O_ID};
        int nkeys = 2;
        public district () {}
        public district(Integer D_W_ID, Integer D_ID, java.lang.String D_NAME, java.lang.String D_STREET_1, java.lang.String D_STREET_2, java.lang.String D_CITY, java.lang.String D_STATE, java.lang.String D_ZIP, java.math.BigDecimal D_TAX, java.math.BigDecimal D_YTD, Integer D_NEXT_O_ID) { this.D_W_ID = D_W_ID; this.D_ID = D_ID; this.D_NAME = D_NAME; this.D_STREET_1 = D_STREET_1; this.D_STREET_2 = D_STREET_2; this.D_CITY = D_CITY; this.D_STATE = D_STATE; this.D_ZIP = D_ZIP; this.D_TAX = D_TAX; this.D_YTD = D_YTD; this.D_NEXT_O_ID = D_NEXT_O_ID; }
        public district(Row r) throws NullPointerException { this(r.isNull("D_W_ID") ? null : r.getInt("D_W_ID"), r.isNull("D_ID") ? null : r.getInt("D_ID"), r.isNull("D_NAME") ? null : r.getString("D_NAME"), r.isNull("D_STREET_1") ? null : r.getString("D_STREET_1"), r.isNull("D_STREET_2") ? null : r.getString("D_STREET_2"), r.isNull("D_CITY") ? null : r.getString("D_CITY"), r.isNull("D_STATE") ? null : r.getString("D_STATE"), r.isNull("D_ZIP") ? null : r.getString("D_ZIP"), r.isNull("D_TAX") ? null : r.getDecimal("D_TAX"), r.isNull("D_YTD") ? null : r.getDecimal("D_YTD"), r.isNull("D_NEXT_O_ID") ? null : r.getInt("D_NEXT_O_ID")); }
    }
    
    public class customer extends tablebase{
        public Integer C_W_ID = null;
        public Integer C_D_ID = null;
        public Integer C_ID = null;
        public java.lang.String C_FIRST = null;
        public java.lang.String C_MIDDLE = null;
        public java.lang.String C_LAST = null;
        public java.lang.String C_STREET_1 = null;
        public java.lang.String C_STREET_2 = null;
        public java.lang.String C_CITY = null;
        public java.lang.String C_STATE = null;
        public java.lang.String C_ZIP = null;
        public java.lang.String C_PHONE = null;
        public java.util.Date C_SINCE = null;
        public java.lang.String C_DREDIT = null;
        public java.math.BigDecimal C_CREDIT_LIM = null;
        public java.math.BigDecimal C_DISCOUNT = null;
        public java.math.BigDecimal C_BALANCE = null;
        public Float C_YTD_PAYMENT = null;
        public Integer C_PAYMENT_CNT = null;
        public Integer C_DELIVERY_CNT = null;
        public java.lang.String C_DATA = null;
        String names[] = new String[] {"C_W_ID", "C_D_ID", "C_ID", "C_FIRST", "C_MIDDLE", "C_LAST", "C_STREET_1", "C_STREET_2", "C_CITY", "C_STATE", "C_ZIP", "C_PHONE", "C_SINCE", "C_DREDIT", "C_CREDIT_LIM", "C_DISCOUNT", "C_BALANCE", "C_YTD_PAYMENT", "C_PAYMENT_CNT", "C_DELIVERY_CNT", "C_DATA"};
        Object values[] = new Object[] {C_W_ID, C_D_ID, C_ID, C_FIRST, C_MIDDLE, C_LAST, C_STREET_1, C_STREET_2, C_CITY, C_STATE, C_ZIP, C_PHONE, C_SINCE, C_DREDIT, C_CREDIT_LIM, C_DISCOUNT, C_BALANCE, C_YTD_PAYMENT, C_PAYMENT_CNT, C_DELIVERY_CNT, C_DATA};
        int nkeys = 3;
        public customer () {}
        public customer(Integer C_W_ID, Integer C_D_ID, Integer C_ID, java.lang.String C_FIRST, java.lang.String C_MIDDLE, java.lang.String C_LAST, java.lang.String C_STREET_1, java.lang.String C_STREET_2, java.lang.String C_CITY, java.lang.String C_STATE, java.lang.String C_ZIP, java.lang.String C_PHONE, java.util.Date C_SINCE, java.lang.String C_DREDIT, java.math.BigDecimal C_CREDIT_LIM, java.math.BigDecimal C_DISCOUNT, java.math.BigDecimal C_BALANCE, Float C_YTD_PAYMENT, Integer C_PAYMENT_CNT, Integer C_DELIVERY_CNT, java.lang.String C_DATA) { this.C_W_ID = C_W_ID; this.C_D_ID = C_D_ID; this.C_ID = C_ID; this.C_FIRST = C_FIRST; this.C_MIDDLE = C_MIDDLE; this.C_LAST = C_LAST; this.C_STREET_1 = C_STREET_1; this.C_STREET_2 = C_STREET_2; this.C_CITY = C_CITY; this.C_STATE = C_STATE; this.C_ZIP = C_ZIP; this.C_PHONE = C_PHONE; this.C_SINCE = C_SINCE; this.C_DREDIT = C_DREDIT; this.C_CREDIT_LIM = C_CREDIT_LIM; this.C_DISCOUNT = C_DISCOUNT; this.C_BALANCE = C_BALANCE; this.C_YTD_PAYMENT = C_YTD_PAYMENT; this.C_PAYMENT_CNT = C_PAYMENT_CNT; this.C_DELIVERY_CNT = C_DELIVERY_CNT; this.C_DATA = C_DATA; }
        public customer(Row r) throws NullPointerException { this(r.isNull("C_W_ID") ? null : r.getInt("C_W_ID"), r.isNull("C_D_ID") ? null : r.getInt("C_D_ID"), r.isNull("C_ID") ? null : r.getInt("C_ID"), r.isNull("C_FIRST") ? null : r.getString("C_FIRST"), r.isNull("C_MIDDLE") ? null : r.getString("C_MIDDLE"), r.isNull("C_LAST") ? null : r.getString("C_LAST"), r.isNull("C_STREET_1") ? null : r.getString("C_STREET_1"), r.isNull("C_STREET_2") ? null : r.getString("C_STREET_2"), r.isNull("C_CITY") ? null : r.getString("C_CITY"), r.isNull("C_STATE") ? null : r.getString("C_STATE"), r.isNull("C_ZIP") ? null : r.getString("C_ZIP"), r.isNull("C_PHONE") ? null : r.getString("C_PHONE"), r.isNull("C_SINCE") ? null : r.getTimestamp("C_SINCE"), r.isNull("C_DREDIT") ? null : r.getString("C_DREDIT"), r.isNull("C_CREDIT_LIM") ? null : r.getDecimal("C_CREDIT_LIM"), r.isNull("C_DISCOUNT") ? null : r.getDecimal("C_DISCOUNT"), r.isNull("C_BALANCE") ? null : r.getDecimal("C_BALANCE"), r.isNull("C_YTD_PAYMENT") ? null : r.getFloat("C_YTD_PAYMENT"), r.isNull("C_PAYMENT_CNT") ? null : r.getInt("C_PAYMENT_CNT"), r.isNull("C_DELIVERY_CNT") ? null : r.getInt("C_DELIVERY_CNT"), r.isNull("C_DATA") ? null : r.getString("C_DATA")); }
    }
    
    public class orders extends tablebase{
        public Integer O_W_ID = null;
        public Integer O_D_ID = null;
        public Integer O_ID = null;
        public Integer O_C_ID = null;
        public Integer O_CARRIER_ID = null;
        public java.math.BigDecimal O_OL_CNT = null;
        public java.math.BigDecimal O_ALL_LOCAL = null;
        public java.util.Date O_ENTRY_D = null;
        String names[] = new String[] {"O_W_ID", "O_D_ID", "O_ID", "O_C_ID", "O_CARRIER_ID", "O_OL_CNT", "O_ALL_LOCAL", "O_ENTRY_D"};
        Object values[] = new Object[] {O_W_ID, O_D_ID, O_ID, O_C_ID, O_CARRIER_ID, O_OL_CNT, O_ALL_LOCAL, O_ENTRY_D};
        int nkeys = 3;
        public orders () {}
        public orders(Integer O_W_ID, Integer O_D_ID, Integer O_ID, Integer O_C_ID, Integer O_CARRIER_ID, java.math.BigDecimal O_OL_CNT, java.math.BigDecimal O_ALL_LOCAL, java.util.Date O_ENTRY_D) { this.O_W_ID = O_W_ID; this.O_D_ID = O_D_ID; this.O_ID = O_ID; this.O_C_ID = O_C_ID; this.O_CARRIER_ID = O_CARRIER_ID; this.O_OL_CNT = O_OL_CNT; this.O_ALL_LOCAL = O_ALL_LOCAL; this.O_ENTRY_D = O_ENTRY_D; }
        public orders(Row r) throws NullPointerException { this(r.isNull("O_W_ID") ? null : r.getInt("O_W_ID"), r.isNull("O_D_ID") ? null : r.getInt("O_D_ID"), r.isNull("O_ID") ? null : r.getInt("O_ID"), r.isNull("O_C_ID") ? null : r.getInt("O_C_ID"), r.isNull("O_CARRIER_ID") ? null : r.getInt("O_CARRIER_ID"), r.isNull("O_OL_CNT") ? null : r.getDecimal("O_OL_CNT"), r.isNull("O_ALL_LOCAL") ? null : r.getDecimal("O_ALL_LOCAL"), r.isNull("O_ENTRY_D") ? null : r.getTimestamp("O_ENTRY_D")); }
    }
    
    public class item extends tablebase{
        public Integer I_ID = null;
        public java.lang.String I_NAME = null;
        public java.math.BigDecimal I_PRICE = null;
        public Integer I_IM_ID = null;
        public java.lang.String I_DATA = null;
        String names[] = new String[] {"I_ID", "I_NAME", "I_PRICE", "I_IM_ID", "I_DATA"};
        Object values[] = new Object[] {I_ID, I_NAME, I_PRICE, I_IM_ID, I_DATA};
        int nkeys = 1;
        public item () {}
        public item(Integer I_ID, java.lang.String I_NAME, java.math.BigDecimal I_PRICE, Integer I_IM_ID, java.lang.String I_DATA) { this.I_ID = I_ID; this.I_NAME = I_NAME; this.I_PRICE = I_PRICE; this.I_IM_ID = I_IM_ID; this.I_DATA = I_DATA; }
        public item(Row r) throws NullPointerException { this(r.isNull("I_ID") ? null : r.getInt("I_ID"), r.isNull("I_NAME") ? null : r.getString("I_NAME"), r.isNull("I_PRICE") ? null : r.getDecimal("I_PRICE"), r.isNull("I_IM_ID") ? null : r.getInt("I_IM_ID"), r.isNull("I_DATA") ? null : r.getString("I_DATA")); }
    }
    
    public class order_line extends tablebase{
        public Integer OL_W_ID = null;
        public Integer OL_D_ID = null;
        public Integer OL_O_ID = null;
        public Integer OL_NUMBER = null;
        public Integer OL_I_ID = null;
        public java.util.Date OL_DELIVERY_D = null;
        public java.math.BigDecimal OL_AMOUNT = null;
        public Integer OL_SUPPLY_W_ID = null;
        public java.math.BigDecimal OL_QUANTITY = null;
        public java.lang.String OL_DIST_INFO = null;
        String names[] = new String[] {"OL_W_ID", "OL_D_ID", "OL_O_ID", "OL_NUMBER", "OL_I_ID", "OL_DELIVERY_D", "OL_AMOUNT", "OL_SUPPLY_W_ID", "OL_QUANTITY", "OL_DIST_INFO"};
        Object values[] = new Object[] {OL_W_ID, OL_D_ID, OL_O_ID, OL_NUMBER, OL_I_ID, OL_DELIVERY_D, OL_AMOUNT, OL_SUPPLY_W_ID, OL_QUANTITY, OL_DIST_INFO};
        int nkeys = 5;
        public order_line () {}
        public order_line(Integer OL_W_ID, Integer OL_D_ID, Integer OL_O_ID, Integer OL_NUMBER, Integer OL_I_ID, java.util.Date OL_DELIVERY_D, java.math.BigDecimal OL_AMOUNT, Integer OL_SUPPLY_W_ID, java.math.BigDecimal OL_QUANTITY, java.lang.String OL_DIST_INFO) { this.OL_W_ID = OL_W_ID; this.OL_D_ID = OL_D_ID; this.OL_O_ID = OL_O_ID; this.OL_NUMBER = OL_NUMBER; this.OL_I_ID = OL_I_ID; this.OL_DELIVERY_D = OL_DELIVERY_D; this.OL_AMOUNT = OL_AMOUNT; this.OL_SUPPLY_W_ID = OL_SUPPLY_W_ID; this.OL_QUANTITY = OL_QUANTITY; this.OL_DIST_INFO = OL_DIST_INFO; }
        public order_line(Row r) throws NullPointerException { this(r.isNull("OL_W_ID") ? null : r.getInt("OL_W_ID"), r.isNull("OL_D_ID") ? null : r.getInt("OL_D_ID"), r.isNull("OL_O_ID") ? null : r.getInt("OL_O_ID"), r.isNull("OL_NUMBER") ? null : r.getInt("OL_NUMBER"), r.isNull("OL_I_ID") ? null : r.getInt("OL_I_ID"), r.isNull("OL_DELIVERY_D") ? null : r.getTimestamp("OL_DELIVERY_D"), r.isNull("OL_AMOUNT") ? null : r.getDecimal("OL_AMOUNT"), r.isNull("OL_SUPPLY_W_ID") ? null : r.getInt("OL_SUPPLY_W_ID"), r.isNull("OL_QUANTITY") ? null : r.getDecimal("OL_QUANTITY"), r.isNull("OL_DIST_INFO") ? null : r.getString("OL_DIST_INFO")); }
    }
    
    public class stock extends tablebase{
        public Integer S_W_ID = null;
        public Integer S_I_ID = null;
        public java.math.BigDecimal S_QUANTITY = null;
        public java.math.BigDecimal S_YTD = null;
        public Integer S_ORDER_CNT = null;
        public Integer S_REMOTE_CNT = null;
        public java.lang.String S_DIST_01 = null;
        public java.lang.String S_DIST_02 = null;
        public java.lang.String S_DIST_03 = null;
        public java.lang.String S_DIST_04 = null;
        public java.lang.String S_DIST_05 = null;
        public java.lang.String S_DIST_06 = null;
        public java.lang.String S_DIST_07 = null;
        public java.lang.String S_DIST_08 = null;
        public java.lang.String S_DIST_09 = null;
        public java.lang.String S_DIST_10 = null;
        public java.lang.String S_DATA = null;
        String names[] = new String[] {"S_W_ID", "S_I_ID", "S_QUANTITY", "S_YTD", "S_ORDER_CNT", "S_REMOTE_CNT", "S_DIST_01", "S_DIST_02", "S_DIST_03", "S_DIST_04", "S_DIST_05", "S_DIST_06", "S_DIST_07", "S_DIST_08", "S_DIST_09", "S_DIST_10", "S_DATA"};
        Object values[] = new Object[] {S_W_ID, S_I_ID, S_QUANTITY, S_YTD, S_ORDER_CNT, S_REMOTE_CNT, S_DIST_01, S_DIST_02, S_DIST_03, S_DIST_04, S_DIST_05, S_DIST_06, S_DIST_07, S_DIST_08, S_DIST_09, S_DIST_10, S_DATA};
        int nkeys = 3;
        public stock () {}
        public stock(Integer S_W_ID, Integer S_I_ID, java.math.BigDecimal S_QUANTITY, java.math.BigDecimal S_YTD, Integer S_ORDER_CNT, Integer S_REMOTE_CNT, java.lang.String S_DIST_01, java.lang.String S_DIST_02, java.lang.String S_DIST_03, java.lang.String S_DIST_04, java.lang.String S_DIST_05, java.lang.String S_DIST_06, java.lang.String S_DIST_07, java.lang.String S_DIST_08, java.lang.String S_DIST_09, java.lang.String S_DIST_10, java.lang.String S_DATA) { this.S_W_ID = S_W_ID; this.S_I_ID = S_I_ID; this.S_QUANTITY = S_QUANTITY; this.S_YTD = S_YTD; this.S_ORDER_CNT = S_ORDER_CNT; this.S_REMOTE_CNT = S_REMOTE_CNT; this.S_DIST_01 = S_DIST_01; this.S_DIST_02 = S_DIST_02; this.S_DIST_03 = S_DIST_03; this.S_DIST_04 = S_DIST_04; this.S_DIST_05 = S_DIST_05; this.S_DIST_06 = S_DIST_06; this.S_DIST_07 = S_DIST_07; this.S_DIST_08 = S_DIST_08; this.S_DIST_09 = S_DIST_09; this.S_DIST_10 = S_DIST_10; this.S_DATA = S_DATA; }
        public stock(Row r) throws NullPointerException { this(r.isNull("S_W_ID") ? null : r.getInt("S_W_ID"), r.isNull("S_I_ID") ? null : r.getInt("S_I_ID"), r.isNull("S_QUANTITY") ? null : r.getDecimal("S_QUANTITY"), r.isNull("S_YTD") ? null : r.getDecimal("S_YTD"), r.isNull("S_ORDER_CNT") ? null : r.getInt("S_ORDER_CNT"), r.isNull("S_REMOTE_CNT") ? null : r.getInt("S_REMOTE_CNT"), r.isNull("S_DIST_01") ? null : r.getString("S_DIST_01"), r.isNull("S_DIST_02") ? null : r.getString("S_DIST_02"), r.isNull("S_DIST_03") ? null : r.getString("S_DIST_03"), r.isNull("S_DIST_04") ? null : r.getString("S_DIST_04"), r.isNull("S_DIST_05") ? null : r.getString("S_DIST_05"), r.isNull("S_DIST_06") ? null : r.getString("S_DIST_06"), r.isNull("S_DIST_07") ? null : r.getString("S_DIST_07"), r.isNull("S_DIST_08") ? null : r.getString("S_DIST_08"), r.isNull("S_DIST_09") ? null : r.getString("S_DIST_09"), r.isNull("S_DIST_10") ? null : r.getString("S_DIST_10"), r.isNull("S_DATA") ? null : r.getString("S_DATA")); }
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