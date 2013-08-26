package twitter.app;

import java.io.File;

public class ResourceFilesPath {
	private String twitter4jPropertiesFile = System.getProperty("user.home")
			+ File.separator + "TwitterApplication" + File.separator + "twitter4j.properties";
	private String timelineFile = System.getProperty("user.home") + File.separator + "TwitterApplication"
			+ File.separator + "TimeLine.xml";
	private String sentMessagesFile = System.getProperty("user.home") + File.separator + "/TwitterApplication"
			+ File.separator + "SentMessages.txt";
	private String recievedMessagesFile = System.getProperty("user.home")
			+ File.separator + "/TwitterApplication" + File.separator + "RecievedMessages.txt";
	private String friendlistFile = System.getProperty("user.home") + File.separator + "/TwitterApplication"
			+ File.separator + "FriendList.txt";

	ResourceFilesPath() {

	}

	ResourceFilesPath(String aTwitter4jProerptiesFile, String aTimelineFile, String aSentMessagesFile,
			String aRecievedMessagesFile, String aFriendlistFile) {
		this.twitter4jPropertiesFile = aTwitter4jProerptiesFile;
		this.timelineFile = aTimelineFile;
		this.sentMessagesFile = aSentMessagesFile;
		this.recievedMessagesFile = aRecievedMessagesFile;
		this.friendlistFile = aFriendlistFile;
	}

	public String getTwitter4jProerptiesFile() {
		return twitter4jPropertiesFile;
	}

	public String getTimelineFile() {
		return timelineFile;
	}

	public String getSentMessagesFile() {
		return sentMessagesFile;
	}

	public String getRecievedMessagesFile() {
		return recievedMessagesFile;
	}

	public String getFriendlistFile() {
		return friendlistFile;
	}

}
