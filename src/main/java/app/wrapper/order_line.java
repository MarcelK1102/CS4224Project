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
	public Integer wid(){return (Integer)values[0];};
	public Integer did(){return (Integer)values[1];};
	public Integer oid(){return (Integer)values[2];};
	public Integer number(){return (Integer)values[3];};
	public Integer iid(){return (Integer)values[4];};
	public java.util.Date deliveryd(){return (java.util.Date)values[5];};
	public java.math.BigDecimal amount(){return (java.math.BigDecimal)values[6];};
	public Integer supplywid(){return (Integer)values[7];};
	public java.math.BigDecimal quantity(){return (java.math.BigDecimal)values[8];};
	public java.lang.String distinfo(){return (java.lang.String)values[9];};
	public void set_wid(Integer value){values[0] = value;};
	public void set_did(Integer value){values[1] = value;};
	public void set_oid(Integer value){values[2] = value;};
	public void set_number(Integer value){values[3] = value;};
	public void set_iid(Integer value){values[4] = value;};
	public void set_deliveryd(java.util.Date value){values[5] = value;};
	public void set_amount(java.math.BigDecimal value){values[6] = value;};
	public void set_supplywid(Integer value){values[7] = value;};
	public void set_quantity(java.math.BigDecimal value){values[8] = value;};
	public void set_distinfo(java.lang.String value){values[9] = value;};
	public order_line () {super(tablename, names, namesi, nkeys);}
	public order_line (Row r) {super(tablename, names, namesi, nkeys, r);}
	public order_line (Integer wid,Integer did,Integer oid,Integer number, String ... attr) {this(Connector.s.execute(
		(attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select())
		.from("order_line")
		.where().and(QueryBuilder.eq("ol_w_id", wid)).and(QueryBuilder.eq("ol_d_id", did)).and(QueryBuilder.eq("ol_o_id", oid)).and(QueryBuilder.eq("ol_number", number)))
	.one());
		set_wid(wid);set_did(did);set_oid(oid);set_number(number);}
}

