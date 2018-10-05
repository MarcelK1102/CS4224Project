package app.wrapper;
import java.util.Map;

import com.datastax.driver.core.ColumnDefinitions.Definition;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Clause;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Update.Assignments;
import com.datastax.driver.core.querybuilder.Update.Conditions;
import com.datastax.driver.core.querybuilder.Update.Where;

import app.Connector;

public abstract class tablebase {
	private static Session s = Connector.s;
	final String tablename;
	final String names[];
	final int nkeys;
	final Map<String, Integer> namesi;
	Object values[];

	public tablebase(String tablename, String names[], Map<String,Integer> namesi, int nkeys, Row r) {
		this.tablename = tablename; 
		this.names = names; 
		this.namesi = namesi; 
		this.nkeys = nkeys; 
		values = new Object[names.length];
		for(Definition d : r.getColumnDefinitions()){
			values[namesi.get(d.getName())] = r.getObject(d.getName());
		}
	}

	public Row update(Clause ...clauses){
		Assignments a = QueryBuilder.update(tablename).with();
		for(int i = nkeys; i < values.length; i++)
			if(values[i] != null) 
				a.and(QueryBuilder.set(names[i], values[i]));
			
		Where w = a.where(QueryBuilder.eq(names[0], values[0]));
		for(int i = 1; i < nkeys; i++)
			w.and(QueryBuilder.eq(names[i], values[i]));
		
		if(clauses.length > 0){
			Conditions c = w.onlyIf(clauses[0]);
			for(int i = 1; i < clauses.length; i++)
				c.and(clauses[i]);
		}
		return s.execute(a).one();
	}
}  