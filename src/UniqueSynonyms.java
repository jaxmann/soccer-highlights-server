

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class UniqueSynonyms {

	public static void main(String[] args) {
		
		HashMap<String, Integer> myLastNames = new HashMap<String, Integer>();

		try {
			BufferedReader reader = new BufferedReader (new FileReader("list-of-players2.csv"));
			String line;

			while ((line = reader.readLine()) != null) {
				String[] arr = line.split(",");
				//System.out.println(arr[0]);
				String[] name = arr[0].split(" ");
				if (name.length == 2) {
					System.out.println(name[1]);
					if (myLastNames.containsKey(name[1])) {
						myLastNames.put(name[1], myLastNames.get(name[1]) + 1);
					} else {
						myLastNames.put(name[1], 1);
					}
					
				} else {
					
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		///////////////////////////////////////////////////////////////////////////////////////////////
		try {
			BufferedReader reader2 = new BufferedReader (new FileReader("list-of-players2.csv"));
			String line;

			while ((line = reader2.readLine()) != null) {
				
				String firstDotLast = "";
				String firstLast = "";
				String last = "";
				
				String[] arr = line.split(",");
				
				String firstChunk = line;
				
				if (line.indexOf(",,") > 0) {
					firstChunk = line.substring(0, line.indexOf(",,") + 1);
				}
				
				
				
				String[] name = arr[0].split(" ");
				if (name.length == 2) {
					if (myLastNames.containsKey(name[1])) {
						if (myLastNames.get(name[1]) == 1) {
							firstDotLast = name[0].substring(0, 1) + ". " + name[1];
							firstLast = name[0].substring(0, 1) + " " + name[1];
							last = name[1];
							String fullLine = firstChunk + firstDotLast + "," + firstLast + "," + last;
							int commaCount = fullLine.length() - fullLine.replace(",", "").length();
							for (int i=commaCount; i<=5; i++) {
								fullLine += ",";
							}
							System.out.println(fullLine);
						}
					}
					
				} else {
					
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		


	}

}


