package twitter.app;

import java.util.List;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class FriendList {
	private Twitter twitter;
	private List<Friend> friendList;
	public FriendList(){
		this.twitter = TwitterFactory.getSingleton();
	}
	
	private void setFriendList() throws TwitterException {
		long cursor = -1;
		IDs friendsIDs = twitter.getFriendsIDs(cursor);		
		do{		
			for(long i : friendsIDs.getIDs()){
					friendList.add(new Friend(i, twitter.showUser(i).getName()));
					System.out.println(i + twitter.showUser(i).getName());
			}
		}
		while(friendsIDs.hasNext());	
	}
	
	public List<Friend> getFriendList() throws TwitterException{
		setFriendList();
		return friendList;
	}
}

class Friend{
	private long id;
	private String name;
	public Friend(long id, String name){
		this.id = id;
		this.name = name;
	}
	public long getID(){
		return id;
	}
	public String getName(){
		return name;
	}
}
