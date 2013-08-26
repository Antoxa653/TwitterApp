package twitter.app;

import twitter4j.Twitter;

class TwitterInitialization {
	private FriendList friendList;
	private UserDirectMessage userDirectMessage;
	private UserStatus userStatus;
	private HomeTimeLine timeLine;
	private Twitter twitter;

	TwitterInitialization(Twitter t) {
		if (t != null) {
			this.twitter = t;
			friendList = new FriendList(twitter);
			userStatus = new UserStatus(twitter);
			userDirectMessage = new UserDirectMessage(twitter);
			timeLine = new HomeTimeLine(twitter);
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

	public HomeTimeLine getTlu() {
		return timeLine;
	}

}
