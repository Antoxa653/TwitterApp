package twitter.app;



import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class UserStatus {
	Twitter twitter;
	UserStatus(){
		this.twitter = TwitterFactory.getSingleton();
	}
	
	public void update(String newStatus) throws TwitterException{
		twitter.updateStatus(newStatus);
	}
	
}
