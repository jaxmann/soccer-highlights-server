import static java.nio.charset.StandardCharsets.ISO_8859_1;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class CrawlerThread implements Runnable {

	public static final String USER_AGENT = "User-Agent: desktop:PMR:v0.0.1 (by /u/pmrtest)"; //Required by reddit to be able to crawl their site
	private static Logger logger = Logger.getLogger(CrawlerThread.class);

	public CrawlerThread() {

	}

	public void run() {
		
		PropertyConfigurator.configure("log4j-configuration.txt"); //configure log4j binding with properties from log4j-configuration file
	
		

		String redditURL = "http://www.reddit.com/r/soccer/new";
		Document document = null;

		Calendar cal = Calendar.getInstance();
		Date mostRecentPostTime = cal.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

		Pattern p = Pattern.compile("[0-9]+(-[0-9]+)");


		while (true) { //run forever unless stopped

			try {
				//modify this number to change based on time of the day/week/year potentially
				//put a logger to print the refresh rate here
				Thread.sleep(60000); //refresh page every n/1k seconds 
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}

			try {
				document = Jsoup.connect(redditURL).userAgent(USER_AGENT).timeout(0).get(); //Get the url - Reddit only accepts 2 requests a minute. edit: 60/min i think? -jonathan
				Elements links = document.select("div.thing"); //Get the entire posts from the doc
				//System.out.println("Total Links: " + links.size());
				logger.info("Most recent post time: " + mostRecentPostTime);
				System.out.println(mostRecentPostTime);

				for (Element link: links) {
					String inputTime = link.select("p.tagline").select("time").attr("title");
					try {
						Date dateReddit = formatter.parse(inputTime);
						if (mostRecentPostTime.compareTo(dateReddit) < 0) {
							Matcher m = p.matcher(link.select("p.title").select("a.title").text());
							//does the link text have something like (2-0) displaying the score of a game ^[0-9]+(-[0-9]+)
							if (m.find()) { // old .*(\\(|\\[)\\s?\\d{1}\\s?\\-\\s?\\d{1}\\s?(\\)|\\]).*
								System.out.println("new post found");
								String time = link.select("p.tagline").select("time").attr("title");
								System.out.println(time); //time
								String title = link.select("p.title").select("a.title").text();
								System.out.println(title); //title
								String url = link.select("p.title").select("a.title").attr("href");
								System.out.println(url); //url
								mostRecentPostTime = formatter.parse(link.select("p.tagline").select("time").attr("title")); //update most recent post
								String keyword = parseKeywords(title); //identify player keywords within play description
								ArrayList<String> subbedUsers = findSubscribedUsers(keyword);
								if (subbedUsers.size() != 0) { //if no users are subscribed to a particular player, don't try to send email (it will fail)
									sendEmail(url, keyword, subbedUsers); //send email to users who match keywords - send them the url, use keyword in email title/body; user's email is returned from sql query
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
			BufferedReader reader = new BufferedReader (new FileReader("output.csv")); //backup list-of-players2
			System.out.println("File found, trying to find player in play snippet");
			String line;
			while ((line = reader.readLine()) != null) {
				byte ptext[] = line.getBytes(ISO_8859_1);
				String newline = new String(ptext, UTF_8);

				String[] s = newline.split(",");
				for (String a : s) {
					//spaces are so that we actually find "Can" (on a word boundary) instead of Lezcano, for instance - not tested yet
					if (postDescription.contains(" " + a + " ") || postDescription.contains(" " + simplify.simplifyName(a) + " ")) { //either ascii > 127 name or simplified name in play description? if yes...
						System.out.println(s[0]);
//						String firstHalf;
//						if (postDescription.contains(a)) {
//							firstHalf = postDescription.substring(0, postDescription.indexOf(a));
//							while (parseKeywords(firstHalf) != null) {
//								firstHalf = parseKeywords(firstHalf);
//							}
//						} else {
//							firstHalf = postDescription.substring(0, postDescription.indexOf(simplify.simplifyName(a)));
//						}
						return s[0]; //found a keyword - we're done - technically should probably return the one that occurs first? idk
					}
				}


			}
			reader.close();
			System.out.println("Done parsing for keywords.");
		} catch (Exception e) {
			System.err.println("Error trying to read player file");
			e.printStackTrace();
		}

		return "poop"; //i.e no player found in the csv
	}

	public static ArrayList<String> findSubscribedUsers(String keyword) { 

		ArrayList<String> subscribedUsers = new ArrayList<String>();

		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement = null;
		try{
			String url = "jdbc:sqlite:../server/db/pmr.db";
			connection = DriverManager.getConnection(url);
			long currentTime = System.nanoTime();
			//note - this will get printed twice because i check it against null above
			keyword = keyword.replace("'", "''");
			String sql = "Select Email from User WHERE Keywords like '%" + keyword + "%' and ReceiveEmails<" + currentTime + ";";
			System.out.println(sql);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			System.out.println("Connection successful");

			//iterate over multiple results
			
			
			//subscribedUsers.add(resultSet.getString("Email"));
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

		return subscribedUsers;
	}

	public static void sendEmail(String link, String keyword, ArrayList<String> emailAddresses) {

		//add sendgrid key a file
		String home = System.getProperty("user.home");
		byte[] encoded = null;
		String pwd = "";
		try {
			encoded = Files.readAllBytes(Paths.get(home + "\\SG.txt"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			pwd =  new String(encoded, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] encoded2 = null;
		String pmremail = "";
		try {
			encoded = Files.readAllBytes(Paths.get(home + "\\SGEmail.txt"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			pmremail =  new String(encoded, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (String em : emailAddresses) {
			
			System.out.println("email to send to " + em);
			
			Email from = new Email(pmremail); 
			String subject = "PMR Highlight Found - " + keyword;
			//Email to = new Email(email);
			Email to = new Email(em);
			Content content = new Content("text/plain", "Goal by " + keyword + "!" + " View (" + link + ").\n\n\n If this wasn't the correct player you selected, it's easiest just to uncheck that player"
					+ " within the website - we're working on a solution to improve our app's cognitive ability. If you notice any other bugs feel free to send me an email personally at jonathan.axmann09@gmail.com");
			Mail mail = new Mail(from, subject, to, content);
			
			
			SendGrid sg = new SendGrid(pwd); 
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
