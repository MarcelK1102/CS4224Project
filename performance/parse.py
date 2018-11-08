import re
import numpy as np
import os
xactreg = re.compile(r"Transaction (\w) was executed (\d*) times, took a total of (\d*\.\d*) seconds, average time per transaction was (\d*.\d*)")
overallreg = re.compile(r"#!#!STATS: number of transactions : (\d*), total transaction execution time : (\d*.\d*) seconds, transaction throughput : (\d*.\d*)")
output = open("stats.out", "w")
for filename in os.listdir("./"):
    if filename.endswith(".py"): 
        continue
    f = open(filename, "r")
    d = {"overall" : []}
    for line in f:
        m = xactreg.search(line)
        if m:
            transaction = m.group(1)
            n = int(m.group(2))
            time = float(m.group(3))
            avg = float(m.group(4))
            if transaction not in d:
                d[transaction] = [avg]
            else:
                print(avg)
                d[transaction].append(avg)
        m = overallreg.search(line)
        if m:
            d["overall"].append(float(m.group(3)))
    output.write(filename + "\n")
    for k,v in d.items():
        output.write("{}\t{}\n".format(k, np.mean(v)))
    output.write("\n")