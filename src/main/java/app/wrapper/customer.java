package app.wrapper;
import com.datastax.driver.core.Row;
import java.util.Arrays;
import java.util.List;
import com.datastax.driver.core.querybuilder.QueryBuilder;
public class customer extends tablebase{
	private static final List<String> primarykeys = Arrays.asList("c_w_id","c_d_id","c_id");
	public Integer wid(){return r.getInt("c_w_id");};
	public Integer did(){return r.getInt("c_d_id");};
	public Integer id(){return r.getInt("c_id");};
	public java.lang.String first(){return r.getString("c_first");};
	public java.lang.String middle(){return r.getString("c_middle");};
	public java.lang.String last(){return r.getString("c_last");};
	public java.lang.String street1(){return r.getString("c_street_1");};
	public java.lang.String street2(){return r.getString("c_street_2");};
	public java.lang.String city(){return r.getString("c_city");};
	public java.lang.String state(){return r.getString("c_state");};
	public java.lang.String zip(){return r.getString("c_zip");};
	public java.lang.String phone(){return r.getString("c_phone");};
	public java.util.Date since(){return r.getTimestamp("c_since");};
	public java.lang.String credit(){return r.getString("c_credit");};
	public java.math.BigDecimal creditlim(){return r.getDecimal("c_credit_lim");};
	public java.math.BigDecimal discount(){return r.getDecimal("c_discount");};
	public java.math.BigDecimal balance(){return r.getDecimal("c_balance");};
	public Float ytdpayment(){return r.getFloat("c_ytd_payment");};
	public Integer paymentcnt(){return r.getInt("c_payment_cnt");};
	public Integer deliverycnt(){return r.getInt("c_delivery_cnt");};
	public java.lang.String data(){return r.getString("c_data");};
	public void set_wid(Integer value){assigns.and(QueryBuilder.set("c_w_id",value));};
	public void set_did(Integer value){assigns.and(QueryBuilder.set("c_d_id",value));};
	public void set_id(Integer value){assigns.and(QueryBuilder.set("c_id",value));};
	public void set_first(java.lang.String value){assigns.and(QueryBuilder.set("c_first",value));};
	public void set_middle(java.lang.String value){assigns.and(QueryBuilder.set("c_middle",value));};
	public void set_last(java.lang.String value){assigns.and(QueryBuilder.set("c_last",value));};
	public void set_street1(java.lang.String value){assigns.and(QueryBuilder.set("c_street_1",value));};
	public void set_street2(java.lang.String value){assigns.and(QueryBuilder.set("c_street_2",value));};
	public void set_city(java.lang.String value){assigns.and(QueryBuilder.set("c_city",value));};
	public void set_state(java.lang.String value){assigns.and(QueryBuilder.set("c_state",value));};
	public void set_zip(java.lang.String value){assigns.and(QueryBuilder.set("c_zip",value));};
	public void set_phone(java.lang.String value){assigns.and(QueryBuilder.set("c_phone",value));};
	public void set_since(java.util.Date value){assigns.and(QueryBuilder.set("c_since",value));};
	public void set_credit(java.lang.String value){assigns.and(QueryBuilder.set("c_credit",value));};
	public void set_creditlim(java.math.BigDecimal value){assigns.and(QueryBuilder.set("c_credit_lim",value));};
	public void set_discount(java.math.BigDecimal value){assigns.and(QueryBuilder.set("c_discount",value));};
	public void set_balance(java.math.BigDecimal value){assigns.and(QueryBuilder.set("c_balance",value));};
	public void set_ytdpayment(Float value){assigns.and(QueryBuilder.set("c_ytd_payment",value));};
	public void set_paymentcnt(Integer value){assigns.and(QueryBuilder.set("c_payment_cnt",value));};
	public void set_deliverycnt(Integer value){assigns.and(QueryBuilder.set("c_delivery_cnt",value));};
	public void set_data(java.lang.String value){assigns.and(QueryBuilder.set("c_data",value));};
	public customer(){super("customer", primarykeys);}
	public customer (Integer wid,Integer did,Integer id, String ... attr) {this(); find(wid,did,id, attr);}
	public Row find(Integer wid,Integer did,Integer id, String ... attr){return super.find(Arrays.asList(wid,did,id), attr); }}

