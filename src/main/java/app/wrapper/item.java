package app.wrapper;
import com.datastax.driver.core.Row;
import java.util.Arrays;
import java.util.List;
import com.datastax.driver.core.querybuilder.QueryBuilder;
public class item extends tablebase{
	private static final List<String> primarykeys = Arrays.asList("i_id");
	public int id(){return keysvalue.get(0);}
	public java.lang.String name(){return r.getString("i_name");};
	public java.math.BigDecimal price(){return r.getDecimal("i_price");};
	public Integer imid(){return r.getInt("i_im_id");};
	public java.lang.String data(){return r.getString("i_data");};
	public void set_id(Integer value){assigns.and(QueryBuilder.set("i_id",value));};
	public void set_name(java.lang.String value){assigns.and(QueryBuilder.set("i_name",value));};
	public void set_price(java.math.BigDecimal value){assigns.and(QueryBuilder.set("i_price",value));};
	public void set_imid(Integer value){assigns.and(QueryBuilder.set("i_im_id",value));};
	public void set_data(java.lang.String value){assigns.and(QueryBuilder.set("i_data",value));};
	public item(){super("item", primarykeys);}
	public item (Integer id, String ... attr) {this(); find(id, attr);}
	public Row find(Integer id, String ... attr){return super.find(Arrays.asList(id), attr); }}

