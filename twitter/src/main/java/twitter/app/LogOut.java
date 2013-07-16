package twitter.app;

import java.io.File;

import twitter4j.internal.logging.Logger;

public class LogOut {
	private Logger LOG = Logger.getLogger(getClass());

	public void doLogout() {
		File f = new File("FriendList.txt");
		File s = new File("twitter4j.properties");
		if (f.exists() & s.exists()) {
			f.delete();
			s.delete();
			System.exit(0);
		}
		else {
			LOG.debug("FriendList.txt and twitter4j.properties files not exist - can't proccess logout correctly");
			System.exit(0);
		}
	}
}
