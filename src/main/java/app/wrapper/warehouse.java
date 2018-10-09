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
	public Integer id(){return (Integer)values[0];};
	public java.lang.String name(){return (java.lang.String)values[1];};
	public java.lang.String street1(){return (java.lang.String)values[2];};
	public java.lang.String street2(){return (java.lang.String)values[3];};
	public java.lang.String city(){return (java.lang.String)values[4];};
	public java.lang.String state(){return (java.lang.String)values[5];};
	public java.lang.String zip(){return (java.lang.String)values[6];};
	public java.math.BigDecimal tax(){return (java.math.BigDecimal)values[7];};
	public java.math.BigDecimal ytd(){return (java.math.BigDecimal)values[8];};
	public void set_id(Integer value){values[0] = value;};
	public void set_name(java.lang.String value){values[1] = value;};
	public void set_street1(java.lang.String value){values[2] = value;};
	public void set_street2(java.lang.String value){values[3] = value;};
	public void set_city(java.lang.String value){values[4] = value;};
	public void set_state(java.lang.String value){values[5] = value;};
	public void set_zip(java.lang.String value){values[6] = value;};
	public void set_tax(java.math.BigDecimal value){values[7] = value;};
	public void set_ytd(java.math.BigDecimal value){values[8] = value;};
	public warehouse () {super(tablename, names, namesi, nkeys);}
	public warehouse (Row r) {super(tablename, names, namesi, nkeys, r);}
	public warehouse (Integer id, String ... attr) {this(Connector.s.execute(
		(attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select())
		.from("warehouse")
		.where().and(QueryBuilder.eq("w_id", id)))
	.one());
		set_id(id);}
}

