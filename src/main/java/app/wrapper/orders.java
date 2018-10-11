package app.wrapper;
import com.datastax.driver.core.Row;
import java.util.Arrays;
import java.util.List;
import com.datastax.driver.core.querybuilder.QueryBuilder;
public class orders extends tablebase{
	private static final List<String> primarykeys = Arrays.asList("o_w_id","o_d_id","o_id");
	public int wid(){return keysvalue.get(0);}
	public int did(){return keysvalue.get(1);}
	public int id(){return keysvalue.get(2);}
	public Integer cid(){return r.getInt("o_c_id");};
	public Integer carrierid(){return r.getInt("o_carrier_id");};
	public java.math.BigDecimal olcnt(){return r.getDecimal("o_ol_cnt");};
	public java.math.BigDecimal alllocal(){return r.getDecimal("o_all_local");};
	public java.util.Date entryd(){return r.getTimestamp("o_entry_d");};
	public void set_wid(Integer value){assigns.and(QueryBuilder.set("o_w_id",value));};
	public void set_did(Integer value){assigns.and(QueryBuilder.set("o_d_id",value));};
	public void set_id(Integer value){assigns.and(QueryBuilder.set("o_id",value));};
	public void set_cid(Integer value){assigns.and(QueryBuilder.set("o_c_id",value));};
	public void set_carrierid(Integer value){assigns.and(QueryBuilder.set("o_carrier_id",value));};
	public void set_olcnt(java.math.BigDecimal value){assigns.and(QueryBuilder.set("o_ol_cnt",value));};
	public void set_alllocal(java.math.BigDecimal value){assigns.and(QueryBuilder.set("o_all_local",value));};
	public void set_entryd(java.util.Date value){assigns.and(QueryBuilder.set("o_entry_d",value));};
	public orders(){super("orders", primarykeys);}
	public orders (Integer wid,Integer did,Integer id, String ... attr) {this(); find(wid,did,id, attr);}
	public Row find(Integer wid,Integer did,Integer id, String ... attr){return super.find(Arrays.asList(wid,did,id), attr); }}

