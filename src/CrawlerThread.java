import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sendgrid.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CrawlerThread implements Runnable {

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

		while (true) { //run forever unless stopped

			try {
				Thread.sleep(60000); //refresh page every n/1k seconds 
			} catch (InterruptedException e2) {
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
							//does the link text have something like (2-0) displaying the score of a game ^[0-9]+(-[0-9]+)
							if (link.select("p.title").select("a.title").text().matches("^[0-9]+(-[0-9]+)")) { // old .*(\\(|\\[)\\s?\\d{1}\\s?\\-\\s?\\d{1}\\s?(\\)|\\]).*
								System.out.println("new post found");
								String time = link.select("p.tagline").select("time").attr("title");
								System.out.println(time); //time
								String title = link.select("p.title").select("a.title").text();
								System.out.println(title); //title
								String url = link.select("p.title").select("a.title").attr("href");
								System.out.println(url); //url
								mostRecentPostTime = formatter.parse(link.select("p.tagline").select("time").attr("title")); //update most recent post
								String keyword = parseKeywords(title); //identify player keywords within play description
								if (findSubscribedUsers(keyword) != null) { //if no users are subscribed to a particular player, don't try to send email (it will fail)
									sendEmail(url, keyword, findSubscribedUsers(keyword)); //send email to users who match keywords - send them the url, use keyword in email title/body; user's email is returned from sql query
								}

							}
						}
					} catch (ParseException e1) {
						e1.printStackTrace();
					}	
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public static String parseKeywords(String postDescription) { //should return ArrayList<String> edit: only if we find multiple keywords in a single play? not worth changing for now
		try {
			BufferedReader reader = new BufferedReader (new FileReader("list-of-players.csv"));
			System.out.println("File found, trying to find player in play snippet");
			String line;
			while ((line = reader.readLine()) != null) {
				if (postDescription.contains(line)) {
					System.out.println(line);
					return line; //found a keyword - we're done
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

		ArrayList<String> subscribedUsers = new ArrayList<String>();
		
		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement = null;
		try{
			String url = "jdbc:sqlite:../server/db/pmr.db";
			connection = DriverManager.getConnection(url);
			String sql = "Select Email from User WHERE Keywords like '%" + keyword + "%' and ReceiveEmails=1;";
			System.out.println(sql);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			System.out.println("Connection successful");
			
			//iterate over multiple results
			subscribedUsers.add(resultSet.getString("Email"));
			if(resultSet.next()){
				subscribedUsers.add(resultSet.getString("Email"));
			}
			return subscribedUsers;

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

	public static void sendEmail(String link, String keyword, ArrayList<String> emailAddress) {

		for (String em : emailAddress) {
			Email from = new Email(""); //censor this
			String subject = "PMR Highlight Found - " + keyword;
			//Email to = new Email(email);
			Email to = new Email(em);
			Content content = new Content("text/plain", "Goal by " + keyword + "!" + " View (" + link + ").\n\n\n If this wasn't the correct player you selected, it's easiest just to uncheck that player"
					+ "within the website - we're working on a solution to improve our app's cognitive ability.");
			Mail mail = new Mail(from, subject, to, content);
			SendGrid sg = new SendGrid(""); //censor this
			Request request = new Request();
	
			try {
				request.method = Method.POST;
				request.endpoint = "mail/send";
				request.body = mail.build();
				Response response = sg.api(request);
				System.out.println(response.statusCode);
				System.out.println(response.body);
				System.out.println(response.headers);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}
