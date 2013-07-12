package twitter.app;

import java.io.File;

import org.apache.log4j.Logger;

public class FriendListFileExist {
	public static final Logger LOG = Logger.getLogger(PropertiesExist.class);
	public boolean isFriendListFileExist() {
		File file = new File("FriendList.txt");
		boolean exist = file.exists();
		if (exist) {
			LOG.info("FriendList file exist");
		}
		if (!exist) {
			LOG.info("FriendList file not exist");
		}
		return exist;
	}
}
