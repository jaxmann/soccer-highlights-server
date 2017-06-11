iconv -f CP1252 -t UTF-8 regenerate-players/synsTable.csv > regenerate-players/EsynsTable.csv
iconv -f CP1252 -t UTF-8 regenerate-players/mlsTable.csv > regenerate-players/EmlsTable.csv
chmod 755 * && chmod 755 db/* && sudo ./gradlew run -Dexec.args="prod"
