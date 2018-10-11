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
	nkeys = len(primarykeys)
	primarykeys = []
	for i, k in enumerate(columns.keys()):
		if i >= nkeys: break
		primarykeys.append(k)
	f = open("../src/main/java/app/wrapper/{}.java".format(tablename), "w", newline = '\n')
	f.write("package app.wrapper;\n")
	f.write("import com.datastax.driver.core.Row;\n")
	f.write("import java.util.Arrays;\n")
	f.write("import java.util.List;\n")
	f.write("import com.datastax.driver.core.querybuilder.QueryBuilder;\n")
	f.write("public class {} extends tablebase{{\n".format(tablename))
	f.write('\tprivate static final List<String> primarykeys = Arrays.asList({});\n'.format(",".join('"{}"'.format(k) for k in primarykeys)))
	for i, k in enumerate(primarykeys):
		f.write("\tpublic int {}(){{return keysvalue.get({});}}\n".format("".join(k.split('_')[1:]),i))
	for i, (k,v) in enumerate(columns.items()):
		if i < len(primarykeys):continue
		f.write('\tpublic {} {}(){{return r.{}("{}");}};\n'.format(v, "".join(k.split('_')[1:]), fs[v], k))
	for i, (k,v) in enumerate(columns.items()):
		f.write('\tpublic void set_{}({} value){{assigns.and(QueryBuilder.set("{}",value));}};\n'.format("".join(k.split('_')[1:]), v, k))
	f.write('\tpublic {0}(){{super("{0}", primarykeys);}}\n'.format(tablename))
	f.write('\tpublic {0} ({1}, String ... attr) {{this(); find({2}, attr);}}\n'.format(
		tablename, 
		",".join(["{} {}".format(columns[k],"".join(k.split('_')[1:])) for k in primarykeys]), 
		",".join("".join(k.split('_')[1:]) for k in primarykeys)))
	f.write("\tpublic Row find(%s, String ... attr){return super.find(Arrays.asList(%s), attr); }" %
	 	(",".join(["{} {}".format(columns[k],"".join(k.split('_')[1:])) for k in primarykeys]),
		 ",".join("".join(k.split('_')[1:]) for k in primarykeys)))
	f.write("}\n\n")