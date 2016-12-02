import java.util.ArrayList;
import java.util.Date;

public class Play {

	private ArrayList<String> keywords;
	private Date date;
	private String score;


	Play(Date timeNow, ArrayList<String> keywords, Date date, String score) {
		this.score = score;
		this.keywords = keywords;
		this.date = date;
	}

	public String crawlSites() {
		//see if any one video contains MOST of the keywords, and pick that video?
		//validate url - make sure its a video

		return "sampleUrl";
	}


	public ArrayList<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(ArrayList<String> keywords) {
		this.keywords = keywords;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}



}
