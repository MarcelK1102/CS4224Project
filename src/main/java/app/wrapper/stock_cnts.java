package app.wrapper;
import com.datastax.driver.core.Row;
import java.util.Arrays;
import java.util.List;
import com.datastax.driver.core.querybuilder.QueryBuilder;
public class stock_cnts extends tablebase{
	private static final List<String> primarykeys = Arrays.asList("s_w_id","s_i_id");
	public int wid(){return keysvalue.get(0);}
	public int iid(){return keysvalue.get(1);}
	public Long quantity(){return r.getTime("s_quantity");};
	public Long ytd(){return r.getTime("s_ytd");};
	public Long ordercnt(){return r.getTime("s_order_cnt");};
	public Long remotecnt(){return r.getTime("s_remote_cnt");};
	public void set_wid(Integer value){assigns.and(QueryBuilder.set("s_w_id",value));};
	public void set_iid(Integer value){assigns.and(QueryBuilder.set("s_i_id",value));};
	public void set_quantity(Long value){assigns.and(QueryBuilder.set("s_quantity",value));};
	public void set_ytd(Long value){assigns.and(QueryBuilder.set("s_ytd",value));};
	public void set_ordercnt(Long value){assigns.and(QueryBuilder.set("s_order_cnt",value));};
	public void set_remotecnt(Long value){assigns.and(QueryBuilder.set("s_remote_cnt",value));};
	public stock_cnts(){super("stock_cnts", primarykeys);}
	public stock_cnts (Integer wid,Integer iid, String ... attr) {this(); find(wid,iid, attr);}
	public Row find(Integer wid,Integer iid, String ... attr){return super.find(Arrays.asList(wid,iid), attr); }}

