     _____     ___ ___     _ __  
    /\ '__`\ /' __` __`\  /\`'__\
    \ \ \L\ \/\ \/\ \/\ \ \ \ \/ 
     \ \ ,__/\ \_\ \_\ \_\ \ \_\ 
      \ \ \/  \/_/\/_/\/_/  \/_/ 
       \ \_\                     
        \/_/   

# PMR &lt; 20
serving up sick highlights since the year 19 just the server though

&nbsp;

### PMR Services

[pmr-server](https://github.com/jaxmann/pmr-server)

[pmr-rest](https://github.com/kevinchesser/pmr-rest)

[pmr-web](https://github.com/jaxmann/pmr-web)

## Usage

https://github.com/jaxmann/insert-delete-sqlite-jar - make this into a jar 

To run, just run main.java. Settings are configured through pmr-web and http requests are handled through pmr-rest 

 - Logs are output daily into /logs. 
 - delete-timeq.jar needs to be run every 60 seconds on a cronjob.
 - db is Sqlite3


 To regenerate player tables (after a transfer window, for instance), run the python script inside regenerate-tables and then run the UniqueSynonyms java program inside src and redirect the output to synsTable inside regenerate-players), then reload the db files using the skeletons
