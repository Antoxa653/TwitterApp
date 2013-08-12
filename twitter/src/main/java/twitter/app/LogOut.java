package twitter.app;

import twitter4j.internal.logging.Logger;

public class LogOut {
	private Logger log = Logger.getLogger(getClass());

	public void doLogout() {
		ResourceFilesChecker resource = new ResourceFilesChecker();
		if (resource.isTwitterPropertiesFileExist() & resource.isFriendListFileExist()) {
			resource.twitterPropertiesFileDelete();
			resource.friendListFileDelete();
			if (resource.isSentMessagesFileExist() & resource.isRecievedMessagesFileExist() & resource.isTimeLineFileExist()) {
				resource.sentMessagesFileDelete();
				resource.recievedMessagesFileDelete();
				resource.timeLineFileDelete();
			}
			System.exit(0);
		}

		else {
			log.debug("FriendList.txt and twitter4j.properties files not exist - can't proccess logout correctly");
			System.exit(1);
		}
	}
}
