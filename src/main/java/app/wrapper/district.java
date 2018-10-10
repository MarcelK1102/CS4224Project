package app.wrapper;
import com.datastax.driver.core.Row;
import java.util.Arrays;
import java.util.List;
import com.datastax.driver.core.querybuilder.QueryBuilder;
public class district extends tablebase{
	private static final List<String> primarykeys = Arrays.asList("d_w_id","d_id");
	public Integer wid(){return r.getInt("d_w_id");};
	public Integer id(){return r.getInt("d_id");};
	public java.lang.String name(){return r.getString("d_name");};
	public java.lang.String street1(){return r.getString("d_street_1");};
	public java.lang.String street2(){return r.getString("d_street_2");};
	public java.lang.String city(){return r.getString("d_city");};
	public java.lang.String state(){return r.getString("d_state");};
	public java.lang.String zip(){return r.getString("d_zip");};
	public java.math.BigDecimal tax(){return r.getDecimal("d_tax");};
	public java.math.BigDecimal ytd(){return r.getDecimal("d_ytd");};
	public Integer nextoid(){return r.getInt("d_next_o_id");};
	public void set_wid(Integer value){assigns.and(QueryBuilder.set("d_w_id",value));};
	public void set_id(Integer value){assigns.and(QueryBuilder.set("d_id",value));};
	public void set_name(java.lang.String value){assigns.and(QueryBuilder.set("d_name",value));};
	public void set_street1(java.lang.String value){assigns.and(QueryBuilder.set("d_street_1",value));};
	public void set_street2(java.lang.String value){assigns.and(QueryBuilder.set("d_street_2",value));};
	public void set_city(java.lang.String value){assigns.and(QueryBuilder.set("d_city",value));};
	public void set_state(java.lang.String value){assigns.and(QueryBuilder.set("d_state",value));};
	public void set_zip(java.lang.String value){assigns.and(QueryBuilder.set("d_zip",value));};
	public void set_tax(java.math.BigDecimal value){assigns.and(QueryBuilder.set("d_tax",value));};
	public void set_ytd(java.math.BigDecimal value){assigns.and(QueryBuilder.set("d_ytd",value));};
	public void set_nextoid(Integer value){assigns.and(QueryBuilder.set("d_next_o_id",value));};
	public district(){super("district", primarykeys);}
	public district (Integer wid,Integer id, String ... attr) {this(); find(wid,id, attr);}
	public Row find(Integer wid,Integer id, String ... attr){return super.find(Arrays.asList(wid,id), attr); }}

