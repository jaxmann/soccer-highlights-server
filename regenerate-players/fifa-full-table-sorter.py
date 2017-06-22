import csv


reader = csv.reader(open("fifa-full-table.csv"))
sortedlist = sorted(reader, key=lambda row: (row[0], row[1], row[2], row[3]))
g2 = open("fifa-full-table-sorted.csv","a")
w2 = csv.writer(g2, lineterminator='\n')

for el in sortedlist:
	w2.writerow(el)


#after entire csv is built, sort by first column, then second, third, etc