package twitter.app;

import java.io.File;

import org.apache.log4j.Logger;

public class FriendListFileExist {
	private Logger LOG = Logger.getLogger(getClass());

	public boolean isFriendListFileExist() {
		File file = new File("FriendList.txt");
		boolean exist = file.exists();
		if (exist) {
			LOG.debug("FriendList file exist");
		}
		else {
			LOG.debug("FriendList file not exist");
		}
		return exist;
	}
}
