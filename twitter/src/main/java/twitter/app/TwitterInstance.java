package twitter.app;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;

public class TwitterInstance {
	final private Twitter twitter;
	TwitterInstance(){
		twitter = TwitterFactory.getSingleton();		
	}
	public Twitter getTwitter() {
		return twitter;
	}
	
}
