package app.wrapper;
import java.util.HashMap;
import java.util.Map;
import app.Connector;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.QueryBuilder;
public class orders extends tablebase{
	private static final String tablename = "orders";
	private static final String names[] = new String[] {"o_w_id", "o_d_id", "o_id", "o_c_id", "o_carrier_id", "o_ol_cnt", "o_all_local", "o_entry_d"};
	private static final int nkeys = 3;
	private static final Map<String,Integer> namesi;
	static {namesi = new HashMap<String,Integer>();namesi.put("o_w_id",0);namesi.put("o_d_id",1);namesi.put("o_id",2);namesi.put("o_c_id",3);namesi.put("o_carrier_id",4);namesi.put("o_ol_cnt",5);namesi.put("o_all_local",6);namesi.put("o_entry_d",7); }
	public Integer o_w_id(){return (Integer)values[0];};
	public void set_o_w_id(Integer value){values[0] = value;};
	public Integer o_d_id(){return (Integer)values[1];};
	public void set_o_d_id(Integer value){values[1] = value;};
	public Integer o_id(){return (Integer)values[2];};
	public void set_o_id(Integer value){values[2] = value;};
	public Integer o_c_id(){return (Integer)values[3];};
	public void set_o_c_id(Integer value){values[3] = value;};
	public Integer o_carrier_id(){return (Integer)values[4];};
	public void set_o_carrier_id(Integer value){values[4] = value;};
	public java.math.BigDecimal o_ol_cnt(){return (java.math.BigDecimal)values[5];};
	public void set_o_ol_cnt(java.math.BigDecimal value){values[5] = value;};
	public java.math.BigDecimal o_all_local(){return (java.math.BigDecimal)values[6];};
	public void set_o_all_local(java.math.BigDecimal value){values[6] = value;};
	public java.util.Date o_entry_d(){return (java.util.Date)values[7];};
	public void set_o_entry_d(java.util.Date value){values[7] = value;};
	public orders (Row r) {super(tablename, names, namesi, nkeys, r);}
	public orders (Integer o_d_id,Integer o_w_id,Integer o_id, String ... attr) {this(Connector.s.execute(
		(attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select())
		.from("orders")
		.where().and(QueryBuilder.eq("o_d_id", o_d_id)).and(QueryBuilder.eq("o_w_id", o_w_id)).and(QueryBuilder.eq("o_id", o_id)))
	.one());}
}

