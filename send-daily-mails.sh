#!/usr/bin/bash

attachments=""

for att in $(find /home/ec2-user/server/logs  -name "logging.log.$(date '+%Y')-$(date '+%m')-$(date '+%d')*")
do
     
	attachments="$attachments -a $att"
	
done

echo "logs attached" | mail -s "Daily PMR Logs $(date '+%m')-$(date '+%d')" $attachments jonathan.axmann09@gmail.com


