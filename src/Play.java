import java.io.IOException;
import java.util.Date;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Play {

	private HashSet<String> keywords;
	private Date date;
	private int[] score;


	Play(Date timeNow, HashSet<String> shortKeywords, HashSet<String> longKeywords, Date date, int[] score) {
		this.score = score;
		this.keywords = keywords;
		this.date = date;
	}

	public String crawlSites() {
		//see if any one video contains MOST of the keywords, and pick that video?
		//validate url - make sure its a video
		String redditURL = "http://www.reddit.com/r/soccer";
		Document document = null;
		try {
			document = Jsoup.connect(redditURL).get(); //Get the url
			Elements links = document.select("link[href]"); //Get the links from the url
			System.out.println("Links: " + links.size());
			for(Element link: links){
				System.out.println("#Link: " + link.attr("abs:href") + " " + link.text());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
