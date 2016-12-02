import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
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
		HttpURLConnection content = (HttpURLConnection) new URL("https://docs.oracle.com/javase/7/docs/api/java/lang/Object.html").openConnection();
		//		System.out.println(content.getContentLength());


		//determine if play is worth examining -- does it contain "goal"?
		//if yes, parse play for keywords/query, set time, etc
		//create a new play
		//create a thread for each play, wait 10, then run each minute until 40 ----- crawlSites + thread
		//each thread validates and checks where to send url, and sends it
		///////////////////////////////////////////////////////////////////


		//-----------------FAKE DATA ------------------------------
		String playAlert = "Goal by Reus! Marcelo crosses from left side and Ronaldo heads it home past Ter Stegen!";
		//users
		ArrayList<String> tags1 = new ArrayList<String>();
		tags1.add("dortmund");
		tags1.add("reus");
		ArrayList<String> tags2 = new ArrayList<String>();
		tags2.add("real");
		tags2.add("ronaldo");
		users.add(new User("jonathan.axmann09@gmail.com", 1314940, tags1));
		users.add(new User("k.chesser@tcs.com", 1314943, tags2));
		//gcdTeams
		gcdTeams.add("dortmund");
		gcdTeams.add("real");
		//gcdPlayers
		gcdPlayers.add("reus");
		gcdPlayers.add("ronaldo");
		//fullTeams
		fullTeams.add("Borussia Dortmund");
		fullTeams.add("Real Madrid");
		//fullPlayers
		fullPlayers.add("Marco Reus");
		fullPlayers.add("Christiano Ronaldo");


		if (isPlayWorthKeeping(playAlert)) {
			parsePlay(playAlert);
		}

	}


	public static boolean isPlayWorthKeeping(String playAlert) {
		if (playAlert.toLowerCase().contains("goal")) {
			if (main.DEBUG) {
				System.out.println("isPlayWorthKeeping: TRUE");
			}
			return true;

		} 
		if (main.DEBUG) {
			System.out.println("isPlayWorthKeeping: FALSE");
		}
		return false;
	}


	public static void parsePlay(String play) {
		Date date = new Date(); 

		HashSet<String> shortKeywords = new HashSet<String>();
		HashSet<String> longKeywords = new HashSet<String>();
		int[] score = null;

		String[] playArr = play.split(" ");

		if (main.DEBUG) {
			System.out.println("Play as array is: " + Arrays.toString(playArr));
		}

		for (int i=0; i<playArr.length; i++) {
			//teams
			for (int j=0; j<gcdTeams.size(); j++) {
				if (playArr[i].toLowerCase().contains(gcdTeams.get(j))) {
					shortKeywords.add(gcdTeams.get(j));
					longKeywords.add(fullTeams.get(j));
				}
			}
			//players
			for (int k=0; k<gcdPlayers.size(); k++) {
				if (playArr[i].toLowerCase().contains(gcdPlayers.get(k))) {
					shortKeywords.add(gcdPlayers.get(k));
					longKeywords.add(fullPlayers.get(k));
				}
			}
//			for (String s : keywords) {
//				if (playArr[i].matches("(\\(|\\[)\\s?\\d{1}\\s?\\-\\s?\\d{1}\\s?(\\)|\\])")) {
//					//score - this is an array of 2 numbers
//					score = regexBuildScore(playArr[i], "(\\(|\\[)\\s?\\d{1}\\s?\\-\\s?\\d{1}\\s?(\\)|\\])");
//				}
//			}
		}

		if (main.DEBUG) {
			System.out.println("Score should be 2 ints: " + Arrays.toString(score));
		}


		if (main.DEBUG) {
			if (shortKeywords.size() == 0) {
				System.out.println("Keywords are empty :(");
			} else {
				for (String q : longKeywords) {
					System.out.println("Keyword: " + q);
				}
			}

		}


		Play p = new Play(date, shortKeywords, longKeywords, date, score);
		//for each play p, start a thread
		//add a new worker thread (newWorkerThread)
		newWorkerThread(p);

	}

	public static int[] regexBuildScore(String text, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		String matchingString = text.substring(matcher.start(), matcher.end());
		Scanner s = new Scanner(matchingString);
		int[] scoreArr = {s.nextInt(), s.nextInt()};
		return scoreArr;

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
