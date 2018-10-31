import re

conv = {
	"INT" : "Integer",
	"ASCII" : "String",
	"BIGINT" : "Long",
	"BLOB" : "ByteBuffer",
	"BOOLEAN" : "Boolean",
	"COUNTER" : "Long",
	"DATE" : "LocalDate",
	"DECIMAL" : "BigDecimal",
	"DOUBLE" : "Double",
	"FLOAT" : "Float",
	"INET" : "InetAddress",
	"INT" : "Integer",
	"LIST" : "List",
	"MAP" : "Map",
	"SET" : "Set",
	"SMALLINT" : "Short",
	"TEXT" : "String",
	"TIME" : "Long",
	"TIMESTAMP" : "Date",
	"TIMEUUID" : "UUID",
	"TINYINT" : "Byte",
	"TUPLE" : "TupleValue",
	"UUID" : "UUID",
	"VARCHAR" : "String",
	"VARINT" : "BigInteger"
}

buf = open("db/createDB.cql", "r").read()
alpha = re.compile(r'[\W]+')
tables = {}
f = open("src/main/java/app/Table.java", "w")
f.write("""package app;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import org.bson.Document;
import org.bson.conversions.Bson;

public class Table{
	public interface ParseInterface<T> {
		public T parse(String value);
	}

	public static ParseInterface<Integer> parseInteger = s -> (int) Double.parseDouble(s);
	public static ParseInterface<String> parseString = s -> s;
	public static ParseInterface<Long> parseLong = s -> (long) Double.parseDouble(s);
	public static ParseInterface<Double> parseDouble = s -> Double.parseDouble(s);
	public static ParseInterface<Float> parseFloat = s -> Float.parseFloat(s);
	public static ParseInterface<Date> parseDate = s -> Date.from(LocalDate.parse(s).atStartOfDay(ZoneId.systemDefault()).toInstant());

	public static class Entry<T> {
		public String s;
		public Class<T> t;
		public ParseInterface<T> parser;
		public Entry(String s, Class<T> t, ParseInterface<T> parser){
			this.s = s;
			this.t = t;
			this.parser = parser;
		}

		public T from(Document d){
			try{
				return d.get(s, t);
			} catch(ClassCastException e){
				return parser.parse(d.get(s).toString());
			}
		}

		public Bson eq(T value){
			return Filters.eq(s, value);
		}

		public Bson lt(T value){
			return Filters.lt(s, value);
		}
		
		public Bson gt(T value){
			return Filters.gt(s, value);
		}

		public Bson inc(Number n){
			return Updates.inc(s, n);
		}
	}
""")
for statement in buf.split(");"):
	tablename = ""
	columns = {}
	primarykeys = []
	found = False
	for line in statement.split('\n'):
		line = line.upper()
		if not found and "CREATE TABLE" not in line : continue
		if "PRIMARY KEY" in line:
			primarykeys = [alpha.sub('',w) for w in line[line.find('('):].split(',')]
			continue
		if "WITH" in line:
			continue
		words = line.split()
		if len(words) < 2: continue
		if not found:
			tablename = "".join([c.upper() if not i else c.lower() for i, c in enumerate(words[2]) if not alpha.match(c)])
			if len(tablename) <= 0:
				continue
			found = True
			continue
		columns[words[0]] = conv[alpha.sub('', words[1])]
	if len(columns) <= 0:continue
	nkeys = len(primarykeys)
	ss = []
	ts = []
	for k, t in columns.items():
		f.write('\tpublic static final Entry<{}> {} = new Entry<>("{}",{}, {});\n'.format(t, k.lower(), k, t + ".class", "parse"+t))
f.write("}\n")
f.close()