#!/usr/bin/env python
# -*- coding: cp1252 -*-.

import csv
g = open("/Users/1314940/Documents/GitHub/server/regenerate-players/test.csv","w")
w=csv.writer(g,lineterminator='\n')
player = 'Mesut Özil'

nameArr = player.split(" ")
if (len(nameArr) == 2):
    firstSpaceLast = nameArr[0][0] + " " + nameArr[1]
    firstDotLast = nameArr[0][0] + ". " + nameArr[1]
    firstLastDot = nameArr[0] + " " + nameArr[1][0] + "."
    last = nameArr[1]
else:
    firstSpaceLast = player
    firstDotLast = player
    firstLastDot = player
    last = " ".join(nameArr[1:]) #de Bruyne from multi-part name

a = bytearray('Özil')
#bytearray(b'\x99zil')
w.writerow((a))
w.writerow((
    player,
    firstSpaceLast,
    firstDotLast,
    firstLastDot,
    last
))
w.writerow(('wwwww'))