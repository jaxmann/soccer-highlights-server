import java.util.ArrayList;
import java.util.Date;

public class Play {

	private Date timeNow;
	private ArrayList keywords;
	private Date date;
	private ArrayList tags;
	
	Play(Date timeNow, ArrayList keywords, Date day, ArrayList tags) {
		this.timeNow = timeNow;
		this.keywords = keywords;
		this.date = date;
		this.tags = tags;
	}

	public Date getTimeNow() {
		return timeNow;
	}

	public void setTimeNow(Date timeNow) {
		this.timeNow = timeNow;
	}

	public ArrayList getKeywords() {
		return keywords;
	}

	public void setKeywords(ArrayList keywords) {
		this.keywords = keywords;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public ArrayList getTags() {
		return tags;
	}

	public void setTags(ArrayList tags) {
		this.tags = tags;
	}
	
	
	
}
