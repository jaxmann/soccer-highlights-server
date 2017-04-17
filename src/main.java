import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

import static java.nio.charset.StandardCharsets.UTF_8;

public class main {

	public static final boolean DEBUG = true;


	public static void main(String[] args) {
		
		String postDescription = "Mickael Le Bihan's 2nd goal vs. Sporting Gijon (2-3)";
		
		String player = "Mickaël Le Bihan";
		
		
		String reg = "((^|\\s)" + player + "('|\\s|$))|((^|\\s)" + simplify.simplifyName(player) + "('|\\s|$))";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(postDescription);

		
		if (m.find()) {
			System.out.println(postDescription.substring(m.start(), m.end()));
		} else {
			System.out.println("not found");
		}

		
//		System.out.println(simplify.simplifyName("Mickaël Le Bihan"));
//		System.out.println(postDescription.contains(simplify.simplifyUTF8Name("Mickaël Le Bihan")));
		String line2 = "Mickaël Le Bihan";
		//char c = 'á';
		//System.out.println(line2);

		//System.out.println(simplify.simplifyName(line2));

		try {
			BufferedReader reader = new BufferedReader (new FileReader("output.csv")); //backup version of this is "list-of-players2.csv"
			String line;

			while ((line = reader.readLine()) != null) {
				
				//System.out.println(line);
//				byte ptext[] = line.getBytes(ISO_8859_1);
//				String newline = new String(ptext, UTF_8);
				
				//System.out.println(newline);

				String[] s = line.split(",");
				for (String a : s) {
					//spaces are so that we actually find "Can" (on a word boundary) instead of Lezcano, for instance
					if (postDescription.contains(" " + a + " ") || postDescription.contains(" " + simplify.simplifyName(a) + " ")) { //either ascii > 127 name or simplified name in play description? if yes...
						//logger.info("Player found inside csv: " + s[0]);
						//return s[0]; 
						//System.out.println(s[0]);
					}
				}
			}

			reader.close();

		} catch (Exception e) {
			//logger.error("Error trying to read player file");
		}

		//		CrawlerThread t = new CrawlerThread();
		//		t.run();

	}
}
