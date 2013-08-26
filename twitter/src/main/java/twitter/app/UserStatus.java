package twitter.app;

import org.apache.log4j.Logger;

import twitter4j.Twitter;
import twitter4j.TwitterException;

public class UserStatus {
	private Logger log = Logger.getLogger(getClass().getName());
	private Twitter twitter;

	UserStatus(Twitter twitter) {
		this.twitter = twitter;
	}

	public boolean update(String newStatus) {
		boolean complit = true;
		try {
			twitter.updateStatus(newStatus);
		} catch (TwitterException e) {
			log.error("Twitter exception" + e.getStatusCode() + " " + e);
			e.printStackTrace();
			complit = false;
		}
		return complit;
	}

}
