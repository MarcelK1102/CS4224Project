package app.wrapper;
import java.util.HashMap;
import java.util.Map;
import app.Connector;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.QueryBuilder;
public class district extends tablebase{
	private static final String tablename = "district";
	private static final String names[] = new String[] {"d_w_id", "d_id", "d_name", "d_street_1", "d_street_2", "d_city", "d_state", "d_zip", "d_tax", "d_ytd", "d_next_o_id"};
	private static final int nkeys = 2;
	private static final Map<String,Integer> namesi;
	static {namesi = new HashMap<String,Integer>();namesi.put("d_w_id",0);namesi.put("d_id",1);namesi.put("d_name",2);namesi.put("d_street_1",3);namesi.put("d_street_2",4);namesi.put("d_city",5);namesi.put("d_state",6);namesi.put("d_zip",7);namesi.put("d_tax",8);namesi.put("d_ytd",9);namesi.put("d_next_o_id",10); }
	public Integer d_w_id(){return (Integer)values[0];};
	public void set_d_w_id(Integer value){values[0] = value;};
	public Integer d_id(){return (Integer)values[1];};
	public void set_d_id(Integer value){values[1] = value;};
	public java.lang.String d_name(){return (java.lang.String)values[2];};
	public void set_d_name(java.lang.String value){values[2] = value;};
	public java.lang.String d_street_1(){return (java.lang.String)values[3];};
	public void set_d_street_1(java.lang.String value){values[3] = value;};
	public java.lang.String d_street_2(){return (java.lang.String)values[4];};
	public void set_d_street_2(java.lang.String value){values[4] = value;};
	public java.lang.String d_city(){return (java.lang.String)values[5];};
	public void set_d_city(java.lang.String value){values[5] = value;};
	public java.lang.String d_state(){return (java.lang.String)values[6];};
	public void set_d_state(java.lang.String value){values[6] = value;};
	public java.lang.String d_zip(){return (java.lang.String)values[7];};
	public void set_d_zip(java.lang.String value){values[7] = value;};
	public java.math.BigDecimal d_tax(){return (java.math.BigDecimal)values[8];};
	public void set_d_tax(java.math.BigDecimal value){values[8] = value;};
	public java.math.BigDecimal d_ytd(){return (java.math.BigDecimal)values[9];};
	public void set_d_ytd(java.math.BigDecimal value){values[9] = value;};
	public Integer d_next_o_id(){return (Integer)values[10];};
	public void set_d_next_o_id(Integer value){values[10] = value;};
	public district (Row r) {super(tablename, names, namesi, nkeys, r);}
	public district (Integer d_w_id,Integer d_id, String ... attr) {this(Connector.s.execute(
		(attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select())
		.from("district")
		.where().and(QueryBuilder.eq("d_w_id", d_w_id)).and(QueryBuilder.eq("d_id", d_id)))
	.one());}
}

