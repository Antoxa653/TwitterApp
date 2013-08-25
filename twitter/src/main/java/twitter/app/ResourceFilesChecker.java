package twitter.app;

import java.io.File;

import org.apache.log4j.Logger;

public class ResourceFilesChecker {
	private final String friendListFileLocation = System.getProperty("user.home") + "/TwitterApplication"
			+ "/FriendList.txt";
	private final String twitter4jPropertiesFileLocation = System.getProperty("user.home") + "/TwitterApplication"
			+ "/twitter4j.properties";
	private final String sentMessagesFileLocaion = System.getProperty("user.home") + "/TwitterApplication"
			+ "/SentMessages.txt";
	private final String recievedMessagesFileLocation = System.getProperty("user.home") + "/TwitterApplication"
			+ "/RecievedMessages.txt";
	private final String timeLineFileLocation = System.getProperty("user.home") + "/TwitterApplication"
			+ "/TimeLine.xml";
	private Logger log = Logger.getLogger(getClass());
	private File friendListFile;
	private File twitterPropertiesFile;
	private File sentMessagesFile;
	private File recievedMessagesFile;
	private File timeLineFile;

	public boolean isFriendListFileExist() {
		friendListFile = new File(friendListFileLocation);
		boolean exist = friendListFile.exists();
		if (exist) {
			log.debug("FriendList.txt file exist");
		}
		else {
			log.debug("FriendList.txt file not exist");
		}
		return exist;
	}

	public boolean isTwitterPropertiesFileExist() {
		twitterPropertiesFile = new File(twitter4jPropertiesFileLocation);
		boolean exist = twitterPropertiesFile.exists();
		if (exist) {
			log.debug("twitter4j.properties file exist");
		}
		else {
			log.debug("twitter4j.properties file not exist");
		}
		return exist;
	}

	public boolean isSentMessagesFileExist() {
		sentMessagesFile = new File(sentMessagesFileLocaion);
		boolean exist = sentMessagesFile.exists();
		if (exist) {
			log.debug("SentMessages.txt file exist");
		}
		else {
			log.debug("SentMessages.txt file not exist");
		}
		return exist;
	}

	public boolean isRecievedMessagesFileExist() {
		recievedMessagesFile = new File(recievedMessagesFileLocation);
		boolean exist = recievedMessagesFile.exists();
		if (exist) {
			log.debug("RecievedMessages.txt file exist");
		}
		else {
			log.debug("RecievedMessages.txt file not exist");
		}
		return exist;
	}

	public boolean isTimeLineFileExist() {
		timeLineFile = new File(timeLineFileLocation);
		boolean exist = timeLineFile.exists();
		if (exist) {
			log.debug("TimeLine.txt file exist");
		}
		else {
			log.debug("TimeLine.txt file not exist");
		}
		return exist;
	}

	public void friendListFileDelete() {
		if (isFriendListFileExist()) {
			friendListFile.delete();
			log.debug("FriendList.txt file delete");
		}
		else {
			log.debug("FriendList.txt file not exist");
		}
	}

	public void twitterPropertiesFileDelete() {
		if (isTwitterPropertiesFileExist()) {
			twitterPropertiesFile.delete();
			log.debug("twitter4j.properties file delete");
		}
		else {
			log.debug("twitter4j.properties file not exist");
		}

	}

	public void sentMessagesFileDelete() {
		if (isSentMessagesFileExist()) {
			sentMessagesFile.delete();
			log.debug("SentMessages.txt file delete");
		}
		else {
			log.debug("SentMessages.txt file not exist");
		}

	}

	public void recievedMessagesFileDelete() {
		if (isRecievedMessagesFileExist()) {
			recievedMessagesFile.delete();
			log.debug("RecievedMessages.txt file delete");
		}
		else {
			log.debug("RecievedMessages.txt file not exist");
		}

	}

	public void timeLineFileDelete() {
		if (isTimeLineFileExist()) {
			timeLineFile.delete();
			log.debug("TimeLine.txt file delete");
		}
		else {
			log.debug("TimeLine.txt file not exist");
		}

	}

}
