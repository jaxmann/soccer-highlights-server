package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import serv.simplify;

public class AccuracyTest {

	public static final String USER_AGENT = "User-Agent: desktop:PMR:v0.0.5 (by /u/pmrtest)"; //Required by reddit to be able to crawl their site

	public static HashMap<String, String> playerTeams;
	public static HashMap<String, String> playerCountry;
	public static HashSet<String> playerMatches;

	public static void main(String[] args) {

		playerTeams = populatePlayerTeams(); //list of players with team names associated
		playerCountry = populatePlayerCountry(); //list of players with country names associated
		playerMatches = loadPlayers(); //list of players with player syns associated
		
//		for (HashMap.Entry<String, String> entry : playerMatches.entrySet()) {
//			String key = entry.getKey();
//			String value = entry.getValue();
//			
//			System.out.println(key + "|" + value);
//
//		}
		
		/*Iterator iter = playerMatches.iterator();
		while (iter.hasNext()) {
			System.out.println(iter.next());
		}
*/
		String redditURL = "https://www.reddit.com/r/soccer/top/?sort=top&t=month";
		
		int i=0;
		
		while (i < 10) {
			crawl(redditURL);
			redditURL = nextURL(redditURL);
			i++;
			System.out.println("=========================");
		}
		
	}
	
