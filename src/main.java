import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class main {

	public static final boolean DEBUG = true;
	

	public static void main(String[] args) {
		
		CrawlerThread t = new CrawlerThread();
		t.run();
	}

}
