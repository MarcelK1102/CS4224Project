package app.wrapper;
import com.datastax.driver.core.Row;
import java.util.Arrays;
import java.util.List;
import com.datastax.driver.core.querybuilder.QueryBuilder;
public class warehouse_cnts extends tablebase{
	private static final List<String> primarykeys = Arrays.asList("w_id");
	public int id(){return keysvalue.get(0);}
	public Long ytd(){return r.getTime("w_ytd");};
	public void set_id(Integer value){assigns.and(QueryBuilder.set("w_id",value));};
	public void set_ytd(Long value){assigns.and(QueryBuilder.set("w_ytd",value));};
	public warehouse_cnts(){super("warehouse_cnts", primarykeys);}
	public warehouse_cnts (Integer id, String ... attr) {this(); find(id, attr);}
	public Row find(Integer id, String ... attr){return super.find(Arrays.asList(id), attr); }}

