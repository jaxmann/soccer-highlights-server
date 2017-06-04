package serv;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.regex.Matcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MLSCrawler {

	public static void main(String[] args) {
		
		String[] tms = {"Atlanta_United_FC","Chicago_Fire_Soccer_Club","Columbus_Crew_SC","D.C._United", "Montreal_Impact","New_England_Revolution",
				"New_York_City_FC","New_York_Red_Bulls","Orlando_City_SC","Philadelphia_Union","Toronto_FC",
				"Colorado_Rapids","FC_Dallas","Houston_Dynamo","LA_Galaxy","Minnesota_United_FC","Portland_Timbers",
				"Real_Salt_Lake","San_Jose_Earthquakes","Seattle_Sounders_FC","Sporting_Kansas_City","Vancouver_Whitecaps_FC"
		};
		
		for (String s : tms) {
			start(s);
		}
		
		//start("New_York_City_FC");

	}
	
	public static void start(String team) {
				
		String mlsURL = "https://en.wikipedia.org/wiki/" + team;
		
		HashSet<MLSPlayer> mlsPlayers = new HashSet<MLSPlayer>();
		
		try {
			Document document = Jsoup.connect(mlsURL).followRedirects(true).get(); 
			Elements players = document.select("tr[class~=vcard agent]"); //> a[title]:matches(United|Chicago|SC|Impact|Revolution|FC|Red Bulls|Union|Rapids|Dynamo|Galaxy|Portland|Real Salt|San Jose|Sounders|Kansas City|Vancouver)"); 
			
			
			for (Element thisTd : players) {
				Elements tds = thisTd.select("td");
				String playerName = null;
				String playerCountry = null;
				String playerTeam = null;
				for (Element td : tds) {
					if (td.select("td > span > span > span > a").attr("title").length() > 1) {
						playerName = td.select("td > span > span.vcard > span > a").attr("title").replaceAll("\\(.*\\)", "").trim();
					} else if (td.select("td > span > a").attr("title").length() > 1) {
						playerName = td.select("td > span > a").attr("title").replaceAll("\\(.*\\)", "").trim();
					}
					if (!td.select("td[style~=text-align] > a").attr("href").contains("association")) {
						playerCountry = td.select("td[style~=text-align] > a").text();
					} 
					playerTeam = team.replaceAll("_", " ");
				}
				MLSPlayer newPlayer = new MLSPlayer(playerName, playerTeam, playerCountry);
				mlsPlayers.add(newPlayer);			
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (MLSPlayer p : mlsPlayers) {
			System.out.println(" Major League Soccer (MLS), " + p.getTeam() + ", " + p.getName() + ", " + p.getCountry());
		}
		
	}

}
