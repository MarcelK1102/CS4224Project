package app.wrapper;
import com.datastax.driver.core.Row;
import java.util.Arrays;
import java.util.List;
import com.datastax.driver.core.querybuilder.QueryBuilder;
public class stock extends tablebase{
	private static final List<String> primarykeys = Arrays.asList("s_w_id","s_i_id");
	public int wid(){return keysvalue.get(0);}
	public int iid(){return keysvalue.get(1);}
	public java.lang.String dist01(){return r.getString("s_dist_01");};
	public java.lang.String dist02(){return r.getString("s_dist_02");};
	public java.lang.String dist03(){return r.getString("s_dist_03");};
	public java.lang.String dist04(){return r.getString("s_dist_04");};
	public java.lang.String dist05(){return r.getString("s_dist_05");};
	public java.lang.String dist06(){return r.getString("s_dist_06");};
	public java.lang.String dist07(){return r.getString("s_dist_07");};
	public java.lang.String dist08(){return r.getString("s_dist_08");};
	public java.lang.String dist09(){return r.getString("s_dist_09");};
	public java.lang.String dist10(){return r.getString("s_dist_10");};
	public java.lang.String data(){return r.getString("s_data");};
	public void set_wid(Integer value){assigns.and(QueryBuilder.set("s_w_id",value));};
	public void set_iid(Integer value){assigns.and(QueryBuilder.set("s_i_id",value));};
	public void set_dist01(java.lang.String value){assigns.and(QueryBuilder.set("s_dist_01",value));};
	public void set_dist02(java.lang.String value){assigns.and(QueryBuilder.set("s_dist_02",value));};
	public void set_dist03(java.lang.String value){assigns.and(QueryBuilder.set("s_dist_03",value));};
	public void set_dist04(java.lang.String value){assigns.and(QueryBuilder.set("s_dist_04",value));};
	public void set_dist05(java.lang.String value){assigns.and(QueryBuilder.set("s_dist_05",value));};
	public void set_dist06(java.lang.String value){assigns.and(QueryBuilder.set("s_dist_06",value));};
	public void set_dist07(java.lang.String value){assigns.and(QueryBuilder.set("s_dist_07",value));};
	public void set_dist08(java.lang.String value){assigns.and(QueryBuilder.set("s_dist_08",value));};
	public void set_dist09(java.lang.String value){assigns.and(QueryBuilder.set("s_dist_09",value));};
	public void set_dist10(java.lang.String value){assigns.and(QueryBuilder.set("s_dist_10",value));};
	public void set_data(java.lang.String value){assigns.and(QueryBuilder.set("s_data",value));};
	public stock(){super("stock", primarykeys);}
	public stock (Integer wid,Integer iid, String ... attr) {this(); find(wid,iid, attr);}
	public Row find(Integer wid,Integer iid, String ... attr){return super.find(Arrays.asList(wid,iid), attr); }}

