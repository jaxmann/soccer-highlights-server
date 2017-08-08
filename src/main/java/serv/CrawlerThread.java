package serv;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import serv.VideoUpload;
import twitter4j.JSONException;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class CrawlerThread implements Runnable {

	public static final String USER_AGENT = "User-Agent: desktop:PMR:v0.1.5 (by /u/pmrtest)"; //Required by reddit to be able to crawl their site
	public static Logger logger = Logger.getLogger(CrawlerThread.class);
	private static GmailService service;
	private static String redditenv;
	public static HashMap<String, String> playerTeams;
	public static HashMap<String, String> playerCountry;
	public static HashMap<String, String> playerLeague;
	public static HashSet<String> playerMatches;

	public CrawlerThread(String env) {
		service = new GmailService();
		redditenv = env;
	}

	public void run() {

		PropertyConfigurator.configure("log4j-configuration.txt"); //configure log4j binding with properties from log4j-configuration file

		String redditURL = "";
		if (redditenv.equals("test")) {
			redditURL = "http://www.reddit.com/r/soccerpmr/new";
			logger.info("Running in the test environment...");
		} else {
			redditURL = "http://www.reddit.com/r/soccer/new";
			logger.info("Running in the live environment...");
		}

		//declare variables outside so they are not re-declared and use more memory each time
		Document document = null;
		Elements links = null;
		int refreshTime = 60000;
		String inputTime = "";
		String time = "";
		String title = "";
		String url = "";
		Matcher m = null;
		Date dateReddit = null;
		String keyword = "";
		ArrayList<String> subbedUsers = null;
		Element link = null;
		int i = 0;

		Calendar cal = Calendar.getInstance();
		Date mostRecentPostTime = cal.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

		Pattern p = Pattern.compile("\\D[\\[|(]?[0-9][\\]|)]?[-|:][\\[|(]?[0-9][\\]|)]?\\D"); //does the link text have something like (2-0) displaying the score of a game ^[0-9]+(-[0-9]+)

		playerTeams = populatePlayerTeams(); //list of players with team names associated
		playerCountry = populatePlayerCountry(); //list of players with country names associated
		playerLeague = populatePlayerLeague(); //list of players with league names associated
		playerMatches = loadPlayers(); //list of players with player syns associated

		while (true) { //run forever unless stopped

			try {
				refreshTime = getSleepTime();
				//logger.info("Current refresh rate: " + refreshTime / 60000 + " min");
				Thread.sleep(refreshTime); //refresh page every n/1k seconds 
			} catch (InterruptedException e2) {
				logger.error(e2.toString());
			}
			try {
				document = Jsoup.connect(redditURL).userAgent(USER_AGENT).timeout(0).get(); //Get the url - Reddit only accepts 2 requests a minute. edit: 60/min i think? -jonathan
				links = document.select("div.thing"); //Get the entire posts from the doc

				logger.info("Most recent post time: [" + mostRecentPostTime + "]");

				for (i=links.size() - 1; i>=0; i--) { //doesn't allocate any new memory 
					link = links.get(i);
					inputTime = link.select("p.tagline").select("time").attr("title");
					try {
						dateReddit = formatter.parse(inputTime);
						if (mostRecentPostTime.compareTo(dateReddit) < 0) {
							m = p.matcher(link.select("p.title").select("a.title").text());
							if (m.find()) { 
								time = link.select("p.tagline").select("time").attr("title");
								title = link.select("p.title").select("a.title").text();
								url = link.select("p.title").select("a.title").attr("href");

								mostRecentPostTime = formatter.parse(link.select("p.tagline").select("time").attr("title")); //update most recent post time

								if (url.contains(".mp4") || url.contains("streamable") || url.contains("imgtc") || url.contains("clippit")) { //only trigger if it's a video link

									logger.info("New post found: [" + title + "] at [" + time + "]");

									keyword = parseKeywords(title, url); //identify player keywords within play description
									logger.info("Keyword is: [" + keyword + "]");
									subbedUsers = findSubscribedUsers(keyword, title);
									logger.info("Number of subbed users: [" + subbedUsers.size() + "]");
									if (subbedUsers.size() != 0) { //if no users are subscribed to a particular player, don't try to send email (it will fail)
										if (!isInvalidTQ(title, keyword)) {
											if (redditenv.equals("test")) {
												logger.info("Keyword not in tq... but test env");
											} else {
												logger.info("Keyword not in tq... calling sendEmail");
												sendEmail(url, keyword, subbedUsers, title); //send email to users who match keywords - send them the url, use keyword in email title/body; user's email is returned from sql query
											}
										} else {
											logger.info("Not calling sendEmail because keyword is already in TQ");
										}
									} 

									addToTimeq(keyword, title);
								} else {
									logger.info("Non-video post found: [" + title + "] at [" + time + "] link [" + url + "]");
								}
							}
						}
					} catch (ParseException e1) {
						logger.error(e1.toString());
					}	
				}
			} catch (IOException e) {
				//exceptions involving connecting to reddit (i.e 503/502 http errors)
				logger.error(e.toString());
			}
		}
	}

	public static String[] findKeyword(String postDescription) {

		String[] sToReturn = new String[2];

		HashMap<String, Integer> playersFound = new HashMap<String, Integer>();
		HashMap<String, Integer> maybes = new HashMap<String, Integer>();

		for (String line : playerMatches) {

			String[] s = line.split(",");

			for (String player : s) {

				// find player starting at start of string or after a whitespace with trailing whitespace, apostrophe, or line boundary
				String reg = "((^|\\s|\\()" + player + "(\\)|'|\\s|$))|((^|\\s|\\()" + Simplify.simplifyName(player) + "(\\)|'|\\s|$))";


				Pattern p = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(postDescription);

				if (m.find() && (m.end() - m.start() > 2) && (postDescription.substring(m.start(), m.end()).trim().length() > 2)) {

					logger.info("regex found [" + postDescription.substring(m.start(), m.end()).trim() + "] treated as [" + s[0] + "]");

					if (playersFound.containsKey(s[0])) {
						if (playersFound.get(s[0]) > m.end()) {
							playersFound.put(s[0], m.end());
						}
					} else {
						playersFound.put(s[0], m.end());
					}

					if (player.equals(s[0])) {
						if (!maybes.containsKey(player)) {
							maybes.put(player, 160); //full name found
						}
					}
					break;
				}
			}
		} 

		logger.info("[" + playersFound.size() + "] matching players found in snippet");

		String minName = "no-player-found"; //fallback
		Integer minNum = 500; //should never be this high (position at which name is found inside string)
		for (HashMap.Entry<String, Integer> entry : playersFound.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();

			if (value < minNum) {
				minNum = value;
				minName = key;
			}
			//find which name occurs nearest to the front of the postdescription
		}

		//if outright first choice from above, assign 100 points, if it wasn't the first name found or a snippet was found, assign 80
		///////////////////////////
		for (HashMap.Entry<String, Integer> entry : playersFound.entrySet()) {
			String key = entry.getKey();

			if (!key.equals(minName) && (!maybes.containsKey(key))) {
				maybes.put(key, 80); //partial/syn name found inside snippet
			} else if (key.equals(minName)) { //overwrite if already set as 160
				maybes.put(key, 100); //full name found and is minName (closest to front)
			}
		}
		for (HashMap.Entry<String, Integer> entry : maybes.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();

			logger.info("Initial -- ["+key+"] : ["+value+"]");
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

				String cleanedPostDescription = postDescription.replaceAll("[Uu]nited","");
				String teamRegex = "((^|\\s|\\()" + tm + "(\\)|'|\\s|$))|((^|\\s|\\()" + Simplify.simplifyName(tm) + "(\\)|'|\\s|$))";

				Pattern teamP = Pattern.compile(teamRegex, Pattern.CASE_INSENSITIVE);
				Matcher teamM = teamP.matcher(cleanedPostDescription);

				if (teamM.find()) {
					maybes.put(key, value + 60); //if ENTIRE team is contained in snippet
					logger.info("Team treated as [" + tm + "] for player [" + key + "] and added 60 (full team)");
				} else {
					String[] tmSplit = tm.split(" ");
					for (int i=0; i<tmSplit.length; i++) {

						String teamRegexPartial = "((^|\\s|\\()" + tmSplit[i] + "(\\)|'|\\s|$))|((^|\\s|\\()" + Simplify.simplifyName(tmSplit[i]) + "(\\)|'|\\s|$))";

						Pattern teamPPartial = Pattern.compile(teamRegexPartial, Pattern.CASE_INSENSITIVE);
						Matcher teamMPartial = teamPPartial.matcher(cleanedPostDescription);

						if (teamMPartial.find()) {
							maybes.put(key, value + 40); //add 15 points for each part of a team that is contained
							logger.info("Team treated as [" + tm + "] for player [" + key + "] and added 40 (partial)");
						}
					}
				}
			}
		}
		/////////////////rip out team names from string and if player sitll found, +100/////////////////////////////////////
		String cleanedPD = postDescription;
		for (HashMap.Entry<String, String> entry : playerTeams.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue(); //teamname

			for (String s : value.split(" ")) {
				cleanedPD = cleanedPD.replaceAll("\\s" + s + "\\s|^" + s + "\\s|\\s" + s + "$", "");
			}

		}
		logger.info("Stripped string is [" + cleanedPD + "]");
		for (HashMap.Entry<String, Integer> entry : maybes.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();

			for (String a : key.split(" ")) {
				if (cleanedPD.contains(a)) {
					maybes.put(key, value + 20);
					logger.info("Added 40 to [" + key + "] because still contained in PD when teams removed");
					break;
				}
			}

		}
		//////////////////////////////////////////////////////
		for (HashMap.Entry<String, Integer> entry : maybes.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();

			if (playerCountry.containsKey(key.trim())) {
				String cn = playerCountry.get(key.trim()); //'Germany'

				String countryRegex = "((^|\\s|\\()" + cn + "(\\)|'|\\s|$))|((^|\\s|\\()" + Simplify.simplifyName(cn) + "(\\)|'|\\s|$))";

				Pattern countryP = Pattern.compile(countryRegex, Pattern.CASE_INSENSITIVE);
				Matcher countryM = countryP.matcher(postDescription);

				if (countryM.find()) {
					maybes.put(key, value + 50); 
					logger.info("Country treated as [" + cn + "] for player [" + key + "]");
				}
			}
		}
		//// checking other teams in the same league
		//if no teams from same league match, deduct 20 pts, if at least one does, add 20 
		ArrayList<String> teamsSameLeague = new ArrayList<String>();

		Connection connection1 = null;
		ResultSet resultSet1 = null;
		Statement statement1 = null;

		for (HashMap.Entry<String, Integer> entry : maybes.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();

			try {
				String url = "jdbc:sqlite:../server/db/player.db";
				connection1 = DriverManager.getConnection(url);
				String sql = "Select league from player where player = ' " + key.replaceAll("'", "''") + "';"; //extra single quote to escape ' in sqlite3
				statement1 = connection1.createStatement();
				resultSet1 = statement1.executeQuery(sql);
				String league = "";
				while(resultSet1.next()) {
					league  = resultSet1.getString("league");
				}
				league = league.trim();

				Connection connection2 = null;
				ResultSet resultSet2 = null;
				Statement statement2 = null;

				try {
					connection2 = DriverManager.getConnection(url);
					String sql2 = "Select distinct Team from player where league= ' " + league +  "';";
					statement2 = connection2.createStatement();
					resultSet2 = statement2.executeQuery(sql2);

					while(resultSet2.next()) {
						teamsSameLeague.add(Simplify.simplifyName(resultSet2.getString("team").trim().replaceAll("^[a-zA-Z]{1,3}\\s|\\s[a-zA-Z]{1,3}$|\\s[a-zA-Z]{1,3}\\s|[0-9]+", "").trim()));
					}

				} catch (SQLException e) {
					logger.error(e.toString());
				} finally {
					try {
						if (connection2 != null) {
							resultSet2.close();
							statement2.close();
							connection2.close();
						}
					} catch (SQLException ex) {
						logger.error(ex.toString());
					}
				}

			} catch (SQLException e) {
				logger.error(e.toString());
			} finally {
				try {
					if (connection1 != null) {
						resultSet1.close();

						statement1.close();
						connection1.close();
					}
				} catch (SQLException ex) {
					logger.error(ex.toString());
				}
			}

			boolean found = false;
			for (String a : teamsSameLeague) {
				for (String ab : a.split(" ")) {
					if (postDescription.contains(ab)) {
						maybes.put(key, value + 20); //add 20 points if part of a team from the same league is found
						logger.info("[" + key + "] team in same league fragment is [" + ab + "]");
						found = true;
						break;
					}
				}
				if (found) {
					break;
				}
			}
			if (!found) {
				maybes.put(key, value - 40); //subtract 40 if no team from same league is found
				logger.info("40 deducted from [" + key + "] because no team from same league found");
				//this is fine if it's an international game because it will subtract 20 points from ALL maybes
			}
		}
		////
		//////////////// remove 10 points if player's name is within parens
		//		for (HashMap.Entry<String, Integer> entry : maybes.entrySet()) {
		//			String key = entry.getKey();
		//			Integer value = entry.getValue();
		//
		//			if (postDescription.charAt(postDescription.indexOf(key) - 1) == '(' && (postDescription.charAt(postDescription.indexOf(key) + key.length()) == ')')) {
		//				maybes.put(key, value - 10); //remove 10 points if key is surrounded by parens (usually means team name instead of player name)
		//			}
		//
		//		}
		//////////////////////////////////////////////////////
		// deduct 10 points if within 2 words of a "vs"
		for (HashMap.Entry<String, Integer> entry : maybes.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();

			int vsLoc = 0;
			ArrayList<Integer> pLoc = new ArrayList<Integer>();

			String[] b = postDescription.split(" ");
			for (int i=0; i<b.length - 1; i++) {
				if (b[i].equals("vs")) {
					System.out.println("vs found at: " + i);
					vsLoc = i;
				} 
				if (b[i].equals(key.split(" ")[0])) {	
					if (key.split(" ").length == 1) {
						pLoc.add(i);
					} else if (key.split(" ").length > 1) {
						if (b[i+1].equals(key.split(" ")[1])) {
							pLoc.add(i);
						}
					}
				}
			}

			for (int l : pLoc) {
				if (Math.abs(l - vsLoc) < 3) {
					maybes.put(key, value - 10); //remove 10 points if key is within 2 words of a vs (only teams should be that close)
					logger.info("10 points removed from [" + key + "] because within 2 words of a 'vs'");
					break;
				}
			}
		}
		// if any name found is a subset of another name found, reduce 30 pts from smaller
		String[] keys = maybes.keySet().toArray(new String[maybes.size()]);
		for (int i = 0; i < keys.length; i++) {
			for (int j = 0; j<keys.length; j++) {
				if (i != j) {
					if (keys[j].contains(keys[i])) {
						maybes.put(keys[i], maybes.get(keys[i]) - 30);
						logger.info("30 points deducted from [" + keys[i] + "] because it is contained in [" + keys[j] + "]");
					}
				}
			}
		}
		////////////////////////////////////////////// add 20 points if similar last name is found
		for (HashMap.Entry<String, Integer> entry : maybes.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();

			for (String s: postDescription.split(" ")) {
				if (Similar.similarity(s, key.split(" ")[key.split(" ").length-1]) >= .9) {
					maybes.put(key, value + 20); //add 20 points if last name is contained in PD (similar, at least)
				}
			}
		}
		////////////////////////////////////////////////////// if postDescription contains the last name with special chars add 30
		for (HashMap.Entry<String, Integer> entry : maybes.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();

			String[] name = key.split(" ");
			if (name.length == 2) {
				if (postDescription.contains(name[1]) && !postDescription.contains(Simplify.simplifyName(name[1]))) {
					maybes.put(key, value + 30);
				}
			}
		}
		//////////////////////////////////////////////////////////

		for (HashMap.Entry<String, Integer> entry : maybes.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();

			logger.info("["+key+"] : ["+value+"]");
			if (value >= maxPoints) {
				maxPlayer = key;
				maxPoints = value;
			}
		}

		sToReturn[0] = maxPlayer;
		sToReturn[1] = Integer.toString(maxPoints);
		return sToReturn;
	}

	//keep going until all instances of any name are found - then select the first one and return it
	public static String parseKeywords(String postDescription, String url) { 

		String[] namePoints = findKeyword(postDescription);
		String minName = namePoints[0];
		String minPoints = namePoints[1];
		logger.info("first name found was [" + minName + "]");
		tweetTweet(minName, postDescription, url, Integer.parseInt(minPoints));

		return minName; //i.e no player found in the csv
	}

	public static void tweetTweet(String minName, String postDescription, String url, int score) {

		postDescription = postDescription.replaceAll("([A-Z])\\.(\\s\\w)", "$1$2"); //M. Reus -> M Reus
		if ((postDescription.charAt(0) == 'M' || postDescription.charAt(0) == 'D') && postDescription.charAt(1) == ' ') {
			postDescription = postDescription.substring(2); //M Reus - > Reus
		}

		String countryName = "";

		if (isInvalidTQ(postDescription, minName)) {
			//do nothing
		} else {

			try {

				String playerHashtag = "";
				String teamHashtag = "";
				String countryHashtag = "";
				String leagueHashtag = "";

				if (minName.equals("no-player-found")){
					logger.info("no player found so posting without hashtags");
				} else if (postDescription.toLowerCase().contains("own goal") || postDescription.contains("OG") || score < 55) {
					//keep hashtags as blanks
					logger.info("Didn't tweet because own goal or score below threshold. Score was [" + score + "]");
				} else {

					//------------------------------------------------------//
					String[] fullN = minName.split(" ");
					if (fullN.length == 1) {
						playerHashtag = fullN[0];
					} else if (fullN.length == 2) {
						playerHashtag = fullN[1];
					} else {
						playerHashtag = fullN[fullN.length - 2] + fullN[fullN.length - 1];
					}
					playerHashtag = Simplify.simplifyName(playerHashtag.replaceAll("\\s|[-]|[!]|[$]|[%]|[\\^]|[&]|[\\*]|[\\+]|[']",""));
					//------------------------------------------------------//
					String teamName = "";
					if (playerTeams.containsKey(minName.trim())) {
						teamName = playerTeams.get(minName.trim()); //'Manchester City'
					}
					String[] teamParts = teamName.split(" ");
					if (teamParts.length == 1) {
						teamHashtag = teamParts[0];
					} else {
						for (int i=0; i<teamParts.length;i++) {
							if (!teamParts[i].equals(teamParts[i].toUpperCase())) {
								teamHashtag += teamParts[i];
							}
						}
					}
					teamHashtag = Simplify.simplifyName(teamHashtag.replaceAll("\\s|[-]|[!]|[$]|[%]|[\\^]|[&]|[\\*]|[\\+]|[']|\\d","")).replace("FootballClub", "FC").replace("SoccerClub", "").replaceAll("([a-zA-Z])de([A-Z])", "$1$2");
					//------------------------------------------------------//
					String leagueName = "";
					if (playerLeague.containsKey(minName.trim())) {
						leagueName = playerLeague.get(minName.trim()); //'Manchester City'
					}
					//special cases
					if (leagueName.trim().equals("Premier League")) {
						leagueHashtag = "EPL";
					} else if (leagueName.trim().equals("LaLiga Santander")) {
						leagueHashtag = "LaLiga";
					} else if (leagueName.trim().equals("Major League Soccer")) {
						leagueHashtag = "MLS";
					} else if (leagueName.trim().equals("Ligue 1")) {
						leagueHashtag = "Ligue1";
					} else if (leagueName.trim().equals("LaLiga 1 I 2 I 3")) {
						leagueHashtag = "LaLiga";
					} else if (leagueName.trim().equals("Bundesliga 2")) {
						leagueHashtag = "Bundesliga2";
					} else if (leagueName.trim().equals("Sogaz Russian Football Championship")) {
						leagueHashtag = "RussianPremierLeague";
					} else if (leagueName.trim().equals("Österreichische Fußball-Bundesliga")) {
						leagueHashtag = "AustrianFootballBundesliga";
					} else if (leagueName.trim().equals("Campeonato Brasileiro Série A")) {
						leagueHashtag = "Brasileiro";
					} else if (leagueName.trim().equals("Calcio A")) {
						leagueHashtag = "SerieA";
					} else if (leagueName.trim().equals("Alka Superliga")) {
						leagueHashtag = "Superliga";
					} else if (leagueName.trim().equals("Belgium Pro League")) {
						leagueHashtag = "ProLeague";
					} else if (leagueName.trim().equals("Calcio B")) {
						leagueHashtag = "SerieB";
					} else if (leagueName.trim().equals("Campeonato Scotiabank")) {
						leagueHashtag = "PrimeraDivision";
					} else if (leagueName.trim().equals("Dawry Jameel")) {
						leagueHashtag = "JameelLeague";
					} else if (leagueName.trim().equals("Domino's Ligue 2")) {
						leagueHashtag = "Ligue2";
					} else if (leagueName.trim().equals("EFL Championship")) {
						leagueHashtag = "Championship";
					} else if (leagueName.trim().equals("EFL League One")) {
						leagueHashtag = "LeagueOne";
					} else if (leagueName.trim().equals("EFL League Two")) {
						leagueHashtag = "LeagueTwo";
					} else if (leagueName.trim().equals("Hyundai A-League")) {
						leagueHashtag = "ALeague";
					} else if (leagueName.trim().equals("K LEAGUE Classic")) {
						leagueHashtag = "KLeague";
					} else if (leagueName.trim().equals("LIGA Bancomer MX")) {
						leagueHashtag = "LigaMX";
					} else if (leagueName.trim().equals("Liga Dimayor")) {
						leagueHashtag = "LigaAguila";
					} else if (leagueName.trim().equals("Meiji Yasuda J1 League")) {
						leagueHashtag = "J1League";
					} else if (leagueName.trim().equals("Primera División")) {
						leagueHashtag = "PrimeraDivision";
					} else if (leagueName.trim().equals("Raiffeisen Super League")) {
						leagueHashtag = "SuperLeague";
					} else if (leagueName.trim().equals("Scottish Premiership")) {
						leagueHashtag = "ScottishPremiership";
					} else if (leagueName.trim().equals("South African FL")) {
						leagueHashtag = "SAFL";
					} else if (leagueName.trim().equals("SSE Airtricity League")) {
						leagueHashtag = "AirtricityLeague";
					} else if (leagueName.trim().equals("Süper Lig")) {
						leagueHashtag = "SuperLig";
					} else if (leagueName.trim().equals("Ukrayina Liha")) {
						leagueHashtag = "UkrayinaLiha";
					} else if (leagueName.trim().equals("Domino’s Ligue 2")) {
						leagueHashtag = "Ligue2";
					} else {
						//						String[] leagueParts = leagueName.split(" ");
						//						if (leagueParts.length == 1) {
						//							leagueHashtag = leagueParts[0];
						//						} else {
						//							for (int i=0; i<leagueParts.length;i++) {
						//								if (!leagueParts[i].equals(leagueParts[i].toUpperCase())) {
						//									leagueHashtag += leagueParts[i];
						//								}
						//							}
						//						}
						if (leagueHashtag.equals("EPL")) {
							leagueHashtag = "EPL #PremierLeague";
						} else {
							leagueHashtag = Simplify.simplifyName(leagueHashtag.replaceAll("\\s|[-]|[!]|[$]|[%]|[\\^]|[&]|[\\*]|[\\+]|[']|\\d","")).replaceAll("PremierLeague", "PL");
						}
					}
					logger.info("League hashtag is [" + leagueHashtag + "]");
					//------------------------------------------------------//
					if (playerCountry.containsKey(minName.trim())) {
						countryName = playerCountry.get(minName.trim()); //'Germany'
					}
					String[] countryParts = countryName.split(" ");
					for (int i=0; i<countryParts.length;i++) {
						countryHashtag += countryParts[i];
					}

					countryHashtag = Simplify.simplifyName(countryHashtag.replaceAll("\\s|[-]|[!]|[$]|[%]|[\\^]|[&]|[\\*]|[\\+]|[']",""));
				}
				String altHashtag = "";
				if (postDescription.contains(countryName)) {
					altHashtag = countryHashtag;
				} else {
					altHashtag = leagueHashtag;
				}
				//------------------------------------------------------//
				int maxPostLength = 140 - url.length() - altHashtag.length() - teamHashtag.length() - playerHashtag.length() - 6 - 1; //140 max twitter, 3 is 1x" | ", second 3 is ..., 1 is off by 1 error below due to whitespace added

				String ellipsePost = "";
				String[] postParts = postDescription.split(" ");
				if (postDescription.length() < maxPostLength) {
					ellipsePost = postDescription; //dont add ellipses if post is already short enough to fit
				} else {
					for (int i=0; i<postParts.length;i++) {
						if (ellipsePost.length() + postParts[i].length() < maxPostLength) {
							ellipsePost += postParts[i] + " ";
						} else {
							System.out.println(ellipsePost);
							break;
						}
					}
					ellipsePost = ellipsePost.trim();
					ellipsePost += "...";
				}

				String stat = ellipsePost + " | " + url;

				if (!playerHashtag.equals("") && (stat.length() + playerHashtag.length() + 2 <= 140)) {
					stat += " #" + Simplify.simplifyName(playerHashtag.replaceAll("\\s|[-]|[!]|[$]|[%]|[\\^]|[&]|[\\*]|[\\+]|[']",""));
				}

				if (!teamHashtag.equals("") && (stat.length() + teamHashtag.length() + 2 <= 140)) {
					stat += " #" + Simplify.simplifyName(teamHashtag.replaceAll("\\s|[-]|[!]|[$]|[%]|[\\^]|[&]|[\\*]|[\\+]|[']|\\d",""));
				}
				if (!altHashtag.equals("") && (stat.length() + altHashtag.length() + 2 <= 140)) {
					stat += " #" + Simplify.simplifyName(altHashtag.replaceAll("\\s|[-]|[!]|[$]|[%]|[\\^]|[&]|[\\*]|[\\+]|[']",""));
				}

				//				if (url.endsWith(".mp4")) {
				//					VideoUpload vu = new VideoUpload();
				//					try {
				//						vu.tweetTweetWithVideo(url, stat);
				//					} catch (IOException e) {
				//						e.printStackTrace();
				//					} catch (InterruptedException e) {
				//						e.printStackTrace();
				//					} catch (JSONException e) {
				//						e.printStackTrace();
				//					}
				//				} else if (url.contains("streamable")) {
				//					String newURL = getStreamableURL(url);
				//					VideoUpload vu = new VideoUpload();
				//					try {
				//						vu.tweetTweetWithVideo(newURL, stat);
				//					} catch (IOException e) {
				//						e.printStackTrace();
				//					} catch (InterruptedException e) {
				//						e.printStackTrace();
				//					} catch (JSONException e) {
				//						e.printStackTrace();
				//					}
				//				} else {
				if (stat.length() < 140) {
					// The factory instance is re-usable and thread safe.
					Twitter twitter = TwitterFactory.getSingleton();
					Status status = null;
					if (!redditenv.equals("test")) {
						status = twitter.updateStatus(stat);
						logger.info("Posted to twitter and successfully updated the status to [" + status.getText() + "].");
					} else {
						logger.info("TEST ENV: Would have posted to twitter with [" + stat + "].");
					}
				} else {
					logger.info("Didn't post to twitter because length was greater than 140. Length was [" + stat.length() + "]");//else do nothing
					logger.info(stat);

				}
				//}
			} catch (TwitterException e) {
				logger.error(e.toString());
			}
		}

	}

	public static ArrayList<String> findSubscribedUsers(String keyword, String postDescription) { 

		ArrayList<String> subscribedUsers = new ArrayList<String>();

		if (!keyword.equals("no-player-found")) {

			Connection connection = null;
			ResultSet resultSet = null;
			Statement statement = null;

			try {
				String url = "jdbc:sqlite:../server/db/user.db";
				connection = DriverManager.getConnection(url);
				long currentTime = System.nanoTime();
				//keyword = keyword.replace("'", "''");
				String sql = "Select Email from User WHERE Keywords like '%" + keyword.replaceAll("'", "''") + "%' and ReceiveEmails<" + currentTime + " and ReceiveEmails>0;";
				logger.info("SQL: " + sql);
				statement = connection.createStatement();
				resultSet = statement.executeQuery(sql);
				//connection successful if error not caught below

				while(resultSet.next()){
					subscribedUsers.add(resultSet.getString("Email"));
				}

				return subscribedUsers;

			} catch (SQLException e) {
				logger.error(e.toString());
			} finally {
				try {
					if (connection != null) {
						resultSet.close();
						statement.close();
						connection.close();
					}
				} catch (SQLException ex) {
					logger.error(ex.toString());
				}
			}
		}

		return subscribedUsers;
	}

	public static boolean isInvalidTQ(String postDescription, String keyword) {

		Connection tqConnection = null;
		Statement tqStatement = null;
		ResultSet tqResultSet = null;

		try {

			Pattern p = Pattern.compile("\\D[\\[|(]?[0-9][\\]|)]?-[\\[|(]?[0-9][\\]|)]?\\D"); 
			Matcher m = p.matcher(postDescription);
			String score = "0-0";

			if (m.find()) { 
				score = postDescription.substring(m.start(), m.end()).replaceAll("\\(|\\)|\\[|\\]|\\{|\\}", "").trim();
			}

			String tqUrl = "jdbc:sqlite:../server/db/timeq.db";
			tqConnection = DriverManager.getConnection(tqUrl);
			String sqlTQ = "Select * from Timeq WHERE Player='" + keyword.replaceAll("'", "''") + "' and Score='" + score + "';";
			logger.info("SQL time queue: " + sqlTQ);
			tqStatement = tqConnection.createStatement();
			tqResultSet = tqStatement.executeQuery(sqlTQ);
			boolean tqFilled = tqResultSet.next();

			logger.info("Already in TQ? (i.e. already found a highlight for this play today): [" + tqFilled + "]");

			return tqFilled;

		} catch (SQLException e) {
			logger.error(e.toString());
		} finally {
			try {
				if (tqConnection != null) {
					tqResultSet.close();
					tqStatement.close();
					tqConnection.close();
				}
			} catch (SQLException ex) {
				logger.error(ex.toString());
			}
		}
		return false; //exit with error
	}

	public static void sendEmail(String link, String keyword, ArrayList<String> emailAddresses, String postDescription) {

		for (String em : emailAddresses) {

			if (!redditenv.equals("test")) {

				String subject = "PMR Highlight Found - " + keyword;
				String content = "Goal by " + keyword + "!" + " View " + link + ".\n\n\nIf this wasn't the correct player you selected, it's easiest just to uncheck that player"
						+ " within the website - we're working on a solution to improve our app's cognitive ability. If you notice any other bugs feel free to send me an email personally at jonathan.axmann09@gmail.com";
				logger.info("Attempting to email: [" + em + "]...");
				GmailService.send(service.getService(), em, "pmridontcareifyourespond@gmail.com", subject, content); 
				logger.info("Email sent to [" + em + "] successfully - starting insert into time queue...");

			}

		}
	}

	public static void addToTimeq(String keyword, String postDescription) {

		if (!keyword.equals("no-player-found")) {
			try {

				Connection connection = null;
				PreparedStatement preparedStatement = null;

				try {
					String url = "jdbc:sqlite:../server/db/timeq.db";
					connection = DriverManager.getConnection(url);
					//keyword = keyword.replace("'", "''"); //dont need to do this for insertions i think

					Pattern p = Pattern.compile("[\\[|(]?[0-9][\\]|)]?-[\\[|(]?[0-9][\\]|)]?"); 
					Matcher m = p.matcher(postDescription);
					String score = "0-0";

					if (m.find()) { 
						score = postDescription.substring(m.start(), m.end()).replaceAll("\\(|\\)|\\[|\\]|\\{|\\}", "");
					}

					String sql = "INSERT INTO Timeq(Player, Score)"
							+ " VALUES(?,?)";
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, keyword);
					preparedStatement.setString(2, score);
					preparedStatement.executeUpdate(); 
					logger.info("TQ insertion: [" + keyword + "],[" + score + "]");

				} catch (SQLException e) {
					logger.error(e.toString());
				} finally {
					try {
						if (connection != null) {
							preparedStatement.close();
							connection.close();
						}
					} catch (SQLException ex) {
						logger.error(ex.toString());
					}
				}
			} catch (Exception ex) {
				logger.error(ex.toString());
			}
		}
	}

	public static int getSleepTime() {
		Calendar calendar = Calendar.getInstance();
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int msWait = 60000;

		if (!redditenv.equals("test")) { //ie prod
			if (hours >= 4 && hours <= 9) { //12am to 5am EST
				msWait = 3600000 * (9 - hours); // milliseconds remaining until 5 am
				return msWait;
			} else {
				return msWait;
			}
		} else {
			return 60000;
		}

	}

	public static HashMap<String, String> populatePlayerTeams() {
		HashMap<String, String> playerTeams = new HashMap<String, String>();

		try {
			BufferedReader reader = new BufferedReader (new FileReader("regenerate-players//fullTable.csv")); 
			String line;
			logger.info("Loading playerTeam HashMap...");

			while ((line = reader.readLine()) != null) {

				String[] s = line.split(",");

				byte pplayer[] = s[2].trim().getBytes(UTF_8);
				String newplayer = new String(pplayer, UTF_8);
				byte pteam[] = s[1].trim().getBytes(UTF_8);
				String newteam = new String(pteam, UTF_8);

				playerTeams.put(newplayer, newteam);

			}

			logger.info("playerTeam HashMap loaded.");
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return playerTeams;
	}


	public static HashMap<String, String> populatePlayerCountry() {

		HashMap<String, String> playerCountry = new HashMap<String, String>();

		try {
			BufferedReader reader = new BufferedReader (new FileReader("regenerate-players//fullTable.csv")); 
			String line;
			logger.info("Loading playerCountry HashMap...");


			while ((line = reader.readLine()) != null) {

				String[] s = line.split(",");

				byte pplayer[] = s[2].trim().getBytes(UTF_8);
				String newplayer = new String(pplayer, UTF_8);
				byte pteam[] = s[3].trim().getBytes(UTF_8);
				String newcountry = new String(pteam, UTF_8);

				playerCountry.put(newplayer, newcountry);


			}
			logger.info("playerCountry HashMap loaded.");

			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return playerCountry;
	}	

	public static HashMap<String, String> populatePlayerLeague() {

		HashMap<String, String> playerLeague = new HashMap<String, String>();

		try {
			BufferedReader reader = new BufferedReader (new FileReader("regenerate-players//fullTable.csv")); 
			String line;
			logger.info("Loading playerLeague HashMap...");


			while ((line = reader.readLine()) != null) {

				String[] s = line.split(",");

				byte pplayer[] = s[2].trim().getBytes(UTF_8);
				String newplayer = new String(pplayer, UTF_8);
				byte pteam[] = s[0].trim().getBytes(UTF_8);
				String newleague = new String(pteam, UTF_8);

				playerLeague.put(newplayer, newleague);


			}
			logger.info("playerLeague HashMap loaded.");

			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return playerLeague;
	}

	public static HashSet<String> loadPlayers() {

		HashSet<String> playerMatches = new HashSet<String>();
		logger.info("Loading player HashSet...");

		try {
			BufferedReader reader = new BufferedReader (new FileReader("regenerate-players//synsTable.csv")); //backup version of this is "list-of-players2.csv"
			String line;

			while ((line = reader.readLine()) != null) {

				byte pplayer[] = line.getBytes(UTF_8);
				String newplayer = new String(pplayer, UTF_8);

				playerMatches.add(newplayer);

			}

			logger.info("player HashSet loaded.");
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return playerMatches;
	}

	public static String getStreamableURL(String urlWithStreamable) {

		String streamableID = urlWithStreamable.replaceAll(".*/", "");

		URL yahoo = null;
		try {
			yahoo = new URL("https://api.streamable.com/videos/" + streamableID);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		URLConnection yc = null;
		try {
			yc = yahoo.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedReader in = null;
		try {
			in = new BufferedReader(
					new InputStreamReader(
							yc.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		String inputLine = null;
		String output = "";
		try {
			while ((inputLine = in.readLine()) != null) {
				output = inputLine;
			}
			System.out.println(output);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JsonObject jsonObj = new JsonParser().parse(output).getAsJsonObject();
		String url = jsonObj.get("files").getAsJsonObject().get("mp4").getAsJsonObject().get("url").getAsString();

		String fullUrl = "https:"+url;

		return fullUrl;

	}

}