package builder;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
		
		

		String a = executePost("http://www.espnfc.us/api/transfers?limit=20");

		JsonObject jsonObj = new JsonParser().parse(a).getAsJsonObject();


		JsonArray data = jsonObj.getAsJsonObject("data").get("transferGroups").getAsJsonArray();


		for (int i=0; i<data.size(); i++) {
			JsonObject chunk = data.get(i).getAsJsonObject();
			JsonArray transfers = chunk.get("transfers").getAsJsonArray();
			for (int j=0; j<transfers.size(); j++) {
				String[] p = new String[2];

				JsonObject playerChunk = transfers.get(j).getAsJsonObject();
				String playerName = playerChunk.get("playerName").getAsString();
				String fromTeam = p[0] = playerChunk.get("fromTeamName").getAsString(); //fromTeam
				String toTeam = p[1] = playerChunk.get("toTeamName").getAsString(); //toTeam
				xferredPlayers.put(Simplify.simplifyName(playerName), p);

			}

		}
		
		for (HashMap.Entry<String, String> entry : leagueTeams.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			System.out.println(key);
		}

//		try {
//			Document document = Jsoup.connect(transferURL).followRedirects(true).get(); 
//			Elements players = document.select("div.transfer-module");  
//
//			for (Element thisPlayer: players) {
//				String[] p = new String[2];
//				String player = thisPlayer.select("div.transfer-module-header > h4 > a").text();
//				p[0] = thisPlayer.select("div.transfer-module-content > div.transfer-graphic > a.previous").text();
//				p[1] = thisPlayer.select("div.transfer-module-content > div.transfer-graphic > a.new").text();
//				xferredPlayers.put(Simplify.simplifyName(player), p);
//
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

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
						String[] lnt = getLeague(value[1], leagueTeams);
						String league = lnt[1];
						String team = lnt[0];
						if (league.equals("PMR")) {
							System.out.println(" " + line.trim());
						} else {
							System.out.println(" " + league.trim() + ", " + team.trim() + ", " + arr[2].trim() + "," + arr[3]);
						}
						written = true;
					} else if (Similar.similarity(key, arr[2].trim()) >= .95) {
						String[] lnt = getLeague(value[1], leagueTeams);
						String league = lnt[1];
						String team = lnt[0];						
						if (league.equals("PMR")) {
							System.out.println(" " + line.trim());
						} else {
							System.out.println(" " + league.trim()  + ", " + team.trim() + ", " + arr[2].trim() + "," + arr[3]);
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

	public static String[] getLeague(String team, HashMap<String, String> leagueTeams) {

		String[] a = {"",""};

		//league
		for (HashMap.Entry<String, String> entry : leagueTeams.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();


			if (Similar.similarity(team, key) >= .700) {
				a[0] = key; //team
				a[1] = value; //league
				break;
			} else {
				//do nothing...
			}
		}

		if (a[1].equals("")) {
			a[1] = "PMR";
		}

		return a;
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

	public static String executePost(String targetURL) {
		HttpURLConnection connection = null;

		try {
			//Create connection
			URL url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			//connection.setRequestProperty("Content-Type", 
			// "application/x-www-form-urlencoded");

			//		    connection.setRequestProperty("Content-Length", 
			//		        Integer.toString(urlParameters.getBytes().length));
			//		    connection.setRequestProperty("Content-Language", "en-US");  

			connection.setUseCaches(false);
			connection.setDoOutput(true);

			//Send request
			DataOutputStream wr = new DataOutputStream (
					connection.getOutputStream());
			//wr.writeBytes(urlParameters);
			wr.close();

			//Get Response  
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}





}
