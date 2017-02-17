import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlerThread implements Runnable {

	private Play play;
	public static final String USER_AGENT = "User-Agent: desktop:PMR:v0.0.1 (by /u/pmrtest)"; //Required by reddit to be able to crawl their site
	
	public CrawlerThread() {
		
	}

	@Override
	public void run() {
		

		String redditURL = "http://www.reddit.com/r/soccer/new";
		Document document = null;
		try {
			document = Jsoup.connect(redditURL).userAgent(USER_AGENT).timeout(0).get(); //Get the url - Reddit only accepts 2 requests a minute
			Elements links = document.select("div.thing"); //Get the links from the url
			System.out.println("Total Links: " + links.size());
			for (Element link: links) {
				System.out.println(link.select("p.title").select("a.title").text()); //title
				//if(link.text().matches(".*(\\(|\\[)\\s?\\d{1}\\s?\\-\\s?\\d{1}\\s?(\\)|\\]).*")){  //does the link text have something like (2-0) displaying the score of a game
				//if above returns true, do a keyword check for content of the post, then do sql 'select user, email where keywords='<keywords>' etc..
				System.out.println(link.select("p.title").select("a.title").attr("href")); //url
				System.out.println(link.select("p.tagline").select("time").attr("title")); //time
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
	
		//Check content size and see if it is approximately the size we'd expect a video to be
		//HttpURLConnection content = (HttpURLConnection) new URL("https://docs.oracle.com/javase/7/docs/api/java/lang/Object.html").openConnection();
		//System.out.println(content.getContentLength());
		
		String url = null;
		url = "sampleURL";

		//crawl sites with the given play
		figureOutWhoToSendItTo(url, main.users, play.getShortKeywords()); //also calls send method
	}

	public static void figureOutWhoToSendItTo(String url, ArrayList<User> users, HashSet<String> hashSet) {
		//given the associated keywords with the video, see if they match "tags" that one can subscribe to (either team name/players)
		//and only send the message to people who are subscribed
		//pass on to "sendAlert" method
		for (int i=0; i<users.size(); i ++) { //for each user...
			//if tags match...
			for (int k=0; k<users.get(i).getTags().size(); k++) { //for each tag within each user
				for (String q : hashSet) {
					if (users.get(i).getTags().get(k).toLowerCase().contains(q.toLowerCase())) {
						users.get(i).sendAlert(url);
					}
				}
			}
		}
	}


}
