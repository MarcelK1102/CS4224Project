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
f = open("wrapper.java", "w")
alpha = re.compile(r'[\W]+')
for statement in buf.split(");"):
	tablename = ""
	columns = {}
	primarykeys = []
	found = False
	for line in statement.split('\n'):
		if not found and "CREATE TABLE" not in line : continue
		if "PRIMARY KEY" in line:
			primarykeys = [alpha.sub('',w) for w in line[line.find('('):].split(',')]
			continue
		words = line.split()
		if len(words) < 2: continue
		if not found:
			tablename = alpha.sub('',words[2])
			if len(tablename) <= 0:
				continue
			found = True
			continue
		columns[alpha.sub('',words[0])] = conv[alpha.sub('', words[1])]
	if len(columns) <= 0:continue
	f.write("public class {} extends tablebase{{\n".format(tablename))
	for k,v in columns.items():
		f.write("\tpublic {} {} = null;\n".format(v, k))
	f.write("\tString names[] = new String[] {{{}}};\n".format(", ".join(['"%s"' % x for x in columns.keys()])))
	f.write("\tObject values[] = new Object[] {{{}}};\n".format(", ".join( columns.keys() )))
	f.write("\tint nkeys = {};\n".format(len(primarykeys)))
	f.write("\tpublic {} () {{}}\n".format(tablename))
	f.write("\tpublic {}({}) {{ {}; }}\n".format(tablename, 
		', '.join(['%s %s' % (value, key) for key,value in columns.items()]),
		'; '.join(['this.%s = %s' % (key, key) for key,value in columns.items()])))
	f.write("\tpublic {}(Row r) throws NullPointerException {{ this({}); }}\n".format(
		tablename, ', '.join(['r.isNull("%s") ? null : r.%s("%s")' % (key, fs[value], key) for key,value in columns.items()])
	))
	
	f.write("}\n\n")

	

