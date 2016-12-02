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
