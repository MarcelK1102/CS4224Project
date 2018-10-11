package app.wrapper;
import java.util.List;
import java.util.NoSuchElementException;

import com.datastax.driver.core.ColumnDefinitions.Definition;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.Clause;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Update;
import com.datastax.driver.core.querybuilder.Update.Assignments;

import app.Connector;

public abstract class tablebase {
	protected Row r;
	protected Assignments assigns;
	private final String tablename;
	private final List<String> keysname;
	private List<Integer> keysvalue;

	public tablebase(String tablename, List<String> keysname){this.tablename = tablename; this.keysname = keysname; assigns = QueryBuilder.update(tablename).with();}

	public Row find(List<Integer> keysvalue, String ... attr) {
		this.keysvalue = keysvalue;
		Select.Where w = (attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select().all()).from(tablename).where();
		for(int i = 0; i < keysname.size(); i++) {w.and(QueryBuilder.eq(keysname.get(i), keysvalue.get(i)));}
		this.r = Connector.s.execute(w).one();
		if(r == null) throw new NoSuchElementException("Unable to find " + tablename + " with keys " + this.keysname + " and values " + this.keysvalue);
		return r;
	}

	public boolean update(Clause ...clauses){	
		Update.Where w = assigns.where(QueryBuilder.eq(keysname.get(0), keysvalue.get(0)));
		for(int i = 1; i < keysname.size(); i++) {w.and(QueryBuilder.eq(keysname.get(i), keysvalue.get(i)));}		
		for(Clause c : clauses) w.onlyIf(c);
		Row r = Connector.s.execute(w).one();
		assigns = QueryBuilder.update(tablename).with();
		if(r.getBool(0))
			return true;
		return false;
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		int j = 0;
		for(Definition d : r.getColumnDefinitions()){
			if(j++ > 0) sb.append(", ");
			sb.append(d.getName() + " : " + r.getObject(d.getName()));
		}
		return sb.toString();
	}
}  