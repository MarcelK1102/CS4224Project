package app.wrapper;
import com.datastax.driver.core.Row;
import java.util.Arrays;
import java.util.List;
import com.datastax.driver.core.querybuilder.QueryBuilder;
public class district_cnts extends tablebase{
	private static final List<String> primarykeys = Arrays.asList("d_w_id","d_id");
	public int wid(){return keysvalue.get(0);}
	public int id(){return keysvalue.get(1);}
	public Long ytd(){return r.getTime("d_ytd");};
	public Long nextoid(){return r.getTime("d_next_o_id");};
	public void set_wid(Integer value){assigns.and(QueryBuilder.set("d_w_id",value));};
	public void set_id(Integer value){assigns.and(QueryBuilder.set("d_id",value));};
	public void set_ytd(Long value){assigns.and(QueryBuilder.set("d_ytd",value));};
	public void set_nextoid(Long value){assigns.and(QueryBuilder.set("d_next_o_id",value));};
	public district_cnts(){super("district_cnts", primarykeys);}
	public district_cnts (Integer wid,Integer id, String ... attr) {this(); find(wid,id, attr);}
	public Row find(Integer wid,Integer id, String ... attr){return super.find(Arrays.asList(wid,id), attr); }}

