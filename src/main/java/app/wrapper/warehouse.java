package app.wrapper;
import java.util.HashMap;
import java.util.Map;
import app.Connector;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.QueryBuilder;
public class warehouse extends tablebase{
	private static final String tablename = "warehouse";
	private static final String names[] = new String[] {"w_id", "w_name", "w_street_1", "w_street_2", "w_city", "w_state", "w_zip", "w_tax", "w_ytd"};
	private static final int nkeys = 1;
	private static final Map<String,Integer> namesi;
	static {namesi = new HashMap<String,Integer>();namesi.put("w_id",0);namesi.put("w_name",1);namesi.put("w_street_1",2);namesi.put("w_street_2",3);namesi.put("w_city",4);namesi.put("w_state",5);namesi.put("w_zip",6);namesi.put("w_tax",7);namesi.put("w_ytd",8); }
	public Integer w_id(){return (Integer)values[0];};
	public void set_w_id(Integer value){values[0] = value;};
	public java.lang.String w_name(){return (java.lang.String)values[1];};
	public void set_w_name(java.lang.String value){values[1] = value;};
	public java.lang.String w_street_1(){return (java.lang.String)values[2];};
	public void set_w_street_1(java.lang.String value){values[2] = value;};
	public java.lang.String w_street_2(){return (java.lang.String)values[3];};
	public void set_w_street_2(java.lang.String value){values[3] = value;};
	public java.lang.String w_city(){return (java.lang.String)values[4];};
	public void set_w_city(java.lang.String value){values[4] = value;};
	public java.lang.String w_state(){return (java.lang.String)values[5];};
	public void set_w_state(java.lang.String value){values[5] = value;};
	public java.lang.String w_zip(){return (java.lang.String)values[6];};
	public void set_w_zip(java.lang.String value){values[6] = value;};
	public java.math.BigDecimal w_tax(){return (java.math.BigDecimal)values[7];};
	public void set_w_tax(java.math.BigDecimal value){values[7] = value;};
	public java.math.BigDecimal w_ytd(){return (java.math.BigDecimal)values[8];};
	public void set_w_ytd(java.math.BigDecimal value){values[8] = value;};
	public warehouse (Row r) {super(tablename, names, namesi, nkeys, r);}
	public warehouse (Integer w_id, String ... attr) {this(Connector.s.execute(
		(attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select())
		.from("warehouse")
		.where().and(QueryBuilder.eq("w_id", w_id)))
	.one());}
}

