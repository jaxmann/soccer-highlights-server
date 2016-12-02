import java.util.ArrayList;
import java.util.Date;

public class HighlightLink {

	private String link;
	private String linkTitle;
	private ArrayList<String> keywords;
	private Long retrievalTime;
	
	public HighlightLink(String link, String linkTitle, ArrayList<String> keywords, Long retrievalTime){
		this.link = link;
		this.linkTitle = linkTitle;
		this.keywords = keywords;
		this.retrievalTime = retrievalTime;
	}
	
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getLinkTitle() {
		return linkTitle;
	}

	public void setLinkTitle(String linkTitle) {
		this.linkTitle = linkTitle;
	}

	public ArrayList<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(ArrayList<String> keywords) {
		this.keywords = keywords;
	}

	public Long getRetrievalTime() {
		return retrievalTime;
	}

	public void setRetrievalTime(Long retrievalTime) {
		this.retrievalTime = retrievalTime;
	}
	
}
