import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Play {

	public static final String USER_AGENT = "User-Agent: desktop:PMR:v0.0.1 (by /u/pmrtest)";
	private HashSet<String> shortKeywords;
	private HashSet<String> longKeywords;
	private Date date;
	private int[] score;


	Play(Date timeNow, HashSet<String> shortKeywords, HashSet<String> longKeywords, Date date, int[] score) {
		this.score = score;
		this.shortKeywords = shortKeywords;
		this.longKeywords = longKeywords;
		this.date = date;
	}

	public String crawlSites() {
		//see if any one video contains MOST of the keywords, and pick that video?
		//validate url - make sure its a video
		String redditURL = "http://www.reddit.com/r/soccer/new";
		Document document = null;
		try {
			document = Jsoup.connect(redditURL).userAgent(USER_AGENT).timeout(0).get(); //Get the url
			Elements links = document.select("a[href]"); //Get the links from the url
			System.out.println("Links: " + links.size());
			for(Element link: links){
				System.out.println("#Link: " + link.attr("abs:href") + " " + link.text());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//Check content size and see if it is approximately the size we'd expect a video to be
		//HttpURLConnection content = (HttpURLConnection) new URL("https://docs.oracle.com/javase/7/docs/api/java/lang/Object.html").openConnection();
		//System.out.println(content.getContentLength());
		return "sampleUrl";
	}



	public HashSet<String> getShortKeywords() {
		return shortKeywords;
	}

	public void setShortKeywords(HashSet<String> shortKeywords) {
		this.shortKeywords = shortKeywords;
	}

	public HashSet<String> getLongKeywords() {
		return longKeywords;
	}

	public void setLongKeywords(HashSet<String> longKeywords) {
		this.longKeywords = longKeywords;
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
