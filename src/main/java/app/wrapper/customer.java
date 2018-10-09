package app.wrapper;
import java.util.HashMap;
import java.util.Map;
import app.Connector;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.QueryBuilder;
public class customer extends tablebase{
	private static final String tablename = "customer";
	private static final String names[] = new String[] {"c_w_id", "c_d_id", "c_id", "c_first", "c_middle", "c_last", "c_street_1", "c_street_2", "c_city", "c_state", "c_zip", "c_phone", "c_since", "c_credit", "c_credit_lim", "c_discount", "c_balance", "c_ytd_payment", "c_payment_cnt", "c_delivery_cnt", "c_data"};
	private static final int nkeys = 3;
	private static final Map<String,Integer> namesi;
	static {namesi = new HashMap<String,Integer>();namesi.put("c_w_id",0);namesi.put("c_d_id",1);namesi.put("c_id",2);namesi.put("c_first",3);namesi.put("c_middle",4);namesi.put("c_last",5);namesi.put("c_street_1",6);namesi.put("c_street_2",7);namesi.put("c_city",8);namesi.put("c_state",9);namesi.put("c_zip",10);namesi.put("c_phone",11);namesi.put("c_since",12);namesi.put("c_credit",13);namesi.put("c_credit_lim",14);namesi.put("c_discount",15);namesi.put("c_balance",16);namesi.put("c_ytd_payment",17);namesi.put("c_payment_cnt",18);namesi.put("c_delivery_cnt",19);namesi.put("c_data",20); }
	public Integer c_w_id(){return (Integer)values[0];};
	public Integer c_d_id(){return (Integer)values[1];};
	public Integer c_id(){return (Integer)values[2];};
	public java.lang.String c_first(){return (java.lang.String)values[3];};
	public java.lang.String c_middle(){return (java.lang.String)values[4];};
	public java.lang.String c_last(){return (java.lang.String)values[5];};
	public java.lang.String c_street_1(){return (java.lang.String)values[6];};
	public java.lang.String c_street_2(){return (java.lang.String)values[7];};
	public java.lang.String c_city(){return (java.lang.String)values[8];};
	public java.lang.String c_state(){return (java.lang.String)values[9];};
	public java.lang.String c_zip(){return (java.lang.String)values[10];};
	public java.lang.String c_phone(){return (java.lang.String)values[11];};
	public java.util.Date c_since(){return (java.util.Date)values[12];};
	public java.lang.String c_credit(){return (java.lang.String)values[13];};
	public java.math.BigDecimal c_credit_lim(){return (java.math.BigDecimal)values[14];};
	public java.math.BigDecimal c_discount(){return (java.math.BigDecimal)values[15];};
	public java.math.BigDecimal c_balance(){return (java.math.BigDecimal)values[16];};
	public Float c_ytd_payment(){return (Float)values[17];};
	public Integer c_payment_cnt(){return (Integer)values[18];};
	public Integer c_delivery_cnt(){return (Integer)values[19];};
	public java.lang.String c_data(){return (java.lang.String)values[20];};
	public void set_c_w_id(Integer value){values[0] = value;};
	public void set_c_d_id(Integer value){values[1] = value;};
	public void set_c_id(Integer value){values[2] = value;};
	public void set_c_first(java.lang.String value){values[3] = value;};
	public void set_c_middle(java.lang.String value){values[4] = value;};
	public void set_c_last(java.lang.String value){values[5] = value;};
	public void set_c_street_1(java.lang.String value){values[6] = value;};
	public void set_c_street_2(java.lang.String value){values[7] = value;};
	public void set_c_city(java.lang.String value){values[8] = value;};
	public void set_c_state(java.lang.String value){values[9] = value;};
	public void set_c_zip(java.lang.String value){values[10] = value;};
	public void set_c_phone(java.lang.String value){values[11] = value;};
	public void set_c_since(java.util.Date value){values[12] = value;};
	public void set_c_credit(java.lang.String value){values[13] = value;};
	public void set_c_credit_lim(java.math.BigDecimal value){values[14] = value;};
	public void set_c_discount(java.math.BigDecimal value){values[15] = value;};
	public void set_c_balance(java.math.BigDecimal value){values[16] = value;};
	public void set_c_ytd_payment(Float value){values[17] = value;};
	public void set_c_payment_cnt(Integer value){values[18] = value;};
	public void set_c_delivery_cnt(Integer value){values[19] = value;};
	public void set_c_data(java.lang.String value){values[20] = value;};
	public customer () {super(tablename, names, namesi, nkeys);}
	public customer (Row r) {super(tablename, names, namesi, nkeys, r);}
	public customer (Integer c_d_id,Integer c_w_id,Integer c_id, String ... attr) {this(Connector.s.execute(
		(attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select())
		.from("customer")
		.where().and(QueryBuilder.eq("c_d_id", c_d_id)).and(QueryBuilder.eq("c_w_id", c_w_id)).and(QueryBuilder.eq("c_id", c_id)))
	.one());}
}

