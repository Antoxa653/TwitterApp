package twitter.app;

import twitter4j.Twitter;

class TwitterInitialization {
	private FriendList friendList;
	private UserDirectMessage userDirectMessage;
	private UserStatus userStatus;
	private TimeLine timeLine;
	private Twitter twitter;

	TwitterInitialization(Twitter t) {
		if (t != null) {
			this.twitter = t;
			friendList = new FriendList(twitter);
			userDirectMessage = new UserDirectMessage(twitter);
			userStatus = new UserStatus(twitter);
			timeLine = new TimeLine(twitter);
		}
		else {
			throw new IllegalArgumentException("Parametr t should not be null or empty. Current value is " + t);
		}
	}

	public FriendList getFl() {
		return friendList;
	}

	public UserDirectMessage getUdm() {
		return userDirectMessage;
	}

	public UserStatus getUs() {
		return userStatus;
	}

	public Twitter getTwitter() {
		return twitter;
	}

	public TimeLine getTlu() {
		return timeLine;
	}

}
