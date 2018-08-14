
# Soccer Highlights

Serving up sick highlights just the server though

&nbsp;

### S Services

[s-server](https://github.com/jaxmann/s-server)

[pmr-rest](https://github.com/kevinchesser/pmr-rest)

[pmr-web](https://github.com/jaxmann/pmr-web)

## Usage

https://github.com/jaxmann/insert-delete-sqlite-jar - make this into a jar 

To run, just run main.java. Settings are configured through pmr-web and http requests are handled through pmr-rest 

 - Logs are output daily into /logs. 
 - delete-timeq.jar needs to be run every 60 seconds on a cronjob.
 - db is Sqlite3


 To regenerate player tables (after a transfer window, for instance), run the python script inside regenerate-tables and then run the UniqueSynonyms java program inside src and redirect the output to synsTable inside regenerate-players), then reload the db files using the skeletons
