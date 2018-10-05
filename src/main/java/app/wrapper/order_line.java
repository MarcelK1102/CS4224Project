package app.wrapper;
import java.util.HashMap;
import java.util.Map;
import app.Connector;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.QueryBuilder;
public class order_line extends tablebase{
	private static final String tablename = "order_line";
	private static final String names[] = new String[] {"ol_w_id", "ol_d_id", "ol_o_id", "ol_number", "ol_i_id", "ol_delivery_d", "ol_amount", "ol_supply_w_id", "ol_quantity", "ol_dist_info"};
	private static final int nkeys = 4;
	private static final Map<String,Integer> namesi;
	static {namesi = new HashMap<String,Integer>();namesi.put("ol_w_id",0);namesi.put("ol_d_id",1);namesi.put("ol_o_id",2);namesi.put("ol_number",3);namesi.put("ol_i_id",4);namesi.put("ol_delivery_d",5);namesi.put("ol_amount",6);namesi.put("ol_supply_w_id",7);namesi.put("ol_quantity",8);namesi.put("ol_dist_info",9); }
	public Integer ol_w_id(){return (Integer)values[0];};
	public void set_ol_w_id(Integer value){values[0] = value;};
	public Integer ol_d_id(){return (Integer)values[1];};
	public void set_ol_d_id(Integer value){values[1] = value;};
	public Integer ol_o_id(){return (Integer)values[2];};
	public void set_ol_o_id(Integer value){values[2] = value;};
	public Integer ol_number(){return (Integer)values[3];};
	public void set_ol_number(Integer value){values[3] = value;};
	public Integer ol_i_id(){return (Integer)values[4];};
	public void set_ol_i_id(Integer value){values[4] = value;};
	public java.util.Date ol_delivery_d(){return (java.util.Date)values[5];};
	public void set_ol_delivery_d(java.util.Date value){values[5] = value;};
	public java.math.BigDecimal ol_amount(){return (java.math.BigDecimal)values[6];};
	public void set_ol_amount(java.math.BigDecimal value){values[6] = value;};
	public Integer ol_supply_w_id(){return (Integer)values[7];};
	public void set_ol_supply_w_id(Integer value){values[7] = value;};
	public java.math.BigDecimal ol_quantity(){return (java.math.BigDecimal)values[8];};
	public void set_ol_quantity(java.math.BigDecimal value){values[8] = value;};
	public java.lang.String ol_dist_info(){return (java.lang.String)values[9];};
	public void set_ol_dist_info(java.lang.String value){values[9] = value;};
	public order_line (Row r) {super(tablename, names, namesi, nkeys, r);}
	public order_line (Integer ol_d_id,Integer ol_w_id,Integer ol_o_id,Integer ol_number, String ... attr) {this(Connector.s.execute(
		(attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select())
		.from("order_line")
		.where().and(QueryBuilder.eq("ol_d_id", ol_d_id)).and(QueryBuilder.eq("ol_w_id", ol_w_id)).and(QueryBuilder.eq("ol_o_id", ol_o_id)).and(QueryBuilder.eq("ol_number", ol_number)))
	.one());}
}

