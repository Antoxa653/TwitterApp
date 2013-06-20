package twitter.app;

import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class UserDirectMessage {
	private Twitter twitter;
	private List<Message> messages;	
	public UserDirectMessage(){
		twitter = TwitterFactory.getSingleton();		
	}
	
	public void sendDirectMessage(long ID, String text) throws TwitterException{		
		DirectMessage massage = twitter.sendDirectMessage(ID, text);
	}
	
	private void listOfDirectMessages() throws TwitterException{
		ResponseList<DirectMessage> responseList = twitter.getDirectMessages();		
		for(DirectMessage m : responseList ){
			messages.add(new Message(m.getId(), m.getSenderScreenName(), m.getText()));
		}
	}
	
	public List<Message> getListOfDirectMessages() throws TwitterException{
		listOfDirectMessages();
		return messages;
	}
			
}

class Message{
	private String text;
	private long id;
	private String senderName;
	public Message(long id, String senderName, String text ){
		this.text = text;
		this.id = id;
		this.senderName = senderName;
	}
	public String getText(){
		return text;
	}
	public long getId(){
		return id;
	}
	public String getSenderName(){
		return senderName;
	}
	
}