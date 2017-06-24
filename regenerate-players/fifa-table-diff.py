import csv
from sets import Set

g = open("fifa-full-table-sorted.csv","r")
r = csv.reader(g)

g2 = open("fifa-full-table-transfers.csv","r")
r2 = csv.reader(g2)

table_small = Set([])
full_players = Set([])

for line in r:
	full_players.add(line[2])

for line2 in r2:
	table_small.add(line2[2])

print(len(full_players))
print(len(table_small))
print(table_small - full_players)