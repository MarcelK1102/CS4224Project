package app.wrapper;
import com.datastax.driver.core.Row;
import java.util.Arrays;
import java.util.List;
import com.datastax.driver.core.querybuilder.QueryBuilder;
public class order_line extends tablebase{
	private static final List<String> primarykeys = Arrays.asList("ol_w_id","ol_d_id","ol_o_id","ol_number");
	public Integer wid(){return r.getInt("ol_w_id");};
	public Integer did(){return r.getInt("ol_d_id");};
	public Integer oid(){return r.getInt("ol_o_id");};
	public Integer number(){return r.getInt("ol_number");};
	public Integer iid(){return r.getInt("ol_i_id");};
	public java.util.Date deliveryd(){return r.getTimestamp("ol_delivery_d");};
	public java.math.BigDecimal amount(){return r.getDecimal("ol_amount");};
	public Integer supplywid(){return r.getInt("ol_supply_w_id");};
	public java.math.BigDecimal quantity(){return r.getDecimal("ol_quantity");};
	public java.lang.String distinfo(){return r.getString("ol_dist_info");};
	public void set_wid(Integer value){assigns.and(QueryBuilder.set("ol_w_id",value));};
	public void set_did(Integer value){assigns.and(QueryBuilder.set("ol_d_id",value));};
	public void set_oid(Integer value){assigns.and(QueryBuilder.set("ol_o_id",value));};
	public void set_number(Integer value){assigns.and(QueryBuilder.set("ol_number",value));};
	public void set_iid(Integer value){assigns.and(QueryBuilder.set("ol_i_id",value));};
	public void set_deliveryd(java.util.Date value){assigns.and(QueryBuilder.set("ol_delivery_d",value));};
	public void set_amount(java.math.BigDecimal value){assigns.and(QueryBuilder.set("ol_amount",value));};
	public void set_supplywid(Integer value){assigns.and(QueryBuilder.set("ol_supply_w_id",value));};
	public void set_quantity(java.math.BigDecimal value){assigns.and(QueryBuilder.set("ol_quantity",value));};
	public void set_distinfo(java.lang.String value){assigns.and(QueryBuilder.set("ol_dist_info",value));};
	public order_line(){super("order_line", primarykeys);}
	public order_line (Integer wid,Integer did,Integer oid,Integer number, String ... attr) {this(); find(wid,did,oid,number, attr);}
	public Row find(Integer wid,Integer did,Integer oid,Integer number, String ... attr){return super.find(Arrays.asList(wid,did,oid,number), attr); }}

