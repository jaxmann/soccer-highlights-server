import java.util.Date;
import java.util.HashSet;

public class Play {

	private HashSet<String> keywords;
	private Date date;
	private int[] score;


	Play(Date timeNow, HashSet<String> keywords, Date date, int[] score) {
		this.score = score;
		this.keywords = keywords;
		this.date = date;
	}

	public String crawlSites() {
		//see if any one video contains MOST of the keywords, and pick that video?
		//validate url - make sure its a video

		return "sampleUrl";
	}


	public HashSet<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(HashSet<String> keywords) {
		this.keywords = keywords;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int[] getScore() {
		return score;
	}

	public void setScore(int[] score) {
		this.score = score;
	}



}
