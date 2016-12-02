import java.util.ArrayList;

import javax.swing.JOptionPane;

public class User {

	private String email;
	private int userID;
	private ArrayList<String> tags;

	public User(String email, int userID, ArrayList<String> tags) {
		this.email = email;
		this.userID = userID;
		this.tags = tags;
	}

	public void sendAlert(String url) {
		//this would be an email or push notification or something, for now, just an alert/pop-up
		System.out.println("send an email?");
		JOptionPane.showMessageDialog(null, "send MarcoReusIsAnAnimal.mp4 to user " + this.userID + " + at email " + this.email);
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


}
