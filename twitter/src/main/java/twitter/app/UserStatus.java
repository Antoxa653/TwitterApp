package twitter.app;



import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.internal.logging.Logger;

public class UserStatus {
	private Twitter twitter;
	public static final Logger LOG = Logger.getLogger(UserStatus.class);
	UserStatus(Twitter twitter){
		this.twitter = twitter;
	}
	
	public boolean update(String newStatus){
		boolean complit = true;
		try {
			twitter.updateStatus(newStatus);
		} catch (TwitterException e) {
			LOG.warn("Twitter exception" + e.getStatusCode());
			e.printStackTrace();
			complit = false;
		}
		return complit;
	}
	
}
