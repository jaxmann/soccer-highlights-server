import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class main {

	public static final boolean DEBUG = true;
	

	public static void main(String[] args) {
		
		CrawlerThread t = new CrawlerThread();
		t.run();
	}


	public static int[] regexBuildScore(String text, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		String matchingString = text.substring(matcher.start(), matcher.end());
		Scanner s = new Scanner(matchingString);
		int[] scoreArr = {s.nextInt(), s.nextInt()};
		return scoreArr;

	}

}
