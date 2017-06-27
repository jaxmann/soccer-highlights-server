package builder;

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

public class BrasilCrawler {

	public static void main(String[] args) {
		
		String[] tms = {"Sociedade_Esportiva_Palmeiras","Santos_FC","São_Paulo_FC","Sport_Club_Corinthians_Paulista","Clube_de_Regatas_do_Flamengo","Cruzeiro_Esporte_Clube",
				"CR_Vasco_da_Gama","Fluminense_FC","Sport_Club_Internacional","Botafogo_de_Futebol_e_Regatas","Grêmio_Foot-Ball_Porto_Alegrense","Esporte_Clube_Bahia",
				"Clube_Atlético_Mineiro","Guarani_FC","Clube_Atlético_Paranaense","Sport_Club_do_Recife","Coritiba_Foot_Ball_Club"
		};
		
		for (String s : tms) {
			start(s);
		}
		
		//start("Guarani_FC");

	}
	
	public static void start(String team) {
				
		String url = "https://en.wikipedia.org/wiki/" + team;
		
		HashSet<Player> mlsPlayers = new HashSet<Player>();
		
		try {
			Document document = Jsoup.connect(url).followRedirects(true).get(); 
			Elements players = document.select("tr[class~=vcard agent]"); //> a[title]:matches(United|Chicago|SC|Impact|Revolution|FC|Red Bulls|Union|Rapids|Dynamo|Galaxy|Portland|Real Salt|San Jose|Sounders|Kansas City|Vancouver)"); 
			
			
			for (Element thisTd : players) {
				Elements tds = thisTd.select("td");
				String playerName = null;
				String playerCountry = null;
				String playerTeam = null;
				boolean containsPlayerNumber = false;
				for (Element td : tds) {
					if (td.select("td[style~=text-align]").text().matches("\\d+")) {
						containsPlayerNumber = true;
					}

					if (td.select("td > span > span > span > a").attr("title").length() > 1) {
						playerName = td.select("td > span > span.vcard > span > a").attr("title").replaceAll("\\(.*\\)", "").trim();
					} else if (td.select("td > span.fn").hasText()) {
						if (td.select("td > span.fn > a").attr("title").length() > 1) {
							playerName = td.select("td > span.fn > a").attr("title").replaceAll("\\(.*\\)", "").trim();
						} else {
							playerName = td.select("td > span.fn").text();
						}
					}
					if (!td.select("td[style~=text-align] > span > a > img").attr("alt").isEmpty()) {
						playerCountry = td.select("td[style~=text-align] > span > a").attr("title");
					}
					
					playerTeam = team.replaceAll("_", " ").replaceAll("\\.", "");
				}
				if (containsPlayerNumber) {
					Player newPlayer = new Player(playerName.replaceAll(",", ""), playerTeam, playerCountry);
					mlsPlayers.add(newPlayer);			
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (Player p : mlsPlayers) {
			System.out.println(" Campeonato Brasileiro Série A, " + p.getTeam() + ", " + p.getName() + ", " + p.getCountry());
		}
		
		
		
	}

}
