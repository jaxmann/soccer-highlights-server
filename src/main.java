import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.FileReader;

public class main {

	public static final boolean DEBUG = true;


	public static void main(String[] args) {
		
		
		CrawlerThread t = new CrawlerThread();
		t.run();
	}

}
