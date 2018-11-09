import re
import numpy as np
import os
xactreg = re.compile(r"Transaction (\w) was executed (\d*) times, took a total of (\d*\.\d*) seconds, average time per transaction was (\d*.\d*)")
overallreg = re.compile(r"#!#!(STATS): number of transactions : (\d*), total transaction execution time : (\d*.\d*) seconds, transaction throughput : (\d*.\d*)")
output = open("stats.out", "w")
def add(d, k, v):
    if k in d:
        d[k].append(v)
    else:
        d[k] = [v]
for filename in os.listdir("./"):
    if filename.endswith(".py"): 
        continue
    f = open(filename, "r")
    timeavg = {}
    exectotal = {}
    timetotal = {}
    for line in f:
        m = xactreg.search(line)
        if not m:
            m = m = overallreg.search(line)
        if m:
            k = m.group(1)
            add(exectotal, k, int(m.group(2)))
            add(timetotal, k, float(m.group(3)))
            add(timeavg, k, float(m.group(4)))
    output.write(filename + "\n")
    for k in timeavg.keys():
        output.write("{}\t{}\t{}\t{}\n".format(k, np.mean(exectotal[k]), np.mean(timetotal[k]), np.mean(timeavg[k])))
    k = "STATS"
    output.write("{}\t{}\t{}\t{}\n".format(k, np.sum(exectotal[k]), np.sum(timetotal[k]), np.sum(timeavg[k])))
    output.write("\n")