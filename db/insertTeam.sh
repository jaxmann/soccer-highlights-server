#!/bin/sh
while true
do
echo "Team?"
read Team
echo "League?"
read League

echo "Running" sqlite3 pmr.db "INSERT INTO Team \
         (TeamName,LeagueName,) \
              values ('$Team','$League');"
sqlite3 pmr.db "INSERT INTO Team (TeamName, LeagueName) values ('$Team','$League');"
done
