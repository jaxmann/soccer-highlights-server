package test;


import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import serv.simplify;

public class Playground {

	public static void main(String[] args) {
		
		String postDescription = "Wissam Ben Yedder 3:6 against Marseille";
		
		String minName = "Marco Reus";
		
		postDescription = postDescription.replaceAll("([A-Z])\\.(\\s\\w)", "$1$2"); //M. Reus -> M Reus
		if ((postDescription.charAt(0) == 'M' || postDescription.charAt(0) == 'D') && postDescription.charAt(1) == ' ') {
			postDescription = postDescription.substring(2); //M Reus - > Reus
		}
		
		String hashtag = "";
		String[] fullN = minName.split(" ");
		if (fullN.length == 1) {
			hashtag = fullN[0];
		} else if (fullN.length == 2) {
			hashtag = fullN[1];
		} else {
			hashtag = fullN[fullN.length - 2] + fullN[fullN.length - 1];
		}
		
		String stat = postDescription + " | " + "http://google.com" + " #" + hashtag.replaceAll("\\s|[-]|[!]|[$]|[%]|[\\^]|[&]|[\\*]|[\\+]","");
		
		System.out.println(stat);
		
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


