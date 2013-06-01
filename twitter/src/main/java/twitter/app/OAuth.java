package twitter.app;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class OAuth {
	private Twitter twitter = TwitterFactory.getSingleton();
	public void authorization(String newStatus) throws TwitterException{
		Status status = twitter.updateStatus(newStatus);
	}
	public List<Status> getTimeLine() throws TwitterException{
		List<Status> statuses = twitter.getHomeTimeline();
		System.out.println("Showing home timeline.");
	    return statuses;
		
		
	}

}
