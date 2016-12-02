import java.util.ArrayList;
import java.util.Date;

public class Play {

	private Date timeNow;
	private ArrayList<String> keywords;
	private Date date;
	private ArrayList<String> tags;
	
	Play(Date timeNow, ArrayList<String> keywords, Date date, ArrayList<String> tags) {
		this.timeNow = timeNow;
		this.keywords = keywords;
		this.date = date;
		this.tags = tags;
	}
	
	public String crawlSites() {
		
		//see if any one video contains MOST of the keywords, and pick that video?
		
		
		//pass it on to "figureOutWhoToSendItTo" method
		
		return "sampleUrl";
	}

	public Date getTimeNow() {
		return timeNow;
	}

	public void setTimeNow(Date timeNow) {
		this.timeNow = timeNow;
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

	public ArrayList<String> getTags() {
		return tags;
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}
	
	
	
}
