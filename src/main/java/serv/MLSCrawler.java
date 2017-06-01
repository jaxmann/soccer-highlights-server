package serv;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.regex.Matcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MLSCrawler {

	public static void main(String[] args) {
		start();

	}
	
	public static void start() {
		
		String mlsURL = "https://en.wikipedia.org/wiki/Major_League_Soccer";
		
		try {
			Document document = Jsoup.connect(mlsURL).followRedirects(true).get(); 
			Elements conferences = document.select("tr > td > a[title]:matches(United|Chicago|SC|Impact|Revolution|FC|Red Bulls|Union|Rapids|Dynamo|Galaxy|Portland|Real Salt|San Jose|Sounders|Kansas City|Vancouver)"); 
			
			HashSet<String> hs = new HashSet<String>();
			
			for (Element e : conferences) {
				hs.add(e.toString());
			}
			
			for (String s : hs) {
				System.out.println(s);
			}


			/*for (i=links.size() - 1; i>=0; i--) { //doesn't allocate any new memory 
				link = links.get(i);
				inputTime = link.select("p.tagline").select("time").attr("title");
				try {
					dateReddit = formatter.parse(inputTime);
					if (mostRecentPostTime.compareTo(dateReddit) < 0) {
						m = p.matcher(link.select("p.title").select("a.title").text());
						if (m.find()) { 
							time = link.select("p.tagline").select("time").attr("title");
							title = link.select("p.title").select("a.title").text();
							url = link.select("p.title").select("a.title").attr("href");

							mostRecentPostTime = formatter.parse(link.select("p.tagline").select("time").attr("title")); //update most recent post time

							if (url.contains(".mp4") || url.contains("streamable")) { //only trigger if it's a video link

								logger.info("New post found: [" + title + "] at [" + time + "]");

								keyword = parseKeywords(title, url); //identify player keywords within play description
								logger.info("Keyword is: [" + keyword + "]");
								subbedUsers = findSubscribedUsers(keyword);
								logger.info("Number of subbed users: [" + subbedUsers.size() + "]");
								if (subbedUsers.size() != 0) { //if no users are subscribed to a particular player, don't try to send email (it will fail)
									sendEmail(url, keyword, subbedUsers); //send email to users who match keywords - send them the url, use keyword in email title/body; user's email is returned from sql query
								}

							} else {
								logger.info("Non-video post found: [" + title + "] at [" + time + "]");
							}
						}
					}
				} catch (ParseException e1) {
					logger.error(e1.toString());
				}	
			}*/
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
