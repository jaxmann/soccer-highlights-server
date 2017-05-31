#!/usr/bin/env python
# -*- coding: UTF-8 -*-

from __future__ import print_function
import csv
from random import randint
import sys
from urllib2 import Request, urlopen, URLError
import json
import email.mime.text
import httplib2
import apiclient
#from apiclient import errors
import base64
import sys, os, re, StringIO
import email, mimetypes
import csv
import sys #maybe used in the future
#import requests
import time
from operator import itemgetter


g2=open("fullTable.csv","w")
w2=csv.writer(g2, lineterminator='\n')
# g3=open("synsTable.csv","w")
# w3=csv.writer(g3, lineterminator='\n')

headers = {'X-Auth-Token': 'd801fd51be9f4b74bfbb868c65b44043'}

allRequest = Request('http://api.football-data.org/v1/competitions', headers = headers)
print(allRequest)
try:
	allResponse = urlopen(allRequest)
	# print(json.dumps(json.loads(allResponse.read()), indent=4, sort_keys=True))
except URLError, e:
    print('Failed to connect to all leagues', e)

allReadResponse = allResponse.read()

obj = {}
obj['league'] = []

for league in json.loads(allReadResponse):
    leagueCaption = league['caption'] #"Premier League 16/17'

    leagueCaption = re.sub('[0-9]+/[0-9|\s]+', '', leagueCaption.rstrip())

    leagueCaption = leagueCaption.replace("Primera Division", "La Liga").replace("Liga Adelante", "Segunda Division")

    leagueLink = league['_links']['teams']['href']

    

    id = league['id']


    if (id == 426 or id == 427 or id == 430 or id == 431 or id == 433 or id == 434 or id ==436 or id==437 or id == 438 or id ==439 or id == 441):

        print("league link is " + leagueLink)

        leagueRequest = Request(leagueLink, headers = headers)

        try:
            leagueResponse = urlopen(leagueRequest)
        except URLError, e:
            print('Failed to connect to a team', e)
            time.sleep(10)
            try:
                leagueResponse = urlopen(leagueRequest)
                print("succeeded on second attempt")
            except URLError, e:
                print('Failed to connect to a team', e)

        leagueReadResponse = leagueResponse.read()

        leagueJSON = {}
        leagueJSON[leagueCaption] = []
        obj['league'].append(leagueJSON) # populate this object before appending it


        print("sleeping")
        time.sleep(60) #api limit is 50 calls/min - each league has roughly 30 teams so do 1 per min (too many for 2/min)
        print("waking up")

        for team in json.loads(leagueReadResponse)['teams']:

            teamName = team['name'].encode("utf-8")
            teamLink = team['_links']['players']['href']

            teamRequest = Request(teamLink, headers=headers)

            try:
                teamResponse = urlopen(teamRequest)
            except URLError, e:
                print('Failed to connect to a team', e)
                time.sleep(10)
                try:
                    teamResponse = urlopen(teamRequest)
                    print("succeeded on second attempt")
                except URLError, e:
                    print('Failed to connect to a team twice', e)


            teamReadResponse = teamResponse.read()

            teamJSON = {}
            teamJSON[teamName] = []
            obj['league'][len(obj['league'])-1][leagueCaption].append(teamJSON)


            if 'players' in json.loads(teamReadResponse):
                for player in json.loads(teamReadResponse)['players']:

                    playerName = player['name'].encode("utf-8")

                    countryName = 'NA'
                    if 'nationality' in player:
                        countryName = player['nationality'].replace('Korea, South','South Korea')

                    for x in countryName:
                        if (ord(x) > 128):
                            countryName = 'NA'
                            break;

                    playerNameTup = (playerName, countryName)
                    


                    obj['league'][len(obj['league'])-1][leagueCaption][len(obj['league'][len(obj['league'])-1][leagueCaption])-1][teamName].append(playerNameTup)

for league in obj['league']:
    for leagueNameKey, teamNameValue in league.items():
        teamNameValue.sort()
        for team in teamNameValue:
            for teamNameArr, playerNameArr in team.items():
                #print(teamNameArr)
                playerNameArr = sorted(playerNameArr, key=lambda x: x[0])
                #playerNameArr[0].sort()
                for player in playerNameArr:

                    #player = player.encode("utf-8")
                    #print(player)
                    try:
                        w2.writerow((
                            ' ' + leagueNameKey.strip(),
                            ' ' + teamNameArr,
                            ' ' + player[0],
                            ' ' + player[1]
                        ))
                    except:
                        print("error----")
                        print(player[0])
                        print(player[1])
                        print("----error")

                    # nameArr = player.split(" ")
                    # if (len(nameArr) == 2):
                    #     firstSpaceLast = nameArr[0][0] + " " + nameArr[1]
                    #     firstDotLast = nameArr[0][0] + ". " + nameArr[1]
                    #     firstLastDot = nameArr[0] + " " + nameArr[1][0] + "."
                    #     last = nameArr[1]
                    # else:
                    #     firstSpaceLast = player
                    #     firstDotLast = player
                    #     firstLastDot = player
                    #     last = " ".join(nameArr[1:]) #de Bruyne from multi-part name


                    # print(firstSpaceLast) # + "," + firstDotLast + "," + firstLastDot + "," + last "\n")

                    # w3.writerow((
                    #     player,
                    #     firstSpaceLast,
                    #     firstDotLast,
                    #     firstLastDot,
                    #     last
                    # ))



