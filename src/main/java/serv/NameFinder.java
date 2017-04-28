package serv;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;


//finds players who have a last name that is also a first name
public class NameFinder {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		HashSet<String> myLastNames = new HashSet<String>();
		HashSet<String> myFirstNames = new HashSet<String>();

		try {
			BufferedReader reader = new BufferedReader (new FileReader("list-of-players2.csv"));
			String line;

			while ((line = reader.readLine()) != null) {
				String[] arr = line.split(",");
				//System.out.println(arr[0]);
				String[] name = arr[0].split(" ");
				if (name.length == 2) {
					//System.out.println(name[1]);
					myLastNames.add(name[1]);
					myFirstNames.add(name[0]);
					
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
		
		Iterator iter = myLastNames.iterator();
		while (iter.hasNext()) {
			String l = iter.next().toString();
			if (myFirstNames.contains(l)) {
				System.out.println(l);
			}
		}

	}

}
