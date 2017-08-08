package builder;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import serv.Similar;
import serv.Simplify;

public class Transfers {
	
	public static HashMap<String, String> leagueTeams;

	public static void main(String[] args) {

		start();

	}

	public static void start() {
		
		leagueTeams = populateLeagueTeams();

		String transferURL = "http://www.espnfc.us/transfers?year=2017";

		HashMap<String,String[]> xferredPlayers = new HashMap<String, String[]>();

		try {
			Document document = Jsoup.connect(transferURL).followRedirects(true).get(); 
			Elements players = document.select("div.transfer-module");  

			for (Element thisPlayer: players) {
				String[] p = new String[2];
				String player = thisPlayer.select("div.transfer-module-header > h4 > a").text();
				p[0] = thisPlayer.select("div.transfer-module-content > div.transfer-graphic > a.previous").text();
				p[1] = thisPlayer.select("div.transfer-module-content > div.transfer-graphic > a.new").text();
				xferredPlayers.put(Simplify.simplifyName(player), p);
				
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		//transferred players (most recent page) loaded into hashmap ^, now compare to existing set of players and update as necessary (below)

		try {
			BufferedReader reader = new BufferedReader (new FileReader("/home/ec2-user/server/regenerate-players//fullTable.csv"));
			String line;


			while ((line = reader.readLine()) != null) {
				String[] arr = line.split(",");
				String name = Simplify.simplifyName(arr[2].trim());
				boolean written = false;

				for (HashMap.Entry<String, String[]> entry : xferredPlayers.entrySet()) {
					String key = entry.getKey();
					String[] value = entry.getValue();

					if (Simplify.simplifyName(key).equals(Simplify.simplifyName(arr[2].trim()))) {
						System.out.println(key + " is equal to " + arr[2].trim());
						String league = getLeague(value[1], leagueTeams);
						if (league.equals("PMR")) {
							System.out.println(" " + line.trim());
						} else {
							System.out.println(" " + league.trim() + ", " + value[1] + ", " + arr[2].trim() + "," + arr[3]);
						}
						written = true;
					} else if (Similar.similarity(key, arr[2].trim()) >= .95) {
						System.out.println(key + " is 95% conf match with " + arr[2].trim());
						String league = getLeague(value[1], leagueTeams);
						if (league.equals("PMR")) {
							System.out.println(" " + line.trim());
						} else {
							System.out.println(" " + league.trim()  + ", " + value[1] + ", " + arr[2].trim() + "," + arr[3]);
						}
						written = true;
					} 

				}

				if (!written) {
					//System.out.println(line);
				}

				/*if (xferredPlayers.containsKey(name)) {
					if (xferredPlayers.get(name)[0].equals(arr[1].trim())) {
						System.out.println(arr[0] + ", " + xferredPlayers.get(name)[1] + ", " + arr[2].trim() + "," + arr[3]);
					} else {
						System.out.println(line);
					}
				} else {
					System.out.println(line);
				}*/

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static String getLeague(String team, HashMap<String, String> leagueTeams) {
		
		for (HashMap.Entry<String, String> entry : leagueTeams.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			
			if (Similar.similarity(team, key) >= .700) {
				return value;
			} else {
				//do nothing...
			}
		}
		
		return "PMR";
	}

	public static HashMap<String, String> populateLeagueTeams() {

		HashMap<String, String> leagueTeam = new HashMap<String, String>();

		try {
			BufferedReader reader = new BufferedReader (new FileReader("regenerate-players/fullTable.csv")); 
			String line;

			String[] s;
			
			while ((line = reader.readLine()) != null) {

				s = line.split(",");
				
				leagueTeam.put(s[1], s[0]);
				
	
			}

			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return leagueTeam;
	}

	

}
