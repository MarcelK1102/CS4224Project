package app.wrapper;
import com.datastax.driver.core.Row;
import java.util.Arrays;
import java.util.List;
import com.datastax.driver.core.querybuilder.QueryBuilder;
public class customer_top_ten extends tablebase{
	private static final List<String> primarykeys = Arrays.asList("c_w_id","c_d_id","c_id");
	public int wid(){return keysvalue.get(0);}
	public int did(){return keysvalue.get(1);}
	public int id(){return keysvalue.get(2);}
	public Long balance(){return r.getTime("c_balance");};
	public void set_wid(Integer value){assigns.and(QueryBuilder.set("c_w_id",value));};
	public void set_did(Integer value){assigns.and(QueryBuilder.set("c_d_id",value));};
	public void set_id(Integer value){assigns.and(QueryBuilder.set("c_id",value));};
	public void set_balance(Long value){assigns.and(QueryBuilder.set("c_balance",value));};
	public customer_top_ten(){super("customer_top_ten", primarykeys);}
	public customer_top_ten (Integer wid,Integer did,Integer id, String ... attr) {this(); find(wid,did,id, attr);}
	public Row find(Integer wid,Integer did,Integer id, String ... attr){return super.find(Arrays.asList(wid,did,id), attr); }}

