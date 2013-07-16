package twitter.app;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.internal.logging.Logger;

public class UserStatus {
	public static final Logger LOG = Logger.getLogger(UserStatus.class);
	private Twitter twitter;
	UserStatus(Twitter twitter) {
		this.twitter = twitter;
	}

	public boolean update(String newStatus) {
		boolean complit = true;
		try {
			twitter.updateStatus(newStatus);
		} catch (TwitterException e) {
			LOG.error("Twitter exception" + e.getStatusCode() + " " + e);
			e.printStackTrace();
			complit = false;
		}
		return complit;
	}

}
