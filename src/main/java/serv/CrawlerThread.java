package serv;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class CrawlerThread implements Runnable {

	public static final String USER_AGENT = "User-Agent: desktop:PMR:v0.0.5 (by /u/pmrtest)"; //Required by reddit to be able to crawl their site
	public static Logger logger = Logger.getLogger(CrawlerThread.class);
	private static GmailService service;
	private static String redditenv;
	public static HashMap<String, String> playerTeams;
	public static HashMap<String, String> playerCountry;
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

		Pattern p = Pattern.compile("[\\[|(]?[0-9][\\]|)]?-[\\[|(]?[0-9][\\]|)]?"); //does the link text have something like (2-0) displaying the score of a game ^[0-9]+(-[0-9]+)

		playerTeams = populatePlayerTeams(); //list of players with team names associated
		playerCountry = populatePlayerCountry(); //list of players with country names associated
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

								if (url.contains(".mp4") || url.contains("streamable")) { //only trigger if it's a video link

									logger.info("New post found: [" + title + "] at [" + time + "]");

									keyword = parseKeywords(title, url); //identify player keywords within play description
									logger.info("Keyword is: [" + keyword + "]");
									subbedUsers = findSubscribedUsers(keyword, title);
									logger.info("Number of subbed users: [" + subbedUsers.size() + "]");
									if (subbedUsers.size() != 0 && !redditenv.equals("test")) { //if no users are subscribed to a particular player, don't try to send email (it will fail)
										sendEmail(url, keyword, subbedUsers, title); //send email to users who match keywords - send them the url, use keyword in email title/body; user's email is returned from sql query
									} else if (redditenv.equals("test")) {
										addToTimeq(keyword, title, "thiswill@never.happen");
									}

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

	public static String findKeyword(String postDescription) {
		//could do postDescription = simplify.simplifyName(postDescription) here
		HashMap<String, Integer> playersFound = new HashMap<String, Integer>();
		HashMap<String, Integer> maybes = new HashMap<String, Integer>();

		for (String line : playerMatches) {

			//byte ptext[] = line.getBytes(ISO_8859_1);
			//String newline = new String(ptext, UTF_8);

			String[] s = line.split(",");
//			if (line.contains("zil")) {
//				logger.info(line);
//			}
			for (String player : s) {

				// find player starting at start of string or after a whitespace with trailing whitespace, apostrophe, or line boundary
				String reg = "((^|\\s|\\()" + player + "(\\)|'|\\s|$))|((^|\\s|\\()" + simplify.simplifyName(player) + "(\\)|'|\\s|$))";
				
				
				Pattern p = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(postDescription);

				if (m.find()) {
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
							maybes.put(player, 120); //full name found
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
		}

		//if outright first choice from above, assign 100 points, if it wasn't the first name found or a snippet was found, assign 80
		///////////////////////////
		for (HashMap.Entry<String, Integer> entry : playersFound.entrySet()) {
			String key = entry.getKey();

			if (!key.equals(minName) && (!maybes.containsKey(key))) {
				maybes.put(key, 80); //partial/syn name found inside snippet
			} else if (key.equals(minName)) { //overwrite if already set as 100
				maybes.put(key, 90); //full name found and is minName (closest to front)
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
						maybes.put(key, value + 60); //if entire team is contained in snippet
						logger.info("Team treated as [" + tm + "] for player [" + key + "]");
					} else if (postDescription.contains(tmSplit[i]) || postDescription.contains(simplify.simplifyName(tmSplit[i]))) {
						maybes.put(key, value + 40); //add 15 points for each part of a team that is contained
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
					logger.info("Country treated as [" + tm + "] for player [" + key + "]");
				}
			}

		}
		//////////////////////////////////////////////////////
		for (HashMap.Entry<String, Integer> entry : maybes.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();

			logger.info("["+key+"] : ["+value+"]");
			if (value > maxPoints) {
				maxPlayer = key;
				maxPoints = value;
			}
		}
		return maxPlayer;
	}

	//keep going until all instances of any name are found - then select the first one and return it
	public static String parseKeywords(String postDescription, String url) { 

		String minName = findKeyword(postDescription);
		logger.info("first name found was [" + minName + "]");
		tweetTweet(minName, postDescription, url);

		return minName; //i.e no player found in the csv
	}

	public static void tweetTweet(String minName, String postDescription, String url) {
		if (!redditenv.equals("test")) {
			if (!minName.equals("no-player-found")) {
				// The factory instance is re-usable and thread safe.
				Twitter twitter = TwitterFactory.getSingleton();
				Status status = null;
				postDescription = postDescription.replaceAll("([A-Z])\\.(\\s\\w)", "$1$2"); //M. Reus -> M Reus
				if ((postDescription.charAt(0) == 'M' || postDescription.charAt(0) == 'D') && postDescription.charAt(1) == ' ') {
					postDescription = postDescription.substring(2); //M Reus - > Reus
				}
				try {
					String stat = postDescription + " | " + url + " #" + minName.replaceAll("\\s|[-]|[!]|[$]|[%]|[\\^]|[&]|[\\*]|[\\+]","");
					if (stat.length() < 140) {
						status = twitter.updateStatus(stat);
						logger.info("Posted to twitter and successfully updated the status to [" + status.getText() + "].");
					} //else do nothing
				} catch (TwitterException e) {
					logger.error(e.toString());
				}
			}
		}
	}

	public static ArrayList<String> findSubscribedUsers(String keyword, String postDescription) { 

		ArrayList<String> subscribedUsers = new ArrayList<String>();

		if (!keyword.equals("no-player-found")) {

			Connection connection = null;
			Connection tqConnection = null;
			ResultSet resultSet = null;
			ResultSet tqResultSet = null;
			Statement statement = null;
			Statement tqStatement = null;
			try {
				String url = "jdbc:sqlite:../server/db/user.db";
				connection = DriverManager.getConnection(url);
				long currentTime = System.nanoTime();
				keyword = keyword.replace("'", "''");
				String sql = "Select Email from User WHERE Keywords like '%" + keyword + "%' and ReceiveEmails<" + currentTime + " and ReceiveEmails>0;";
				logger.info("SQL: " + sql);
				statement = connection.createStatement();
				resultSet = statement.executeQuery(sql);
				//connection successful if error not caught below

				//iterate over results
				if(resultSet.next()) {

					try {
						
						Pattern p = Pattern.compile("[\\[|(]?[0-9][\\]|)]?-[\\[|(]?[0-9][\\]|)]?"); 
						Matcher m = p.matcher(postDescription);
						String score = "0-0";
						
						if (m.find()) { 
							score = postDescription.substring(m.start(), m.end()).replaceAll("\\(|\\)|\\[|\\]|\\{|\\}", "");
						}

						String tqUrl = "jdbc:sqlite:../server/db/timeq.db";
						tqConnection = DriverManager.getConnection(tqUrl);
						String sqlTQ = "Select * from Timeq WHERE Email='" + resultSet.getString("Email") + "' and Player='" + keyword + "' and Score='" + score + "';";
						logger.info("SQL time queue: " + sqlTQ);
						tqStatement = tqConnection.createStatement();
						tqResultSet = tqStatement.executeQuery(sqlTQ);
						boolean tqFilled = tqResultSet.next();

						logger.info("Already in TQ? (i.e. email already sent to this user/email for this player+score today): [" + tqFilled + "]");

						if (tqFilled == false) { //if no player exists, add to list and send email
							subscribedUsers.add(resultSet.getString("Email"));
						}

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

	public static void sendEmail(String link, String keyword, ArrayList<String> emailAddresses, String postDescription) {

		for (String em : emailAddresses) {

			if (!redditenv.equals("test")) {

				String subject = "PMR Highlight Found - " + keyword;
				String content = "Goal by " + keyword + "!" + " View " + link + ".\n\n\n If this wasn't the correct player you selected, it's easiest just to uncheck that player"
						+ " within the website - we're working on a solution to improve our app's cognitive ability. If you notice any other bugs feel free to send me an email personally at jonathan.axmann09@gmail.com";
				logger.info("Attempting to email: [" + em + "]...");
				GmailService.send(service.getService(), em, "pmridontcareifyourespond@gmail.com", subject, content); 
				logger.info("Email sent to [" + em + "] successfully - starting insert into time queue...");

			}
			
			addToTimeq(keyword, postDescription, em);

			
		}
	}
	
	public static void addToTimeq(String keyword, String postDescription, String em) {
		
		try {

			Connection connection = null;
			PreparedStatement preparedStatement = null;

			try {
				String url = "jdbc:sqlite:../server/db/timeq.db";
				connection = DriverManager.getConnection(url);
				long currentTime = System.nanoTime();
				keyword = keyword.replace("'", "''");
				
				Pattern p = Pattern.compile("[\\[|(]?[0-9][\\]|)]?-[\\[|(]?[0-9][\\]|)]?"); 
				Matcher m = p.matcher(postDescription);
				String score = "0-0";
				
				if (m.find()) { 
					score = postDescription.substring(m.start(), m.end()).replaceAll("\\(|\\)|\\[|\\]|\\{|\\}", "");
				}
				
				String sql = "INSERT INTO Timeq(Email, Player, Timestamp, Score)"
						+ " VALUES(?,?,?,?)";
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, em);
				preparedStatement.setString(2, keyword);
				preparedStatement.setLong(3, currentTime);
				preparedStatement.setString(4, score);
				preparedStatement.executeUpdate(); 
				logger.info("TQ insertion: [" + em + "], [" + keyword + "],[" + score + "] inserted at [" + currentTime + "]");

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

	public static int getSleepTime() {
		Calendar calendar = Calendar.getInstance();
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int msWait = 60000;
		
		if (!redditenv.equals("test")) { //ie prod
			if (hours >= 22 || hours <= 9) { //7pm to 6am
				if (hours >= 22) {
					msWait = 3600000*( (24-hours) + 9 );
					return msWait; 
				} else {
					msWait = 3600000 * (9 - hours);
					return msWait;
				}
				// milliseconds remaining until 6 am

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

				byte pplayer[] = s[2].trim().getBytes("Windows-1252");
				String newplayer = new String(pplayer, UTF_8);
				byte pteam[] = s[1].trim().getBytes("Windows-1252");
				String newteam = new String(pteam, UTF_8);

				playerTeams.put(newplayer, newteam);

			}

			logger.info("playerTeam HashMap loaded.");
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
			logger.info("Loading playerCountry HashMap...");


			while ((line = reader.readLine()) != null) {

				String[] s = line.split(",");

				byte pplayer[] = s[2].trim().getBytes("Windows-1252");
				String newplayer = new String(pplayer, UTF_8);
				byte pteam[] = s[3].trim().getBytes("Windows-1252");
				String newcountry = new String(pteam, UTF_8);

				playerCountry.put(newplayer, newcountry);


			}
			logger.info("playerCountry HashMap loaded.");

			reader.close();

		} catch (Exception e) {
		}
		return playerCountry;
	}	

	public static HashSet<String> loadPlayers() {

		HashSet<String> playerMatches = new HashSet<String>();
		logger.info("Loading player HashSet...");

		try {
			BufferedReader reader = new BufferedReader (new FileReader("regenerate-players//synsTable.csv")); //backup version of this is "list-of-players2.csv"
			String line;

			while ((line = reader.readLine()) != null) {

				byte pplayer[] = line.getBytes("Windows-1252");
				String newplayer = new String(pplayer, "Windows-1252");
				
				playerMatches.add(newplayer);

			}

			logger.info("player HashSet loaded.");
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return playerMatches;
	}


	
}