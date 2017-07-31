awk -F"," '{print $1}' fullTable.csv | sort | uniq
