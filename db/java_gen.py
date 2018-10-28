import re

conv = {
	"INT" : "Integer",
	"ASCII" : "java.lang.String",
	"BIGINT" : "Long",
	"BLOB" : "java.nio.ByteBuffer",
	"BOOLEAN" : "Boolean",
	"COUNTER" : "Long",
	"DATE" : "LocalDate",
	"DECIMAL" : "java.math.BigDecimal",
	"DOUBLE" : "Double",
	"FLOAT" : "Float",
	"INET" : "java.net.InetAddress",
	"INT" : "Integer",
	"LIST" : "java.util.List",
	"MAP" : "java.util.Map",
	"SET" : "java.util.Set",
	"SMALLINT" : "Short",
	"TEXT" : "java.lang.String",
	"TIME" : "Long",
	"TIMESTAMP" : "java.util.Date",
	"TIMEUUID" : "java.util.UUID",
	"TINYINT" : "Byte",
	"TUPLE" : "TupleValue",
	"USER" : "defined types	getUDTValue	UDTValue",
	"UUID" : "java.util.UUID",
	"VARCHAR" : "java.lang.String",
	"VARINT" : "java.math.BigInteger"
}

fs = {
	"java.lang.String" : "getString",
	"Long" : "getLong",
	"java.nio.ByteBuffer" : "getBytes",
	"Boolean" : "getBool",
	"Long" : "getLong",
	"LocalDate" : "getDate",
	"java.math.BigDecimal" : "getDecimal",
	"Double" : "getDouble",
	"Float" : "getFloat",
	"java.net.InetAddress" : "getInet",
	"Integer" : "getInt",
	"java.util.List" : "getList",
	"java.util.Map" : "getMap",
	"java.util.Set" : "getSet",
	"short" : "getShort",
	"java.lang.String" : "getString",
	"Long" : "getTime",
	"java.util.Date" : "getTimestamp",
	"java.util.UUID" : "getUUID",
	"Byte" : "getByte",
	"TupleValue" : "getTupleValue",
	"java.util.UUID" : "getUUID",
	"java.lang.String" : "getString",
	"java.math.BigInteger" : "getVarint"
}

buf = open("db/createDB.cql", "r").read()
alpha = re.compile(r'[\W]+')
tables = {}
f = open("src/main/java/app/Table.java", "w")
f.write("""package app;

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
		f.write('\tpublic static final Entry<{}> {} = new Entry<>("{}",{});\n'.format(t, k.lower(), k, t + ".class"))
f.write("}\n")
f.close()