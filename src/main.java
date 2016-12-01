import java.util.ArrayList;
import java.util.Date;

import javax.swing.JOptionPane;

public class main {
	
	boolean debug = false;

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
		//determine if play is worth examining -- does it contain "goal"?
		//if yes, parse play for keywords/query, set time, etc
		//create a new play
		//create a thread for each play, wait 10, then run each minute until 40 ----- crawlSites + thread
		//each thread valides and checks where to send url, and sends it
		///////////////////////////////////////////////////////////////////
		
	}
	
	public boolean isPlayWorthKeeping(String playAlert) {
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
	
		
	
	public void parsePlay(String play) {
		String play1 = "Goal by Ronaldo! Marcelo crosses from left side and Ronaldo heads it home past Ter Stegen!";
		Date date = new Date(); //dot getNow
		
		ArrayList keywords = new ArrayList<String>();
		String[] playArr = play1.split(" ");
		for (int i=0; i<playArr.length; i++) {
			keywords.add(playArr[i]);
		}
		
		//find real tags ---- involves checking db if any known players are involved in the play string
		
		Play p = new Play(date, keywords, date, keywords);
		//for each play p, start a thread
		
		//pass on to "crawlSites" method
		
		
	}
	
	public void crawlSites(String fullQuery, ArrayList keywords) {
		//fullQuery is all combined keywords with overall best chance of success?
		//OR see if any one video contains MOST of the keywords, and pick that video?
		
		
		
		//pass it on to "figureOutWhoToSendItTo" method
	}
	
	public void figureOutWhoToSendItTo(String keywords) {
		//given the associated keywords with the video, see if they match "tags" that one can subscribe to (either team name/players)
		//and only send the message to people who are subscribed
		//pass on to "sendAlert" method
	}
	
	public void sendAlert(String url) {
		//this would be an email or push notification or something, for now, just an alert/pop-up
		JOptionPane.showMessageDialog(null, "MarcoReusIsAnAnimal.mp4");
	}
	
	
	
	


}
