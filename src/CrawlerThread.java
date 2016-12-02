import java.util.ArrayList;

public class CrawlerThread implements Runnable {

	private Play play;
	private static String url;

	public CrawlerThread(Play play) {
		this.play = play;
	}

	@Override
	public void run() {
		if (main.DEBUG) {
			System.out.println("do some operations here");
		}
		url = play.crawlSites();
		//crawl sites with the given play
		figureOutWhoToSendItTo(main.users, play.getKeywords()); //also calls send method

	}

	public static void figureOutWhoToSendItTo(ArrayList<User> users, ArrayList<String> keywords) {
		//given the associated keywords with the video, see if they match "tags" that one can subscribe to (either team name/players)
		//and only send the message to people who are subscribed
		//pass on to "sendAlert" method
		for (User u : users) {
			//if tags match...
			u.sendAlert(url);
		}
	}


}
