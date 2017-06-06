package serv;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

import static java.nio.charset.StandardCharsets.UTF_8;

public class main {

	public static final boolean DEBUG = true;


	public static void main(String[] args) {


		CrawlerThread t = new CrawlerThread(args[0]);
		t.run();


	}
}