	public static String nextURL(String currURL) {
		
		Document doc;
		Elements next;
		try {
			doc = Jsoup.connect(currURL).userAgent(USER_AGENT).timeout(0).get();
			next = doc.select("span.next-button > a[href]"); //Get the entire posts from the doc
			for (Element e : next) {
				return e.attr("href");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} //Get the url - Reddit only accepts 2 requests a minute. edit: 60/min i think? -jonathan
		
		return null;

	}

	public static void crawl(String redditURL) {
		//declare variables outside so they are not re-declared and use more memory each time
		Document document = null;
		Elements links = null;
		String title = "";
		String url = "";
		Matcher m = null;

		Element link = null;
		int i = 0;

		String score = "0-0";
		Pattern p = Pattern.compile("[\\[|(]?[0-9][\\]|)]?-[\\[|(]?[0-9][\\]|)]?"); //does the link text have something like (2-0) displaying the score of a game ^[0-9]+(-[0-9]+)


		try {
			document = Jsoup.connect(redditURL).userAgent(USER_AGENT).timeout(0).get(); //Get the url - Reddit only accepts 2 requests a minute. edit: 60/min i think? -jonathan
			links = document.select("div.thing"); //Get the entire posts from the doc

			for (i=links.size() - 1; i>=0; i--) { //doesn't allocate any new memory 
				link = links.get(i);

				m = p.matcher(link.select("p.title").select("a.title").text());
				if (m.find()) { 

					title = link.select("p.title").select("a.title").text();
					url = link.select("p.title").select("a.title").attr("href");
					score = title.substring(m.start(), m.end()).replaceAll("\\(|\\)|\\[|\\]|\\{|\\}", "");

					if (url.contains(".mp4") || url.contains("streamable")) { //only trigger if it's a video link

						System.out.println("Title is [" + title + "]");
						System.out.println("URL is [" + url + "]");
						String keyword = parseKeywords(title, url); //identify player keywords within play description
						System.out.println("Keyword is: [" + keyword + "]");
						System.out.println("-------------------------");


					} else {
						System.out.println("Non-video post found: [" + title + "] link [" + url + "]");
						System.out.println("-------------------------");

					}
				}

			}
		} catch (IOException e) {
			//exceptions involving connecting to reddit (i.e 503/502 http errors)
			e.printStackTrace();
		}




	}

	//keep going until all instances of any name are found - then select the first one and return it
	public static String parseKeywords(String postDescription, String url) { 

		String minName = findKeyword(postDescription);

		return minName; //i.e no player found in the csv
	}

	public static String findKeyword(String postDescription) {
		//could do postDescription = simplify.simplifyName(postDescription) here
		HashMap<String, Integer> playersFound = new HashMap<String, Integer>();
		HashMap<String, Integer> maybes = new HashMap<String, Integer>();

		for (String line : playerMatches) {

			//byte ptext[] = line.getBytes(ISO_8859_1);
			//String newline = new String(ptext, UTF_8);

			String[] s = line.split(",");
//			if (line.contains("zil")) {
//				System.out.println(line);
//			}
			for (String player : s) {

				// find player starting at start of string or after a whitespace with trailing whitespace, apostrophe, or line boundary
				String reg = "((^|\\s|\\()" + player + "(\\)|'|\\s|$))|((^|\\s|\\()" + simplify.simplifyName(player) + "(\\)|'|\\s|$))";
				
				
				Pattern p = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(postDescription);

				if (m.find()) {
					System.out.println("regex found [" + postDescription.substring(m.start(), m.end()).trim() + "] treated as [" + s[0] + "]");

					if (playersFound.containsKey(s[0])) {
						if (playersFound.get(s[0]) > m.end()) {
							playersFound.put(s[0], m.end());
						}
					} else {
						playersFound.put(s[0], m.end());
					}
					if (player.equals(s[0])) {
						if (!maybes.containsKey(player)) {
							maybes.put(player, 100); //full name found
						}
					}
					break;
				}
			}
		} 

		System.out.println("[" + playersFound.size() + "] matching players found in snippet");

		String minName = "no-player-found"; //fallback
		Integer minNum = 500; //should never be this high (position at which name is found inside string)
		for (HashMap.Entry<String, Integer> entry : playersFound.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();

			if (value < minNum) {
				minNum = value;
				minName = key;
			}
		}

		//if outright first choice from above, assign 100 points, if it wasn't the first name found or a snippet was found, assign 80
		///////////////////////////
		for (HashMap.Entry<String, Integer> entry : playersFound.entrySet()) {
			String key = entry.getKey();

			if (!key.equals(minName) && (!maybes.containsKey(key))) {
				maybes.put(key, 80); //partial/syn name found inside snippet
			} else if (key.equals(minName)) { //overwrite if already set as 100
				maybes.put(key, 120); //full name found and is minName (closest to front)
			}
		}
		/////////////////////////
		int maxPoints = 0;
		String maxPlayer = "no-player-found";
		// do a "join" here with the team name for the respective player
		////////////////////////////////////////////////////////////////
		for (HashMap.Entry<String, Integer> entry : maybes.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();

			if (playerTeams.containsKey(key.trim())) {
				String tm = playerTeams.get(key.trim()); //'Manchester City'
				String[] tmSplit = tm.split(" ");
				for (int i=0; i<tmSplit.length; i++) {
					if (postDescription.contains(tm) || postDescription.contains(simplify.simplifyName(tm))) {
						maybes.put(key, value + 50); //if entire team is contained in snippet
						System.out.println("Team treated as [" + tm + "] for player [" + key + "]");
					} else if (postDescription.contains(tmSplit[i]) || postDescription.contains(simplify.simplifyName(tmSplit[i]))) {
						maybes.put(key, value + 15); //add 15 points for each part of a team that is contained
					}
				}
			}
		}
		//////////////////////////////////////////////////////
		for (HashMap.Entry<String, Integer> entry : maybes.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();

			if (playerTeams.containsKey(key.trim())) {
				String tm = playerCountry.get(key.trim()); //'Germany'
				if (postDescription.contains(tm)) {
					maybes.put(key, value + 50); 
					System.out.println("Country treated as [" + tm + "] for player [" + key + "]");
				}
			}

		}
		//////////////////////////////////////////////////////
		for (HashMap.Entry<String, Integer> entry : maybes.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();

			System.out.println("["+key+"] : ["+value+"]");
			if (value > maxPoints) {
				maxPlayer = key;
				maxPoints = value;
			}
		}
		return maxPlayer;
	}

	public static HashMap<String, String> populatePlayerTeams() {
		HashMap<String, String> playerTeams = new HashMap<String, String>();

		try {
			BufferedReader reader = new BufferedReader (new FileReader("regenerate-players//fullTable.csv")); 
			String line;
			System.out.println("Loading playerTeam HashMap...");

			while ((line = reader.readLine()) != null) {

				String[] s = line.split(",");

				byte pplayer[] = s[2].trim().getBytes("Windows-1252");
				String newplayer = new String(pplayer, "Windows-1252");
				byte pteam[] = s[1].trim().getBytes("Windows-1252");
				String newteam = new String(pteam, "Windows-1252");

				playerTeams.put(newplayer, newteam);

			}

			System.out.println("playerTeam HashMap loaded.");
			reader.close();

		} catch (Exception e) {
		}
		return playerTeams;
	}


	public static HashMap<String, String> populatePlayerCountry() {

		HashMap<String, String> playerCountry = new HashMap<String, String>();

		try {
			BufferedReader reader = new BufferedReader (new FileReader("regenerate-players//fullTable.csv")); 
			String line;
			System.out.println("Loading playerCountry HashMap...");


			while ((line = reader.readLine()) != null) {

				String[] s = line.split(",");

				byte pplayer[] = s[2].trim().getBytes("Windows-1252");
				String newplayer = new String(pplayer, "Windows-1252");
				byte pteam[] = s[3].trim().getBytes("Windows-1252");
				String newcountry = new String(pteam, "Windows-1252");

				playerCountry.put(newplayer, newcountry);


			}
			System.out.println("playerCountry HashMap loaded.");

			reader.close();

		} catch (Exception e) {
		}
		return playerCountry;
	}	

	public static HashSet<String> loadPlayers() {

		HashSet<String> playerMatches = new HashSet<String>();
		System.out.println("Loading player HashSet...");

		try {
			BufferedReader reader = new BufferedReader (new FileReader("regenerate-players//synsTable.csv")); //backup version of this is "list-of-players2.csv"
			String line;

			while ((line = reader.readLine()) != null) {

				byte pplayer[] = line.getBytes("Windows-1252");
				String newplayer = new String(pplayer, "Windows-1252");
				
				playerMatches.add(newplayer);

			}

			System.out.println("player HashSet loaded.");
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return playerMatches;
	}


}
