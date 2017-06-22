# -*- coding: UTF-8 -*-


import csv
import urllib2
import json
from sets import Set

g = open("fifa-full-table.csv","a")
w = csv.writer(g, lineterminator='\n')

players = Set([])

for n in xrange(779):

	website = "https://www.easports.com/fifa/ultimate-team/api/fut/item?page=" + str(n)

	page = urllib2.urlopen(website).read()

	json_page = json.loads(page)

	print(json_page['page'])

	for player in json_page['items']:
		#name
		if (player['commonName']) == "":
			name = player['firstName'].encode('UTF-8') + ' ' + player['lastName'].encode('UTF-8')
		else:
			name = player['commonName'].encode('UTF-8')

		if name not in players:

			players.add(name)

			#league
			league = player['league']['name'].encode('UTF-8')

			#team
			team = player['club']['name'].encode('UTF-8')

			if (league != "Legends" and team != "Legends"):                                     

				#country
				country = player['nation']['name'].encode('UTF-8')

				print(league + ', ' + team + ', ' + name + ', ' + country)

				w.writerow((
		            ' ' + league,
		            ' ' + team,
		            ' ' + name,
		            ' ' + country
		        ))


