#!/usr/bin/bash

freeMem=$(grep MemFree /proc/meminfo | awk '{print $2}')

if [ $freeMem -gt 200000 ]
then 
	echo "crashed"
	echo "crashed with memory: "$freeMem | mail -s "Crash Alert" jonathan.axmann09@gmail.com
else
	echo "stable with memory: "$freeMem
fi



