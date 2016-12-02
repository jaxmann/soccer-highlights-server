import java.util.ArrayList;
import java.util.Date;


public class main {
	
	public static final boolean DEBUG = true;
	public static ArrayList<User> users = new ArrayList<User>();


	public static void main(String[] args) {
		

		// TODO Auto-gener
		//debug flag for print statements
		
		//set up favorite teams/players - players and teams you want to track
		
		//parse play - figure out what happened - who scored, what team, what the score is, the date?, what sport, set time as NOW
			
		//identify a search query based on the play/identify keywords used to search, make sure post is recent (PMR)
		
		//perform search/crawl on websites, save url, verify its a smallish video file
		
		//send a link (create an alert at first)
		
		
		
		//////////////////////////////////////////////////////////////////
		//after picking teams, etc
		//play alert is received from api
		String playAlert = "Goal by Ronaldo! Marcelo crosses from left side and Ronaldo heads it home past Ter Stegen!";
		submitPlay(playAlert);
		//determine if play is worth examining -- does it contain "goal"?
		//if yes, parse play for keywords/query, set time, etc
		//create a new play
		//create a thread for each play, wait 10, then run each minute until 40 ----- crawlSites + thread
		//each thread valides and checks where to send url, and sends it
		///////////////////////////////////////////////////////////////////
		
	}
	
	public static void submitPlay(String play) {
		if (isPlayWorthKeeping(play)) {
			parsePlay(play);
		}
	}
	
	public static boolean isPlayWorthKeeping(String playAlert) {
		if (playAlert.toLowerCase().contains("goal")) {
			return true;
		} else {
			return false;
		}
	}
	
	public String pickTeam(String team) {
		//connect to a db and validate their input (checking permutations of what they said, e.g. "dortmund" = "Borussia Dortmund")
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
	
		
	
	public static void parsePlay(String play) {
		Date date = new Date(); //dot getNow
		
		ArrayList<String> keywords = new ArrayList<String>();
		String[] playArr = play.split(" ");
		for (int i=0; i<playArr.length; i++) {
			keywords.add(playArr[i]);
		}
		
		//find real tags ---- involves checking db if any known players are involved in the play string
		
		Play p = new Play(date, keywords, date, keywords);
		//for each play p, start a thread
		//add a new worker thread (newWorkerThread)
		newWorkerThread(p);
		
		
	}
	
	public static void newWorkerThread(Play p) {
		//create one at a time
		CrawlerThread t = new CrawlerThread(p);
		t.run();
	}
	
	
	
	
	
	


}
