import java.util.ArrayList;

import javax.swing.JOptionPane;

public class User {
	
	private String email;
	private int userID;
	private ArrayList<String> teams;
	private ArrayList<String> players;
	
	public User(String email, int userID, ArrayList<String> teams, ArrayList<String> players) {
		this.email = email;
		this.userID = userID;
		this.teams = teams;
		this.players = players;
	}
	
	public void sendAlert(String url) {
		//this would be an email or push notification or something, for now, just an alert/pop-up
		JOptionPane.showMessageDialog(null, "send MarcoReusIsAnAnimal.mp4 to user " + this.userID + " + at email " + this.email);
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public ArrayList<String> getTeams() {
		return teams;
	}

	public void setTeams(ArrayList<String> teams) {
		this.teams = teams;
	}

	public ArrayList<String> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<String> players) {
		this.players = players;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	

}
