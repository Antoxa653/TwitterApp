package twitter.app;

import java.util.ArrayList;
import java.util.List;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class FriendList {
	private Twitter twitter;
	private List<Friend> friendList = new ArrayList<Friend>();
	public FriendList(){
		this.twitter = TwitterFactory.getSingleton();
	}
	
	private void setFriendList(){
		try{
			long cursor = -1;		
			IDs friendsIDs = twitter.getFriendsIDs(cursor);
			do{						
				for(long id : friendsIDs.getIDs()){																		
					friendList.add(new Friend(id, twitter.showUser(id).getName()));					
				}
			}
			while(friendsIDs.hasNext());
			}
			catch(TwitterException te){
				te.printStackTrace();
			}
	}
	
	
	public List<Friend> getFriendList() throws TwitterException{
		setFriendList();
		return friendList;
	}
}

class Friend{
	private long id;
	private String name;
	Friend(long id, String name){
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
