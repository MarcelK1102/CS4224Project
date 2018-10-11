package app.wrapper;
import com.datastax.driver.core.Row;
import java.util.Arrays;
import java.util.List;
import com.datastax.driver.core.querybuilder.QueryBuilder;
public class warehouse extends tablebase{
	private static final List<String> primarykeys = Arrays.asList("w_id");
	public int id(){return keysvalue.get(0);}
	public java.lang.String name(){return r.getString("w_name");};
	public java.lang.String street1(){return r.getString("w_street_1");};
	public java.lang.String street2(){return r.getString("w_street_2");};
	public java.lang.String city(){return r.getString("w_city");};
	public java.lang.String state(){return r.getString("w_state");};
	public java.lang.String zip(){return r.getString("w_zip");};
	public java.math.BigDecimal tax(){return r.getDecimal("w_tax");};
	public void set_id(Integer value){assigns.and(QueryBuilder.set("w_id",value));};
	public void set_name(java.lang.String value){assigns.and(QueryBuilder.set("w_name",value));};
	public void set_street1(java.lang.String value){assigns.and(QueryBuilder.set("w_street_1",value));};
	public void set_street2(java.lang.String value){assigns.and(QueryBuilder.set("w_street_2",value));};
	public void set_city(java.lang.String value){assigns.and(QueryBuilder.set("w_city",value));};
	public void set_state(java.lang.String value){assigns.and(QueryBuilder.set("w_state",value));};
	public void set_zip(java.lang.String value){assigns.and(QueryBuilder.set("w_zip",value));};
	public void set_tax(java.math.BigDecimal value){assigns.and(QueryBuilder.set("w_tax",value));};
	public warehouse(){super("warehouse", primarykeys);}
	public warehouse (Integer id, String ... attr) {this(); find(id, attr);}
	public Row find(Integer id, String ... attr){return super.find(Arrays.asList(id), attr); }}

