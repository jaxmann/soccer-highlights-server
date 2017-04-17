import static java.nio.charset.StandardCharsets.ISO_8859_1;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import java.sql.PreparedStatement;
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

		Pattern p = Pattern.compile("[0-9]+(-[0-9]+)"); //does the link text have something like (2-0) displaying the score of a game ^[0-9]+(-[0-9]+)

		while (true) { //run forever unless stopped

			try {
				int refreshTime = getSleepTime();
				//logger.info("Current refresh rate: " + refreshTime / 60000 + " min");
				Thread.sleep(refreshTime); //refresh page every n/1k seconds 
			} catch (InterruptedException e2) {
				logger.error(e2.getMessage());
			}

			try {
				document = Jsoup.connect(redditURL).userAgent(USER_AGENT).timeout(0).get(); //Get the url - Reddit only accepts 2 requests a minute. edit: 60/min i think? -jonathan
				Elements links = document.select("div.thing"); //Get the entire posts from the doc

				logger.info("Most recent post time: " + mostRecentPostTime);

				for (Element link: links) {
					String inputTime = link.select("p.tagline").select("time").attr("title");
					try {
						Date dateReddit = formatter.parse(inputTime);
						if (mostRecentPostTime.compareTo(dateReddit) < 0) {
							Matcher m = p.matcher(link.select("p.title").select("a.title").text());

							if (m.find()) { 
								String time = link.select("p.tagline").select("time").attr("title");
								String title = link.select("p.title").select("a.title").text();
								String url = link.select("p.title").select("a.title").attr("href");

								logger.info("New post found at " + time + " TITLE: " + title);

								mostRecentPostTime = formatter.parse(link.select("p.tagline").select("time").attr("title")); //update most recent post time
								String keyword = parseKeywords(title); //identify player keywords within play description
								logger.info("Keyword is: " + keyword);
								ArrayList<String> subbedUsers = findSubscribedUsers(keyword);
								logger.info("Number of subbed users: " + subbedUsers.size());
								if (subbedUsers.size() != 0) { //if no users are subscribed to a particular player, don't try to send email (it will fail)
									sendEmail(url, keyword, subbedUsers); //send email to users who match keywords - send them the url, use keyword in email title/body; user's email is returned from sql query
								}

							}
						}
					} catch (ParseException e1) {
						logger.error(e1.getMessage());
					}	
				}
			} catch (IOException e) {
				//exceptions involving connecting to reddit (i.e 503/502 http errors)
				logger.error(e.getMessage());
				logger.error(e.toString());
			}
		}
	}


	public static String parseKeywords(String postDescription) { 
		try {
			BufferedReader reader = new BufferedReader (new FileReader("output.csv")); //backup version of this is "list-of-players2.csv"
			String line;

			while ((line = reader.readLine()) != null) {
//				byte ptext[] = line.getBytes(ISO_8859_1);
//				String newline = new String(ptext, UTF_8);

				String[] s = line.split(",");
				for (String player : s) {
					
					// find player starting at start of string or after a whitespace with trailing whitespace, apostrophe, or line boundary
					String reg = "((^|\\s)" + player + "('|\\s|$))|((^|\\s)" + simplify.simplifyName(player) + "('|\\s|$))";
					Pattern p = Pattern.compile(reg);
					Matcher m = p.matcher(postDescription);

					if (m.find()) {
						System.out.println(postDescription.substring(m.start(), m.end()));
						logger.info("Player found inside csv: " + s[0]);
						return s[0]; 
					} 
					
				}
			}

			reader.close();

		} catch (Exception e) {
			logger.error("Error trying to read player file");
		}

		return "no-player-found"; //i.e no player found in the csv
	}

	public static ArrayList<String> findSubscribedUsers(String keyword) { 

		ArrayList<String> subscribedUsers = new ArrayList<String>();

		Connection connection = null;
		ResultSet resultSet = null;
		ResultSet tqResultSet = null;
		Statement statement = null;
		Statement tqStatement = null;
		try{
			String url = "jdbc:sqlite:../server/db/pmr.db";
			connection = DriverManager.getConnection(url);
			long currentTime = System.nanoTime();
			keyword = keyword.replace("'", "''");
			String sql = "Select Email from User WHERE Keywords like '%" + keyword + "%' and ReceiveEmails<" + currentTime + ";";
			logger.info("SQL: " + sql);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			//connection successful if error not caught below

			//iterate over multiple results
			if(resultSet.next()){
				String sqlTQ = "Select * from Timeq WHERE Email='" + resultSet.getString("Email") + "' and Player='" + keyword + "';";
				logger.info("SQL time queue: " + sqlTQ);
				tqStatement = connection.createStatement();
				tqResultSet = tqStatement.executeQuery(sqlTQ);

				logger.info("TQ SQL Size is: " + tqResultSet.getFetchSize());

				if (tqResultSet.getFetchSize() != 0) {
					subscribedUsers.add(resultSet.getString("Email"));
				}

			}


			return subscribedUsers;

		} catch (SQLException e){
			logger.error(e.getMessage());
		} finally {
			try {
				if (connection != null) {
					resultSet.close();
					statement.close();
					connection.close();
				}
			} catch (SQLException ex) {
				logger.error(ex.getMessage());
			}
		}

		return subscribedUsers;
	}

	public static void sendEmail(String link, String keyword, ArrayList<String> emailAddresses) {

		//SG.txt and SGEmail.txt both need to be in home directory
		String home = System.getProperty("user.home");

		byte[] encoded = null;
		String pwd = "";
		try {
			encoded = Files.readAllBytes(Paths.get(home + "\\SG.txt"));
		} catch (IOException e1) {
			logger.error(e1.getMessage());
		}
		try {
			pwd =  new String(encoded, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}

		byte[] encoded2 = null;
		String pmremail = "";
		try {
			encoded2 = Files.readAllBytes(Paths.get(home + "\\SGEmail.txt"));
		} catch (IOException e1) {
			logger.error(e1.getMessage());
		}
		try {
			pmremail =  new String(encoded2, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}

		for (String em : emailAddresses) {

			logger.info("Attempting to email: [" + em + "]...");

			Email from = new Email(pmremail); 
			String subject = "PMR Highlight Found - " + keyword;
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
				/*System.out.println(response.statusCode);
				System.out.println(response.body);
				System.out.println(response.headers);*/
				logger.info("Email sent to [" + em + "] successfully - starting insert into time queue...");
				logger.info(response);
				
				//if email sends, do an insert into the time queue

				Connection connection = null;
				ResultSet resultSet = null;
				Statement statement = null;

				try {
					String url = "jdbc:sqlite:../server/db/pmr.db";
					connection = DriverManager.getConnection(url);
					long currentTime = System.nanoTime();
					keyword = keyword.replace("'", "''");
					String sql = "INSERT INTO User(Email, Player, Timestamp)"
							+ " VALUES(?,?,?)";
					PreparedStatement preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, em);
					preparedStatement.setString(2, keyword);
					preparedStatement.setLong(3, currentTime);

					preparedStatement.executeUpdate(); 
					logger.info("SQL Insert into TQ: " + sql);
					statement = connection.createStatement();
					resultSet = statement.executeQuery(sql);

				} catch (SQLException e){
					logger.error(e.getMessage());
				} finally {
					try {
						if (connection != null) {
							resultSet.close();
							statement.close();
							connection.close();
						}
					} catch (SQLException ex) {
						logger.error(ex.getMessage());
					}
				}

			} catch (IOException ex) {
				logger.error(ex.getMessage());
			}
		}
	}

	public static int getSleepTime() {
		Calendar calendar = Calendar.getInstance();
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		if (hours >= 19 && hours <= 6) { //7pm to 7am
			return 600000; //10 minutes
		} else {
			return 60000; //1 minute
		}
	}


}
