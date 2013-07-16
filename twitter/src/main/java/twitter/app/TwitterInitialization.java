package twitter.app;

import twitter4j.Twitter;

class TwitterInitialization {
	private FriendList fl;
	private UserDirectMessage udm;
	private UserStatus us;
	private TimeLine tlu;
	private Twitter twitter;

	TwitterInitialization(Twitter t) {
		if (t != null) {
			this.twitter = t;
			fl = new FriendList(twitter);
			udm = new UserDirectMessage(twitter);
			us = new UserStatus(twitter);
			tlu = new TimeLine(twitter);
		}
		else {
			throw new IllegalArgumentException("Parametr t should not be null or empty. Current value is " + t);
		}
	}

	public FriendList getFl() {
		return fl;
	}

	public UserDirectMessage getUdm() {
		return udm;
	}

	public UserStatus getUs() {
		return us;
	}

	public Twitter getTwitter() {
		return twitter;
	}

	public TimeLine getTlu() {
		return tlu;
	}

}
