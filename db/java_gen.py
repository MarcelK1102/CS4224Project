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

buf = open("createDB.cql", "r").read()
alpha = re.compile(r'[\W]+')
for statement in buf.split(");"):
	tablename = ""
	columns = {}
	primarykeys = []
	found = False
	for line in statement.split('\n'):
		line = line.upper()
		if not found and "CREATE TABLE" not in line : continue
		if "PRIMARY KEY" in line:
			primarykeys = [alpha.sub('',w).lower() for w in line[line.find('('):].split(',')]
			continue
		if "WITH" in line:
			continue
		words = line.split()
		if len(words) < 2: continue
		if not found:
			tablename = alpha.sub('',words[2]).lower()
			if len(tablename) <= 0:
				continue
			found = True
			continue
		columns[alpha.sub('',words[0]).lower()] = conv[alpha.sub('', words[1])]
	if len(columns) <= 0:continue
	f = open("../src/main/java/app/wrapper/{}.java".format(tablename), "w", newline = '\n')
	f.write("package app.wrapper;\n")
	f.write("import java.util.HashMap;\n")
	f.write("import java.util.Map;\n")
	f.write("import app.Connector;\n")
	f.write("import com.datastax.driver.core.Row;\n")
	f.write("import com.datastax.driver.core.querybuilder.QueryBuilder;\n")
	f.write("public class {} extends tablebase{{\n".format(tablename))
	f.write('\tprivate static final String tablename = "{}";\n'.format(tablename))
	f.write("\tprivate static final String names[] = new String[] {{{}}};\n".format(", ".join(['"%s"' % x for x in columns.keys()])))
	f.write("\tprivate static final int nkeys = {};\n".format(len(primarykeys)))
	f.write("\tprivate static final Map<String,Integer> namesi;\n")
	f.write("\tstatic {{namesi = new HashMap<String,Integer>();{} }}\n".format("".join(['namesi.put("{}",{});'.format(k, i) for i, k in enumerate(columns.keys())])))
	for i, (k,v) in enumerate(columns.items()):
		f.write("\tpublic {} {}(){{return ({})values[{}];}};\n".format(v, k, v, i))
	for i, (k,v) in enumerate(columns.items()):
		f.write("\tpublic void set_{}({} value){{values[{}] = value;}};\n".format(k, v, i))
	f.write('\tpublic {0} () {{super(tablename, names, namesi, nkeys);}}\n'.format(tablename))
	f.write('\tpublic {0} (Row r) {{super(tablename, names, namesi, nkeys, r);}}\n'.format(tablename))
	f.write('\tpublic {0} ({1}, String ... attr) {{this(Connector.s.execute(\n\t\t(attr.length > 0 ? QueryBuilder.select(attr) : QueryBuilder.select())\n\t\t.from("{0}")\n\t\t.where().{2})\n\t.one());}}\n'.format(
		tablename, 
		",".join(["{} {}".format(columns[k],k) for k in primarykeys]), 
		'.'.join(['and(QueryBuilder.eq("{0}", {0}))'.format(k) for k in primarykeys]) ))
	# f.write("\tpublic {}({}) {{ this(); {}; }}\n".format(tablename, 
	# 	',\n\t\t'.join(['%s %s' % (value, key) for key,value in columns.items()]),
	# 	';\n\t\t'.join(['this.%s = %s' % (key, key) for key,value in columns.items()])))
	# f.write("\tpublic {}(Row r) throws NullPointerException {{this({});}}\n".format(
	# 	tablename, ',\n\t\t'.join(['r.isNull("%s") ? null : r.%s("%s")' % (key, fs[value], key) for key,value in columns.items()])
	# ))
	
	f.write("}\n\n")

	

