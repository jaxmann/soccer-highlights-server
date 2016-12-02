import java.util.ArrayList;
import java.util.HashSet;

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

	public static void figureOutWhoToSendItTo(ArrayList<User> users, HashSet<String> hashSet) {
		//given the associated keywords with the video, see if they match "tags" that one can subscribe to (either team name/players)
		//and only send the message to people who are subscribed
		//pass on to "sendAlert" method
		for (int i=0; i<users.size(); i ++) { //for each user...
			//if tags match...
			for (int k=0; k<users.get(i).getTags().size(); k++) { //for each tag within each user
				for (int j=0; j<hashSet.size(); j++ ) { //check if the user has a tag that matches a tag in the play, if so, send an alert with the url to the play
					if (users.get(i).getTags().get(k).contains(hashSet.get(j))) {
						users.get(i).sendAlert(url);
					}
				}
			}
		}
	}


}
