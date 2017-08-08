package builder;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import serv.simplify;

public class Transfers {

	public static void main(String[] args) {

		/*String[] tms = {"Atlanta_United_FC","Chicago_Fire_Soccer_Club","Columbus_Crew_SC","D.C._United", "Montreal_Impact","New_England_Revolution",
				"New_York_City_FC","New_York_Red_Bulls","Orlando_City_SC","Philadelphia_Union","Toronto_FC",
				"Colorado_Rapids","FC_Dallas","Houston_Dynamo","LA_Galaxy","Minnesota_United_FC","Portland_Timbers",
				"Real_Salt_Lake","San_Jose_Earthquakes","Seattle_Sounders_FC","Sporting_Kansas_City","Vancouver_Whitecaps_FC"
		};*/

		/*for (String s : tms) {
			start(s);
		}*/

		start();

		//start("Orlando_City_SC");

	}

	public static void start() {

		String transferURL = "http://www.espnfc.us/transfers?year=2017";

		HashMap<String,String[]> xferredPlayers = new HashMap<String, String[]>();

		try {
			Document document = Jsoup.connect(transferURL).followRedirects(true).get(); 
			Elements players = document.select("div.transfer-module");  

			for (Element thisPlayer: players) {
				String[] p = new String[2];
				String player = thisPlayer.select("div.transfer-module-header > h4 > a").text();
				p[0] = thisPlayer.select("div.transfer-module-content > div.transfer-graphic > a.previous").text();
				p[1] = thisPlayer.select("div.transfer-module-content > div.transfer-graphic > a.new").text();
				xferredPlayers.put(simplify.simplifyName(player), p);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//transferred players (most recent page) loaded into hashmap ^, now compare to existing set of players and update as necessary (below)

		try {
			BufferedReader reader = new BufferedReader (new FileReader("/home/ec2-user/server/regenerate-players//fullTable.csv"));
			String line;
 

			while ((line = reader.readLine()) != null) {
				String[] arr = line.split(",");
				String name = simplify.simplifyName(arr[2].trim());
				
				for (HashMap.Entry<String, String[]> entry : xferredPlayers.entrySet()) {
					String key = entry.getKey();
					String[] value = entry.getValue();
					
					if (similarity(key, arr[2]) >= .500 && similarity(value[0], arr[1]) >= .500) {
						System.out.println(arr[0] + ", " + xferredPlayers.get(name)[1] + ", " + arr[2].trim() + "," + arr[3]);
					} else {
						System.out.println(line);
					}
				}
				
				/*if (xferredPlayers.containsKey(name)) {
					if (xferredPlayers.get(name)[0].equals(arr[1].trim())) {
						System.out.println(arr[0] + ", " + xferredPlayers.get(name)[1] + ", " + arr[2].trim() + "," + arr[3]);
					} else {
						System.out.println(line);
					}
				} else {
					System.out.println(line);
				}*/

			}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		


	}
	
	/**
	   * Calculates the similarity (a number within 0 and 1) between two strings.
	   */
	  public static double similarity(String s1, String s2) {
	    String longer = s1, shorter = s2;
	    if (s1.length() < s2.length()) { // longer should always have greater length
	      longer = s2; shorter = s1;
	    }
	    int longerLength = longer.length();
	    if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
	    /* // If you have StringUtils, you can use it to calculate the edit distance:
	    return (longerLength - StringUtils.getLevenshteinDistance(longer, shorter)) /
	                               (double) longerLength; */
	    return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

	  }

	  // Example implementation of the Levenshtein Edit Distance
	  // See http://rosettacode.org/wiki/Levenshtein_distance#Java
	  public static int editDistance(String s1, String s2) {
	    s1 = s1.toLowerCase();
	    s2 = s2.toLowerCase();

	    int[] costs = new int[s2.length() + 1];
	    for (int i = 0; i <= s1.length(); i++) {
	      int lastValue = i;
	      for (int j = 0; j <= s2.length(); j++) {
	        if (i == 0)
	          costs[j] = j;
	        else {
	          if (j > 0) {
	            int newValue = costs[j - 1];
	            if (s1.charAt(i - 1) != s2.charAt(j - 1))
	              newValue = Math.min(Math.min(newValue, lastValue),
	                  costs[j]) + 1;
	            costs[j - 1] = lastValue;
	            lastValue = newValue;
	          }
	        }
	      }
	      if (i > 0)
	        costs[s2.length()] = lastValue;
	    }
	    return costs[s2.length()];
	  }

	  public static void printSimilarity(String s, String t) {
	    System.out.println(String.format(
	      "%.3f is the similarity between \"%s\" and \"%s\"", similarity(s, t), s, t));
	  }

}
