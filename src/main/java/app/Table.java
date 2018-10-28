package app;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

public class Table{
	public static class Entry<T> {
		public String s;
		public Class<T> t;
		public Entry(String s, Class<T> t){
			this.s = s;
			this.t = t;
		}

		public T from(Document d){
			return d.get(s, t);
		}

		public Bson eq(T value){
			return Filters.eq(s, value);
		}

		public Bson gt(T value){
			return Filters.gt(s, value);
		}

		public Bson lt(T value){
			return Filters.lt(s, value);
		}

		public Bson inc(Number n){
			return Updates.inc(s, n);
		}
	}
	public static final Entry<java.lang.String> w_name = new Entry<>("W_NAME",java.lang.String.class);
	public static final Entry<java.lang.String> w_zip = new Entry<>("W_ZIP",java.lang.String.class);
	public static final Entry<Double> w_ytd = new Entry<>("W_YTD",Double.class);
	public static final Entry<Double> w_tax = new Entry<>("W_TAX",Double.class);
	public static final Entry<java.lang.String> w_state = new Entry<>("W_STATE",java.lang.String.class);
	public static final Entry<java.lang.String> w_street_1 = new Entry<>("W_STREET_1",java.lang.String.class);
	public static final Entry<java.lang.String> w_street_2 = new Entry<>("W_STREET_2",java.lang.String.class);
	public static final Entry<java.lang.String> w_city = new Entry<>("W_CITY",java.lang.String.class);
	public static final Entry<Integer> w_id = new Entry<>("W_ID",Integer.class);
	public static final Entry<java.lang.String> d_city = new Entry<>("D_CITY",java.lang.String.class);
	public static final Entry<java.lang.String> d_zip = new Entry<>("D_ZIP",java.lang.String.class);
	public static final Entry<Integer> d_w_id = new Entry<>("D_W_ID",Integer.class);
	public static final Entry<Double> d_tax = new Entry<>("D_TAX",Double.class);
	public static final Entry<Double> d_ytd = new Entry<>("D_YTD",Double.class);
	public static final Entry<java.lang.String> d_street_2 = new Entry<>("D_STREET_2",java.lang.String.class);
	public static final Entry<java.lang.String> d_street_1 = new Entry<>("D_STREET_1",java.lang.String.class);
	public static final Entry<java.lang.String> d_state = new Entry<>("D_STATE",java.lang.String.class);
	public static final Entry<Double> d_next_o_id = new Entry<>("D_NEXT_O_ID",Double.class);
	public static final Entry<java.lang.String> d_name = new Entry<>("D_NAME",java.lang.String.class);
	public static final Entry<Integer> d_id = new Entry<>("D_ID",Integer.class);
	public static final Entry<java.lang.String> c_middle = new Entry<>("C_MIDDLE",java.lang.String.class);
	public static final Entry<java.lang.String> c_city = new Entry<>("C_CITY",java.lang.String.class);
	public static final Entry<java.lang.String> c_street_2 = new Entry<>("C_STREET_2",java.lang.String.class);
	public static final Entry<Integer> c_w_id = new Entry<>("C_W_ID",Integer.class);
	public static final Entry<java.util.Date> c_since = new Entry<>("C_SINCE",java.util.Date.class);
	public static final Entry<Double> c_discount = new Entry<>("C_DISCOUNT",Double.class);
	public static final Entry<Integer> c_d_id = new Entry<>("C_D_ID",Integer.class);
	public static final Entry<Integer> c_id = new Entry<>("C_ID",Integer.class);
	public static final Entry<java.lang.String> c_phone = new Entry<>("C_PHONE",java.lang.String.class);
	public static final Entry<Integer> c_payment_cnt = new Entry<>("C_PAYMENT_CNT",Integer.class);
	public static final Entry<java.lang.String> c_credit = new Entry<>("C_CREDIT",java.lang.String.class);
	public static final Entry<java.lang.String> c_zip = new Entry<>("C_ZIP",java.lang.String.class);
	public static final Entry<java.lang.String> c_last = new Entry<>("C_LAST",java.lang.String.class);
	public static final Entry<java.lang.String> c_data = new Entry<>("C_DATA",java.lang.String.class);
	public static final Entry<java.lang.String> c_street_1 = new Entry<>("C_STREET_1",java.lang.String.class);
	public static final Entry<Float> c_ytd_payment = new Entry<>("C_YTD_PAYMENT",Float.class);
	public static final Entry<Double> c_balance = new Entry<>("C_BALANCE",Double.class);
	public static final Entry<Double> c_credit_lim = new Entry<>("C_CREDIT_LIM",Double.class);
	public static final Entry<Integer> c_delivery_cnt = new Entry<>("C_DELIVERY_CNT",Integer.class);
	public static final Entry<java.lang.String> c_state = new Entry<>("C_STATE",java.lang.String.class);
	public static final Entry<java.lang.String> c_first = new Entry<>("C_FIRST",java.lang.String.class);
	public static final Entry<Integer> o_c_id = new Entry<>("O_C_ID",Integer.class);
	public static final Entry<java.util.Date> o_entry_d = new Entry<>("O_ENTRY_D",java.util.Date.class);
	public static final Entry<Integer> o_carrier_id = new Entry<>("O_CARRIER_ID",Integer.class);
	public static final Entry<Integer> o_w_id = new Entry<>("O_W_ID",Integer.class);
	public static final Entry<Double> o_all_local = new Entry<>("O_ALL_LOCAL",Double.class);
	public static final Entry<Integer> o_d_id = new Entry<>("O_D_ID",Integer.class);
	public static final Entry<Double> o_ol_cnt = new Entry<>("O_OL_CNT",Double.class);
	public static final Entry<Integer> o_id = new Entry<>("O_ID",Integer.class);
	public static final Entry<Integer> i_im_id = new Entry<>("I_IM_ID",Integer.class);
	public static final Entry<Double> i_price = new Entry<>("I_PRICE",Double.class);
	public static final Entry<Integer> i_id = new Entry<>("I_ID",Integer.class);
	public static final Entry<java.lang.String> i_data = new Entry<>("I_DATA",java.lang.String.class);
	public static final Entry<java.lang.String> i_name = new Entry<>("I_NAME",java.lang.String.class);
	public static final Entry<java.lang.String> s_dist_09 = new Entry<>("S_DIST_09",java.lang.String.class);
	public static final Entry<java.lang.String> s_data = new Entry<>("S_DATA",java.lang.String.class);
	public static final Entry<java.lang.String> s_dist_10 = new Entry<>("S_DIST_10",java.lang.String.class);
	public static final Entry<Integer> s_w_id = new Entry<>("S_W_ID",Integer.class);
	public static final Entry<java.lang.String> s_dist_03 = new Entry<>("S_DIST_03",java.lang.String.class);
	public static final Entry<Double> s_ytd = new Entry<>("S_YTD",Double.class);
	public static final Entry<Integer> s_remote_cnt = new Entry<>("S_REMOTE_CNT",Integer.class);
	public static final Entry<java.lang.String> s_dist_02 = new Entry<>("S_DIST_02",java.lang.String.class);
	public static final Entry<Integer> s_i_id = new Entry<>("S_I_ID",Integer.class);
	public static final Entry<java.lang.String> s_dist_06 = new Entry<>("S_DIST_06",java.lang.String.class);
	public static final Entry<java.lang.String> s_dist_01 = new Entry<>("S_DIST_01",java.lang.String.class);
	public static final Entry<java.lang.String> s_dist_04 = new Entry<>("S_DIST_04",java.lang.String.class);
	public static final Entry<Long> s_quantity = new Entry<>("S_QUANTITY",Long.class);
	public static final Entry<java.lang.String> s_dist_07 = new Entry<>("S_DIST_07",java.lang.String.class);
	public static final Entry<Integer> s_order_cnt = new Entry<>("S_ORDER_CNT",Integer.class);
	public static final Entry<java.lang.String> s_dist_05 = new Entry<>("S_DIST_05",java.lang.String.class);
	public static final Entry<java.lang.String> s_dist_08 = new Entry<>("S_DIST_08",java.lang.String.class);
	public static final Entry<Integer> ol_w_id = new Entry<>("OL_W_ID",Integer.class);
	public static final Entry<Integer> ol_d_id = new Entry<>("OL_D_ID",Integer.class);
	public static final Entry<java.util.Date> ol_delivery_d = new Entry<>("OL_DELIVERY_D",java.util.Date.class);
	public static final Entry<Double> ol_quantity = new Entry<>("OL_QUANTITY",Double.class);
	public static final Entry<Integer> ol_number = new Entry<>("OL_NUMBER",Integer.class);
	public static final Entry<Integer> ol_i_id = new Entry<>("OL_I_ID",Integer.class);
	public static final Entry<Integer> ol_o_id = new Entry<>("OL_O_ID",Integer.class);
	public static final Entry<Integer> ol_supply_w_id = new Entry<>("OL_SUPPLY_W_ID",Integer.class);
	public static final Entry<Double> ol_amount = new Entry<>("OL_AMOUNT",Double.class);
	public static final Entry<java.lang.String> ol_dist_info = new Entry<>("OL_DIST_INFO",java.lang.String.class);
}
