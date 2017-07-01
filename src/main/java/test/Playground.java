package test;


import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UploadedMedia;
import twitter4j.auth.AccessToken;

import serv.simplify;

public class Playground {

	public static void main(String[] args) throws TwitterException {
		
		
		Twitter twitter = new TwitterFactory().getInstance();
//		twitter.setOAuthConsumer(consumerKey, consumerSecret);
//        AccessToken accessToken = new AccessToken(accessTokenStr,accessTokenSecret);
//        twitter.setOAuthAccessToken(accessToken);
		
        //twitter.setOAuthConsumer(consumerKey, consumerSecret);
        //AccessToken accessToken = new AccessToken(accessTokenStr,accessTokenSecret);
        //twitter.setOAuthAccessToken(accessToken);
        File file = new File("/home/jonathan/Downloads/video.mp4");
        //UploadedMedia media = twitter.uploadMedia(file);
        
        StatusUpdate update = new StatusUpdate("testing");
        UploadedMedia media = twitter.uploadMedia(arg0, arg1);
        
        long mediaIds[] = new long[1];
        mediaIds[0] = media.getMediaId();
        update.setMediaIds(mediaIds);
        
        twitter.updateStatus(update);
        
        System.out.println("it may have worked...");
        
        //StatusUpdate statusUpdate = new StatusUpdate(text);
        //statusUpdate.setMedia(media.getMediaId());
        //twitter.updateStatus(statusUpdate.inReplyToStatusId(tweetId));`
		
		
		
		
		
//		String postDescription = "celtic Man United goal vs Celtic 4-5";
//		
//		String tm = "celtic";
//		
//		String cleanedPostDescription = postDescription.replaceAll("[Uu]nited","");
//		
//		String teamRegex = "((^|\\s|\\()" + tm + "(\\)|'|\\s|$))|((^|\\s|\\()" + simplify.simplifyName(tm) + "(\\)|'|\\s|$))";
//		
//		Pattern teamP = Pattern.compile(teamRegex, Pattern.CASE_INSENSITIVE);
//		Matcher teamM = teamP.matcher(cleanedPostDescription);
//		
//		if (teamM.find()) {
//			System.out.println("regex found [" + cleanedPostDescription.substring(teamM.start(), teamM.end()).trim() + "] treated as [" + tm + "]");
//		} else {
//			System.out.println("found nothing");
//		}
		
		
		
//		String postDescription = "Germany 1-0 Cameroon - Demirbay 48' (FIFA Confederations Cup - Group B) - Streamable links in the comment section";
//		
//		String url = "https://my.mixtape.moe/bjdlnm.mp4";
//		
//		String playerHashtag = " #Demirbay";
//		
//		String team = "1899Hoffenheim";
//				
//		String teamHashtag = " #" + team.replaceAll("\\s|[-]|[!]|[$]|[%]|[\\^]|[&]|[\\*]|[\\+]|[']|\\d","");
//		
//		String countryHashtag = " #Germany";
//		
//		int maxPostLength = 140 - url.length() - countryHashtag.length() - teamHashtag.length() - playerHashtag.length() - 6 - 1; //140 max twitter, 3 is 1x" | ", second 3 is ..., 1 is off by 1 error below due to whitespace added
//				
//		String ellipsePost = "";
//		String[] postParts = postDescription.split(" ");
//		for (int i=0; i<postParts.length;i++) {
//			if (ellipsePost.length() + postParts[i].length() < maxPostLength) {
//				ellipsePost += postParts[i] + " ";
//			} else {
//				System.out.println(ellipsePost);
//				break;
//			}
//		}
//		ellipsePost = ellipsePost.trim();
//		ellipsePost += "...";
//		
//		System.out.println("ellipse length is: " + ellipsePost.length());
//		String stat = ellipsePost + " | " + url;
//
//		if (!playerHashtag.equals("") && (stat.length() + playerHashtag.length() <= 140)) {
//			stat += playerHashtag;
//		}
//		if (!teamHashtag.equals("") && (stat.length() + teamHashtag.length() <= 140)) {
//			stat += teamHashtag;
//		}
//		if (!countryHashtag.equals("") && (stat.length() + countryHashtag.length() <= 140)) {
//			stat += countryHashtag;
//		}		
//		
//		System.out.println(stat.length());
//		System.out.println(stat);
//		
//		System.out.println(playerHashtag + " | " + playerHashtag.length());
//		System.out.println(teamHashtag + " | " + teamHashtag.length());
//		System.out.println(countryHashtag + " | " + countryHashtag.length());
		
//		String postDescription = "Wissam Ben Yedder 3:6 against Marseille";
//		
//		String minName = "Marco Reus";
//		
//		String teamName = "AZ"; //not actually the name
//		
//		postDescription = postDescription.replaceAll("([A-Z])\\.(\\s\\w)", "$1$2"); //M. Reus -> M Reus
//		if ((postDescription.charAt(0) == 'M' || postDescription.charAt(0) == 'D') && postDescription.charAt(1) == ' ') {
//			postDescription = postDescription.substring(2); //M Reus - > Reus
//		}
//		
//		String hashtag = "";
//		String[] fullN = minName.split(" ");
//		if (fullN.length == 1) {
//			hashtag = fullN[0];
//		} else if (fullN.length == 2) {
//			hashtag = fullN[1];
//		} else {
//			hashtag = fullN[fullN.length - 2] + fullN[fullN.length - 1];
//		}
//		/////////////////////////
//		String[] teamParts = teamName.split(" ");
//		String teamHashtag = "";
//		if (teamParts.length == 1) {
//			teamHashtag = teamParts[0];
//		} else {
//			for (int i=0; i<teamParts.length;i++) {
//				if (!teamParts[i].equals(teamParts[i].toUpperCase())) {
//					teamHashtag += teamParts[i];
//				}
//			}
//		}
//		
//		
//		System.out.println(teamHashtag);
//		
//		String stat = postDescription + " | " + "http://google.com" + " #" + hashtag.replaceAll("\\s|[-]|[!]|[$]|[%]|[\\^]|[&]|[\\*]|[\\+]","");
//		
//		System.out.println(stat);
		
//		Calendar calendar = Calendar.getInstance();
//		int hours = calendar.get(Calendar.HOUR_OF_DAY);
//		
//		System.out.println("hours is" + hours);
		
//		System.out.println(simplify.simplifyName("Mesut Özil"));
		
/*String postDescription = "Juventus 1-3 Real Madrid (Ronaldo 63')";
		
		String player = "Ronaldo";
		
		String reg = "((^|\\s|\\()" + player + "(\\)|'|\\s|$))|((^|\\s|\\()" + simplify.simplifyName(player) + "(\\)|'|\\s|$))";
		Pattern p = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(postDescription);

		if (m.find()) {
			System.out.println(postDescription.substring(m.start(), m.end()));
		}*/
		
		
		/*String player = "Mickaël Le Bihan";
		
		System.out.println(player.toUpperCase());*/
		
		/*String score = "Reus goal 5-4 against Bayern";
		
		String regex = "[\\[|(]?[0-9][\\]|)]?-[0-9]";
		
		Pattern p = Pattern.compile(regex);
		
		Matcher m = p.matcher(score);
		
		if (m.find()) {
			System.out.println(score.substring(m.start(), m.end()));
		}*/
		
		/*String postDescription = "Marco Reus Mickael Le Bihan's 2nd goal vs. Ronaldo Sporting Gijon (2-3) against Cristiano Ronaldo Ronaldinho Ronaldo";
		
		//String player = "Mickaël Le Bihan";
		
		
//		String reg = "((^|\\s)" + player + "('|\\s|$))|((^|\\s)" + simplify.simplifyName(player) + "('|\\s|$))";
//		Pattern p = Pattern.compile(reg);
//		Matcher m = p.matcher(postDescription);
//
//		
//		if (m.find()) {
//			System.out.println(postDescription.substring(m.start(), m.end()));
//		} else {
//			System.out.println("not found");
//		}

		
//		System.out.println(simplify.simplifyName("Mickaël Le Bihan"));
//		System.out.println(postDescription.contains(simplify.simplifyUTF8Name("Mickaël Le Bihan")));
		String line2 = "Mickaël Le Bihan";
		//char c = 'á';
		//System.out.println(line2);

		//System.out.println(simplify.simplifyName(line2));

		HashMap<String, Integer> playersFound = new HashMap<String, Integer>();
		
		try {
			BufferedReader reader = new BufferedReader (new FileReader("output.csv")); //backup version of this is "list-of-players2.csv"
			String line;
			
			

			while ((line = reader.readLine()) != null) {
				
				//System.out.println(line);
//				byte ptext[] = line.getBytes(ISO_8859_1);
//				String newline = new String(ptext, UTF_8);
				
				//System.out.println(newline);

				String[] s = line.split(",");
				for (String player : s) {
					String reg = "((^|\\s)" + player + "('|\\s|$))|((^|\\s)" + simplify.simplifyName(player) + "('|\\s|$))";
					Pattern p = Pattern.compile(reg);
					Matcher m = p.matcher(postDescription);

					if (m.find()) {
						System.out.println("regex found " + postDescription.substring(m.start(), m.end()));
						System.out.println("player found " + s[0]);
						
						if (playersFound.containsKey(s[0])) {
							if (playersFound.get(s[0]) > m.end()) {
								playersFound.put(s[0], m.end());
							}
						} else {
							playersFound.put(s[0], m.end());
						}
						
//						logger.info("Player found inside csv: " + s[0]);
//						return s[0]; 
					}
				}
				
				
			}

			reader.close();

		} catch (Exception e) {
			//logger.error("Error trying to read player file");
		}*/
		
//		String postDescription = "Juventus 1-[1] Real Madrid ( Mandzukic M. 27')";
//		
//		Pattern p = Pattern.compile("[\\[|(]?[0-9][\\]|)]?-[\\[|(]?[0-9][\\]|)]?"); //does the link text have something like (2-0) displaying the score of a game ^[0-9]+(-[0-9]+)
//	
//		Matcher m = p.matcher(postDescription);
//		
//		if (m.find()) { 
//			String score = postDescription.substring(m.start(), m.end());
//			System.out.println(score.replaceAll("\\(|\\)|\\[|\\]|\\{|\\}", ""));
//		}
		
		

	}
	

}


