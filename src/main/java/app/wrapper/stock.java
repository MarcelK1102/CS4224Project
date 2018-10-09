package app.wrapper;
import java.util.HashMap;
import java.util.Map;
import app.Connector;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.QueryBuilder;
public class stock extends tablebase{
	private static final String tablename = "stock";
	private static final String names[] = new String[] {"s_w_id", "s_i_id", "s_quantity", "s_ytd", "s_order_cnt", "s_remote_cnt", "s_dist_01", "s_dist_02", "s_dist_03", "s_dist_04", "s_dist_05", "s_dist_06", "s_dist_07", "s_dist_08", "s_dist_09", "s_dist_10", "s_data"};
	private static final int nkeys = 2;
	private static final Map<String,Integer> namesi;
	static {namesi = new HashMap<String,Integer>();namesi.put("s_w_id",0);namesi.put("s_i_id",1);namesi.put("s_quantity",2);namesi.put("s_ytd",3);namesi.put("s_order_cnt",4);namesi.put("s_remote_cnt",5);namesi.put("s_dist_01",6);namesi.put("s_dist_02",7);namesi.put("s_dist_03",8);namesi.put("s_dist_04",9);namesi.put("s_dist_05",10);namesi.put("s_dist_06",11);namesi.put("s_dist_07",12);namesi.put("s_dist_08",13);namesi.put("s_dist_09",14);namesi.put("s_dist_10",15);namesi.put("s_data",16); }
	public Integer s_w_id(){return (Integer)values[0];};
	public Integer s_i_id(){return (Integer)values[1];};
	public java.math.BigDecimal s_quantity(){return (java.math.BigDecimal)values[2];};
	public java.math.BigDecimal s_ytd(){return (java.math.BigDecimal)values[3];};
	public Integer s_order_cnt(){return (Integer)values[4];};
	public Integer s_remote_cnt(){return (Integer)values[5];};
	public java.lang.String s_dist_01(){return (java.lang.String)values[6];};
	public java.lang.String s_dist_02(){return (java.lang.String)values[7];};
	public java.lang.String s_dist_03(){return (java.lang.String)values[8];};
	public java.lang.String s_dist_04(){return (java.lang.String)values[9];};
	public java.lang.String s_dist_05(){return (java.lang.String)values[10];};
	public java.lang.String s_dist_06(){return (java.lang.String)values[11];};
	public java.lang.String s_dist_07(){return (java.lang.String)values[12];};
	public java.lang.String s_dist_08(){return (java.lang.String)values[13];};
	public java.lang.String s_dist_09(){return (java.lang.String)values[14];};
	public java.lang.String s_dist_10(){return (java.lang.String)values[15];};
	public java.lang.String s_data(){return (java.lang.String)values[16];};
	public void set_s_w_id(Integer value){values[0] = value;};
	public void set_s_i_id(Integer value){values[1] = value;};
	public void set_s_quantity(java.math.BigDecimal value){values[2] = value;};
	public void set_s_ytd(java.math.BigDecimal value){values[3] = value;};
	public void set_s_order_cnt(Integer value){values[4] = value;};
	public void set_s_remote_cnt(Integer value){values[5] = value;};
	public void set_s_dist_01(java.lang.String value){values[6] = value;};
	public void set_s_dist_02(java.lang.String value){values[7] = value;};
	public void set_s_dist_03(java.lang.String value){values[8] = value;};
	public void set_s_dist_04(java.lang.String value){values[9] = value;};
	public void set_s_dist_05(java.lang.String value){values[10] = value;};
	public void set_s_dist_06(java.lang.String value){values[11] = value;};
	public void set_s_dist_07(java.lang.String value){values[12] = value;};
	public void set_s_dist_08(java.lang.String value){values[13] = value;};
	public void set_s_dist_09(java.lang.String value){values[14] = value;};
	public void set_s_dist_10(java.lang.String value){values[15] = value;};
	public void set_s_data(java.lang.String value){values[16] = value;};
	public stock () {super(tablename, names, namesi, nkeys);}
	public stock (Row r) {super(tablename, names, namesi, nkeys, r);}
	public stock (Integer s_i_id,Integer s_w_id, String ... attr) {this(Connector.s.execute(
		(attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select())
		.from("stock")
		.where().and(QueryBuilder.eq("s_i_id", s_i_id)).and(QueryBuilder.eq("s_w_id", s_w_id)))
	.one());}
}

