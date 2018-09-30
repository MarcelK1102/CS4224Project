#!/bin/bash
cd /temp
rm -r 4224-project-files*
wget https://www.comp.nus.edu.sg/~cs4224/4224-project-files.zip
unzip 4224-project-files.zip
git clone https://github.com/morbitech1/cs4224-project project
cd project
git pull
cd /temp/4224-project-files/data-files/
cqlsh -f /temp/project/db/createDB.cql
cqlsh -f /temp/project/db/insertDB.cql