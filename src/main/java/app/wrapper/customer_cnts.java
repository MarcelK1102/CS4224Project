package app.wrapper;
import com.datastax.driver.core.Row;
import java.util.Arrays;
import java.util.List;
import com.datastax.driver.core.querybuilder.QueryBuilder;
public class customer_cnts extends tablebase{
	private static final List<String> primarykeys = Arrays.asList("c_w_id","c_d_id","c_id");
	public int wid(){return keysvalue.get(0);}
	public int did(){return keysvalue.get(1);}
	public int id(){return keysvalue.get(2);}
	public Long balance(){return r.getTime("c_balance");};
	public Long ytdpayment(){return r.getTime("c_ytd_payment");};
	public Long paymentcnt(){return r.getTime("c_payment_cnt");};
	public Long deliverycnt(){return r.getTime("c_delivery_cnt");};
	public void set_wid(Integer value){assigns.and(QueryBuilder.set("c_w_id",value));};
	public void set_did(Integer value){assigns.and(QueryBuilder.set("c_d_id",value));};
	public void set_id(Integer value){assigns.and(QueryBuilder.set("c_id",value));};
	public void set_balance(Long value){assigns.and(QueryBuilder.set("c_balance",value));};
	public void set_ytdpayment(Long value){assigns.and(QueryBuilder.set("c_ytd_payment",value));};
	public void set_paymentcnt(Long value){assigns.and(QueryBuilder.set("c_payment_cnt",value));};
	public void set_deliverycnt(Long value){assigns.and(QueryBuilder.set("c_delivery_cnt",value));};
	public customer_cnts(){super("customer_cnts", primarykeys);}
	public customer_cnts (Integer wid,Integer did,Integer id, String ... attr) {this(); find(wid,did,id, attr);}
	public Row find(Integer wid,Integer did,Integer id, String ... attr){return super.find(Arrays.asList(wid,did,id), attr); }}

