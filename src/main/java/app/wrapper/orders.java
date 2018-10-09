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
	public Integer wid(){return (Integer)values[0];};
	public Integer did(){return (Integer)values[1];};
	public Integer id(){return (Integer)values[2];};
	public Integer cid(){return (Integer)values[3];};
	public Integer carrierid(){return (Integer)values[4];};
	public java.math.BigDecimal olcnt(){return (java.math.BigDecimal)values[5];};
	public java.math.BigDecimal alllocal(){return (java.math.BigDecimal)values[6];};
	public java.util.Date entryd(){return (java.util.Date)values[7];};
	public void set_wid(Integer value){values[0] = value;};
	public void set_did(Integer value){values[1] = value;};
	public void set_id(Integer value){values[2] = value;};
	public void set_cid(Integer value){values[3] = value;};
	public void set_carrierid(Integer value){values[4] = value;};
	public void set_olcnt(java.math.BigDecimal value){values[5] = value;};
	public void set_alllocal(java.math.BigDecimal value){values[6] = value;};
	public void set_entryd(java.util.Date value){values[7] = value;};
	public orders () {super(tablename, names, namesi, nkeys);}
	public orders (Row r) {super(tablename, names, namesi, nkeys, r);}
	public orders (Integer wid,Integer did,Integer id, String ... attr) {this(Connector.s.execute(
		(attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select())
		.from("orders")
		.where().and(QueryBuilder.eq("o_w_id", wid)).and(QueryBuilder.eq("o_d_id", did)).and(QueryBuilder.eq("o_id", id)))
	.one());
		set_wid(wid);set_did(did);set_id(id);}
}

