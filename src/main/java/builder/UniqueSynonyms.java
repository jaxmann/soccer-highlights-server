package builder;


import java.io.BufferedReader;
import static java.nio.charset.StandardCharsets.ISO_8859_1;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class UniqueSynonyms {

	public static void main(String[] args) {

		//HashMap<String, Integer> myLastNames = new HashMap<String, Integer>();
		
		////////////////////////////////////////////////////
		//redirect output to file 'synsTable.csv' inside regenerate-players folder
		///////////////////////////////

		try {
			BufferedReader reader = new BufferedReader (new FileReader("regenerate-players//fullTable.csv"));
			String line;


			while ((line = reader.readLine()) != null) {
				String[] arr = line.split(",");
				String name = arr[2].trim();
				byte pFull[] = name.getBytes("Windows-1252");
				String fullName = new String(pFull, UTF_8);
				boolean hasIllegalChars = false;

				for (int i=0; i<fullName.length(); i++) {
					if (fullName.charAt(i) == '?') {
						hasIllegalChars = true;
						break;
					}
				}

				if (!hasIllegalChars) {
					if (name.split(" ").length == 2) {
						byte p0[] = name.split(" ")[0].getBytes("Windows-1252");
						String n0 = new String(p0, UTF_8);
						byte p1[] = name.split(" ")[1].getBytes("Windows-1252");
						String n1 = new String(p1, UTF_8);
						String firstSpaceLast = n0.substring(0,1) + " " + n1;
						String firstDotLast = n0.substring(0,1) + ". " + n1;
						String firstLastDot = n0 + " " + n1.substring(0,1) + ".";
						String last = n1;
						System.out.println(fullName + "," + firstSpaceLast + "," + firstDotLast + "," + firstLastDot + "," + last);
					} else if (name.split(" ").length == 1) {
//						byte p2[] = name.split(" ")[0].getBytes("Windows-1252");
//						String n2 = new String(p2, UTF_8);
						System.out.println(fullName);
					} else if (name.split(" ").length == 3) {
						byte p3[] = name.split(" ")[0].getBytes("Windows-1252");
						String n3 = new String(p3, UTF_8);
						byte p4[] = name.split(" ")[1].getBytes("Windows-1252");
						String n4 = new String(p4, UTF_8);
						byte p5[] = name.split(" ")[2].getBytes("Windows-1252");
						String n5 = new String(p5, UTF_8);
						String firstSpaceLast = n3.substring(0,1) + " " + n4 + " " + n5;
						String firstDotLast = n3.substring(0,1) + ". " + n4 + " " +n5;
						String firstLastDot = n3 + " " + n4 + " " + n3.substring(0,1) + ".";
						String last = n4 + " " + n5;
						String initials = n3.substring(0,1) + n4.substring(0,1) + n5.substring(0,1);
						System.out.println(fullName + "," + firstSpaceLast + "," + firstDotLast + "," + firstLastDot + "," + last + ", " + initials);
					}
				}
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		///////////////////////////////////////////////////////////////////////////////////////////////
		/*try {
			BufferedReader reader2 = new BufferedReader (new FileReader("db//2bundes.csv"));
			String line;

			while ((line = reader2.readLine()) != null) {

				String firstDotLast = "";
				String firstLast = "";
				String last = "";

				String[] arr = line.split(",");

				byte ptext[] = line.getBytes(ISO_8859_1);
				String newline = new String(ptext, UTF_8);

				String firstChunk = newline + ",";

				if (line.indexOf(",,") > 0) {
					firstChunk = line.substring(0, line.indexOf(",,") + 1);
				}

				byte ptext2[] = arr[0].getBytes(ISO_8859_1);
				String newline2 = new String(ptext2, UTF_8);

				String[] name = newline2.split(" ");
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
		}*/



	}

}


