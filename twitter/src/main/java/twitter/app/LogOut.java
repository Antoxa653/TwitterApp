package twitter.app;

import org.apache.log4j.Logger;

public class LogOut {
	private static Logger log = Logger.getLogger(LogOut.class.getName());

	LogOut() {

	}

	public static void doLogout() {
		ResourceFilesPath filesPath = new ResourceFilesPath();
		if (ResourceFilesChecker.isFileExist(filesPath.getTwitter4jProerptiesFile())) {
			ResourceFilesChecker.deleteFile(filesPath.getTwitter4jProerptiesFile());

			if (ResourceFilesChecker.isFileExist(filesPath.getFriendlistFile())) {
				ResourceFilesChecker.deleteFile(filesPath.getFriendlistFile());
			}
			if (ResourceFilesChecker.isFileExist(filesPath.getTimelineFile())) {
				ResourceFilesChecker.deleteFile(filesPath.getTimelineFile());
			}
			if (ResourceFilesChecker.isFileExist(filesPath.getSentMessagesFile())) {
				ResourceFilesChecker.deleteFile(filesPath.getSentMessagesFile());
			}
			if (ResourceFilesChecker.isFileExist(filesPath.getRecievedMessagesFile())) {
				ResourceFilesChecker.deleteFile(filesPath.getRecievedMessagesFile());
			}
			System.exit(0);
		}

		else {
			log.debug("twitter4j.properties files not exist - can't proccess logout correctly");
			System.exit(1);
		}
	}
}
