package serv;


import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {

	public static void main(String[] args) {
		
		String postDescription = "Juventus 1-[1] Real Madrid ( Mandzukic M. 27')";
		
		Pattern p = Pattern.compile("[\\[|(]?[0-9][\\]|)]?-[\\[|(]?[0-9][\\]|)]?"); //does the link text have something like (2-0) displaying the score of a game ^[0-9]+(-[0-9]+)
	
		Matcher m = p.matcher(postDescription);
		
		if (m.find()) { 
			String score = postDescription.substring(m.start(), m.end());
			System.out.println(score.replaceAll("\\(|\\)|\\[|\\]|\\{|\\}", ""));
		}
		
		

	}
	

}


