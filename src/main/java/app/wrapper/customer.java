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
	public Integer wid(){return (Integer)values[0];};
	public Integer did(){return (Integer)values[1];};
	public Integer id(){return (Integer)values[2];};
	public java.lang.String first(){return (java.lang.String)values[3];};
	public java.lang.String middle(){return (java.lang.String)values[4];};
	public java.lang.String last(){return (java.lang.String)values[5];};
	public java.lang.String street1(){return (java.lang.String)values[6];};
	public java.lang.String street2(){return (java.lang.String)values[7];};
	public java.lang.String city(){return (java.lang.String)values[8];};
	public java.lang.String state(){return (java.lang.String)values[9];};
	public java.lang.String zip(){return (java.lang.String)values[10];};
	public java.lang.String phone(){return (java.lang.String)values[11];};
	public java.util.Date since(){return (java.util.Date)values[12];};
	public java.lang.String credit(){return (java.lang.String)values[13];};
	public java.math.BigDecimal creditlim(){return (java.math.BigDecimal)values[14];};
	public java.math.BigDecimal discount(){return (java.math.BigDecimal)values[15];};
	public java.math.BigDecimal balance(){return (java.math.BigDecimal)values[16];};
	public Float ytdpayment(){return (Float)values[17];};
	public Integer paymentcnt(){return (Integer)values[18];};
	public Integer deliverycnt(){return (Integer)values[19];};
	public java.lang.String data(){return (java.lang.String)values[20];};
	public void set_wid(Integer value){values[0] = value;};
	public void set_did(Integer value){values[1] = value;};
	public void set_id(Integer value){values[2] = value;};
	public void set_first(java.lang.String value){values[3] = value;};
	public void set_middle(java.lang.String value){values[4] = value;};
	public void set_last(java.lang.String value){values[5] = value;};
	public void set_street1(java.lang.String value){values[6] = value;};
	public void set_street2(java.lang.String value){values[7] = value;};
	public void set_city(java.lang.String value){values[8] = value;};
	public void set_state(java.lang.String value){values[9] = value;};
	public void set_zip(java.lang.String value){values[10] = value;};
	public void set_phone(java.lang.String value){values[11] = value;};
	public void set_since(java.util.Date value){values[12] = value;};
	public void set_credit(java.lang.String value){values[13] = value;};
	public void set_creditlim(java.math.BigDecimal value){values[14] = value;};
	public void set_discount(java.math.BigDecimal value){values[15] = value;};
	public void set_balance(java.math.BigDecimal value){values[16] = value;};
	public void set_ytdpayment(Float value){values[17] = value;};
	public void set_paymentcnt(Integer value){values[18] = value;};
	public void set_deliverycnt(Integer value){values[19] = value;};
	public void set_data(java.lang.String value){values[20] = value;};
	public customer () {super(tablename, names, namesi, nkeys);}
	public customer (Row r) {super(tablename, names, namesi, nkeys, r);}
	public customer (Integer wid,Integer did,Integer id, String ... attr) {this(Connector.s.execute(
		(attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select())
		.from("customer")
		.where().and(QueryBuilder.eq("c_w_id", wid)).and(QueryBuilder.eq("c_d_id", did)).and(QueryBuilder.eq("c_id", id)))
	.one());
		set_wid(wid);set_did(did);set_id(id);}
}

