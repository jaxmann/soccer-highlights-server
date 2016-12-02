import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class main {

	public static final boolean DEBUG = true;
	public static ArrayList<User> users = new ArrayList<User>();
	public static ArrayList<String> gcdTeams = new ArrayList<String>();
	public static ArrayList<String> gcdPlayers = new ArrayList<String>();
	public static ArrayList<String> fullTeams = new ArrayList<String>();
	public static ArrayList<String> fullPlayers = new ArrayList<String>();


	public static void main(String[] args) throws MalformedURLException, IOException {
		//debug flag for print statements

		//set up favorite teams/players - players and teams you want to track

		//parse play - figure out what happened - who scored, what team, what the score is, the date?, what sport, set time as NOW

		//identify a search query based on the play/identify keywords used to search, make sure post is recent (PMR)

		//perform search/crawl on websites, save url, verify its a smallish video file

		//send a link (create an alert at first)

		
		//////////////////////////////////////////////////////////////////
		//after picking teams, etc
		//do big db call to populate teams/players arraylist
		//play alert is received from api
		
		//possible to approximate file size before download
		HttpURLConnection content = (HttpURLConnection) new URL("www.example.com").openConnection();
		System.out.println(content.getContentLength());
		
		String playAlert = "Goal by Ronaldo! Marcelo crosses from left side and Ronaldo heads it home past Ter Stegen!";
		if (isPlayWorthKeeping(playAlert)) {
			parsePlay(playAlert);
		}		
		//determine if play is worth examining -- does it contain "goal"?
		//if yes, parse play for keywords/query, set time, etc
		//create a new play
		//create a thread for each play, wait 10, then run each minute until 40 ----- crawlSites + thread
		//each thread validates and checks where to send url, and sends it
		///////////////////////////////////////////////////////////////////

	}


	public static boolean isPlayWorthKeeping(String playAlert) {
		if (playAlert.toLowerCase().contains("goal")) {
			return true;
		} 
		return false;
	}


	public static void parsePlay(String play) {
		Date date = new Date(); 

		ArrayList<String> keywords = new ArrayList<String>();
		String score = null;

		String[] playArr = play.split(" ");
		for (int i=0; i<playArr.length; i++) {
			//teams
			for (int j=0; j<gcdTeams.size(); j++) {
				if (playArr[i].contains(gcdTeams.get(j))) {
					keywords.add(fullTeams.get(j));
				}
			}
			//players
			for (int k=0; k<gcdPlayers.size(); k++) {
				if (playArr[i].contains(gcdPlayers.get(k))) {
					keywords.add(fullPlayers.get(k));
				}
			}
			if (playArr[i].matches("\\(\\s?\\d{1}\\s?\\-\\s?\\d{1}\\s?\\)")) {
				score = regexMatchString(playArr[i], "\\(\\s?\\d{1}\\s?\\-\\s?\\d{1}\\s?\\)");
			}
		}
		
		
		Play p = new Play(date, keywords, date, score);
		//for each play p, start a thread
		//add a new worker thread (newWorkerThread)
		newWorkerThread(p);

	}

	public static String regexMatchString(String text, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		String matchingString = text.substring(matcher.start(), matcher.end());
		return matchingString;

	}

	public static void newWorkerThread(Play p) {
		//create one at a time
		CrawlerThread t = new CrawlerThread(p);
		t.run();
	}


	public String pickTeam(String team) {
		//connect to a db and validate their input (checking permutations of what they said, e.g. "dortmund" = "Borussia Dortmund") - like watson..
		//for now...
		String teamPicked = "Borussia Dortmund";
		return teamPicked;
		//this will be different from "add team", etc, which will also check if the team already exists, etc
	}

	public String pickPlayer(String player, String team) {
		//prompt for team first, then prompt for player
		//validate their player against the players full/last name in db, for each player associated with every team :/
		String playerPicked = "Marco Reus";
		return playerPicked;
	}

}
