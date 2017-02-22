import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CrawlerThread implements Runnable {

	private Play play;
	public static final String USER_AGENT = "User-Agent: desktop:PMR:v0.0.1 (by /u/pmrtest)"; //Required by reddit to be able to crawl their site

	public CrawlerThread() {

	}

	public void run() {


		String redditURL = "http://www.reddit.com/r/soccer/new";
		Document document = null;
		
		Calendar cal = Calendar.getInstance();
		Date mostRecentPostTime = cal.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		String keyword_marco = parseKeywords("Marco Reus scores against Hertha (1-0)");
		System.out.println(findSubscribedUsers(keyword_marco));
		
		int i=0;
		while (i<2) { //run forever unless stopped
			i=2;
			try {
				Thread.sleep(1000); //refresh page every n/1k seconds 
			} catch (InterruptedException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			try {
				document = Jsoup.connect(redditURL).userAgent(USER_AGENT).timeout(0).get(); //Get the url - Reddit only accepts 2 requests a minute. edit: 60/min i think? -jonathan
				Elements links = document.select("div.thing"); //Get the entire posts from the doc
				System.out.println("Total Links: " + links.size());
				System.out.println(mostRecentPostTime);
				
				for (Element link: links) {
					String inputTime = link.select("p.tagline").select("time").attr("title");
					try {
						Date dateReddit = formatter.parse(inputTime);
						if (mostRecentPostTime.compareTo(dateReddit) < 0) {
							//does the link text have something like (2-0) displaying the score of a game
							if (link.select("p.title").select("a.title").text().matches(".*(\\(|\\[)\\s?\\d{1}\\s?\\-\\s?\\d{1}\\s?(\\)|\\]).*")) {
								// do a keyword check for content of the post, then do sql 'select user, email where keywords='<keywords>' etc..
								System.out.println("new post found");
								System.out.println(link.select("p.tagline").select("time").attr("title")); //time
								System.out.println(link.select("p.title").select("a.title").text()); //title
								System.out.println(link.select("p.title").select("a.title").attr("href")); //url
								mostRecentPostTime = formatter.parse(link.select("p.tagline").select("time").attr("title")); //update most recent post
								parseKeywords(link.select("p.title").select("a.title").text());
								
							}
						}
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					
					
				}


				//			for(Element link: links){
				//				System.out.println(link);
				//				if(link.text().matches(".*(\\(|\\[)\\s?\\d{1}\\s?\\-\\s?\\d{1}\\s?(\\)|\\]).*")){  //does the link text have something like (2-0) displaying the score of a game
				//					System.out.println("#: " + link.text());
				//					long currentTime = System.currentTimeMillis();
				//					ArrayList<String> keywords = new ArrayList<String>();
				//					for(String playerName: main.fullPlayers){ //Use list of full player names, split into parts and check link text against player names add to keyword list
				//						String[] playerNameSplit = playerName.split(" ");
				//						for(int i = 0; i < playerNameSplit.length; i++){
				//							if(link.text().contains(playerNameSplit[i])){
				//								keywords.add(playerNameSplit[i]);
				//							}
				//						}
				//					}
				//					for(String teamName: main.fullTeams){ //Use list of full teams, split into parts and check link text against split up team names add to keyword list
				//						String[] teamNameSplit = teamName.split(" ");
				//						for(int i = 0; i < teamNameSplit.length; i++){
				//							if(link.text().contains(teamNameSplit[i])){
				//								keywords.add(teamNameSplit[i]);
				//							}
				//						}
				//					}
				//					//link.attr("abs:href") - The actual link --- link.text() - the link's text
				//					HighlightLink highlightLink = new HighlightLink(link.attr("abs:href"), link.text(), keywords, currentTime);
				//					main.highlightLinks.add(highlightLink);
				//				}
				//			}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//Check content size and see if it is approximately the size we'd expect a video to be
		//HttpURLConnection content = (HttpURLConnection) new URL("https://docs.oracle.com/javase/7/docs/api/java/lang/Object.html").openConnection();
		//System.out.println(content.getContentLength());

//		String url = null;
//		url = "sampleURL";

		//crawl sites with the given play
		//figureOutWhoToSendItTo(url, main.users, play.getShortKeywords()); //also calls send method
		//System.out.println("Done.");
	}

//	public static void figureOutWhoToSendItTo(String url, ArrayList<User> users, HashSet<String> hashSet) {
//		//given the associated keywords with the video, see if they match "tags" that one can subscribe to (either team name/players)
//		//and only send the message to people who are subscribed
//		//pass on to "sendAlert" method
//		for (int i=0; i<users.size(); i ++) { //for each user...
//			//if tags match...
//			for (int k=0; k<users.get(i).getTags().size(); k++) { //for each tag within each user
//				for (String q : hashSet) {
//					if (users.get(i).getTags().get(k).toLowerCase().contains(q.toLowerCase())) {
//						users.get(i).sendAlert(url);
//					}
//				}
//			}
//		}
//	}
	
	public static String parseKeywords(String postDescription) { //should return ArrayList<String>
		ArrayList<String> keywords = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader (new FileReader("list-of-players.csv"));
			System.out.println("File found, trying to find player in play snippet");
			String line;
			while ((line = reader.readLine()) != null) {
				if (postDescription.contains(line)) {
					System.out.println(line);
					return line; //remove later - just for marco testing
					//keywords.add(line);
					//break; //no need to continue wasting compute time once we've found (hopefully) the only match - might need to be revised if we look for multiple players or assists (?)
				}
			}
			reader.close();
			System.out.println("Done parsing for keywords.");
		} catch (Exception e) {
			System.err.println("Error trying to read player file");
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static ArrayList<String> findSubscribedUsers(String keyword) {
		
		System.out.println("Keyword entering subscriber search is " + keyword);
		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement = null;
		try{
			String url = "jdbc:sqlite:../server/db/pmr.db";
			connection = DriverManager.getConnection(url);
			String sql = "Select * from User WHERE Keywords like '%" + keyword + "%';";
			System.out.println(sql);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			System.out.println(resultSet.first());
			System.out.println("Connection successful");
		} catch (SQLException e){
			System.out.println(e.getMessage());
		} finally {
			try{
				if (connection != null){
					resultSet.close();
					statement.close();
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println(ex.getMessage());
			}
		}
		
		
		return null;
	}


}
