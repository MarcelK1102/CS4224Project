#!/bin/bash
cd /temp
rm -rf 4224-project-files*
wget https://www.comp.nus.edu.sg/~cs4224/4224-project-files.zip
unzip 4224-project-files.zip
cd 4224-project-files/data-files/
sed -i -e 's/,null,/,,/g' *.csv
cqlsh -f ~/createDB.cql
cqlsh -f ~/insertDB.cql