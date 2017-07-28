package builder;


import java.io.BufferedReader;
import static java.nio.charset.StandardCharsets.ISO_8859_1;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class RemoveDupes {

	public static void main(String[] args) {

		HashMap<String, ArrayList<String>> players = new HashMap<String, ArrayList<String>>(); 
		//player name, team name
		HashSet<String> rows = new HashSet<String>();
		//entire rows (reference back to this for what to remove)

		////////////////////////////////////////////////////
		//read entire player list (along with team names) into hashmap, then iterate over hashmap and prompt user which players to keep
		///////////////////////////////

		try {
			BufferedReader reader = new BufferedReader (new FileReader("regenerate-players//full-table-tmp.csv"));
			String line;

			while ((line = reader.readLine()) != null) {
				String[] arr = line.split(","); //0 - league, 1 - team, 2 - name, 3 -country
				if (players.containsKey(arr[2])) {
					ArrayList<String> teams = players.get(arr[2]);
					teams.add(arr[1]);
					players.put(arr[2], teams);
				} else {
					ArrayList<String> teams = new ArrayList<String>();
					teams.add(arr[1]);
					players.put(arr[2], teams);
				}
				rows.add(line); //add entire line
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (HashMap.Entry<String, ArrayList<String>> entry : players.entrySet()) {
			String key = entry.getKey();
			ArrayList<String> values = entry.getValue();
			
			
		}


	}
}

