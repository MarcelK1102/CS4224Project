package app.wrapper;
import java.util.HashMap;
import java.util.Map;
import app.Connector;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.QueryBuilder;
public class item extends tablebase{
	private static final String tablename = "item";
	private static final String names[] = new String[] {"i_id", "i_name", "i_price", "i_im_id", "i_data"};
	private static final int nkeys = 1;
	private static final Map<String,Integer> namesi;
	static {namesi = new HashMap<String,Integer>();namesi.put("i_id",0);namesi.put("i_name",1);namesi.put("i_price",2);namesi.put("i_im_id",3);namesi.put("i_data",4); }
	public Integer i_id(){return (Integer)values[0];};
	public void set_i_id(Integer value){values[0] = value;};
	public java.lang.String i_name(){return (java.lang.String)values[1];};
	public void set_i_name(java.lang.String value){values[1] = value;};
	public java.math.BigDecimal i_price(){return (java.math.BigDecimal)values[2];};
	public void set_i_price(java.math.BigDecimal value){values[2] = value;};
	public Integer i_im_id(){return (Integer)values[3];};
	public void set_i_im_id(Integer value){values[3] = value;};
	public java.lang.String i_data(){return (java.lang.String)values[4];};
	public void set_i_data(java.lang.String value){values[4] = value;};
	public item (Row r) {super(tablename, names, namesi, nkeys, r);}
	public item (Integer i_id, String ... attr) {this(Connector.s.execute(
		(attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select())
		.from("item")
		.where().and(QueryBuilder.eq("i_id", i_id)))
	.one());}
}

