#!/bin/bash

for (( c=0; c<$1; c++ ))
do
  t=$((c%5 + 1))
  
  if [ $3 -eq $t ]
  then
    echo $((c+1)).txt
    java -jar app.jar $4 9042 $2 < $((c+1)).txt > $((c+1))_$1_$2_output.txt &
  fi

done

wait

echo "Complete"