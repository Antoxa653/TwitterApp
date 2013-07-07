package twitter.app;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

import twitter4j.DirectMessage;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.internal.logging.Logger;

public class UserDirectMessage{
	private Twitter twitter;
	private LinkedList<RecievedMessage> recieved = new LinkedList<RecievedMessage>();
	private LinkedList<SentMessage> sent = new LinkedList<SentMessage>();
	public static final Logger LOG = Logger.getLogger(UserDirectMessage.class);
	UserDirectMessage(Twitter t){
		this.twitter = t;
		setRecieved();
		setSent();
	}
	
	public boolean sentDirectMessageTo(String name, String text){
		boolean complit = true;
		try {
			DirectMessage message = twitter.sendDirectMessage(twitter.showUser(name).getId(), text);			
		}
		catch (TwitterException e) {
			if(e.getStatusCode() == 403) LOG.warn("You cannot send messages to users who are not following you");
			else e.printStackTrace();
			complit = false;
		}
		return complit;
	}
	
	public LinkedList<RecievedMessage> setRecieved(){		
		try {
			ResponseList<DirectMessage> recievedMessages = twitter.getDirectMessages();
			for(DirectMessage m : recievedMessages ){
				recieved.add(new RecievedMessage(m.getId(), m.getSenderScreenName(), m.getText(), m.getCreatedAt()));				
			}
			LOG.info("Recieved Messages update");
		}
		catch (TwitterException e) {
			LOG.warn("Twitter Exception"+e.getStatusCode());
			e.printStackTrace();
		}
		return recieved;
	}		
	
	public LinkedList<SentMessage> setSent(){
		try{
			ResponseList<DirectMessage> sentMessages = twitter.getSentDirectMessages();
			for(DirectMessage m : sentMessages ){
				sent.add(new SentMessage(m.getId(), m.getRecipientScreenName(), m.getText(), m.getCreatedAt()));				
			}
			LOG.info("Sent Messages update");
		}
		catch (TwitterException e) {
			LOG.warn("Twitter Exception"+e.getStatusCode());
			e.printStackTrace();
		}
		return sent;
	}
	
	public LinkedList<RecievedMessage> getRecieved(){		
		return recieved;
	}
	
	public LinkedList<SentMessage> getSent(){		
		return sent;
	}	
	
	public LinkedHashSet<String> conversationsList(){
		LinkedHashSet<String> list = new LinkedHashSet<String>();		
		for(RecievedMessage rm : recieved){
			list.add(rm.getSenderName());
		}
		for(SentMessage sm : sent){
			list.add(sm.getRecipientName());
		}
		return list;		
	}
	
	public LinkedList<Conversation> getConversationMessages(String name){
		LinkedList<Conversation> conv = new LinkedList<Conversation>();		
		for(RecievedMessage rm : recieved){
			if(rm.getSenderName().equals(name)){
				conv.add(new Conversation(rm.getDate(), rm.getText(), false));
				
			}
		}		
		for(SentMessage sm : sent){
			if(sm.getRecipientName().equals(name)){
				conv.add(new Conversation(sm.getDate(), sm.getText(), true));
				
			}
		}
		
		Collections.sort(conv, new Comparator<Conversation>(){

			@Override
			public int compare(Conversation o1, Conversation o2) {
				if(o1.getDate()==o2.getDate())
					return 0;
				return o1.getDate().getTime() < o2.getDate().getTime() ? -1 : 1;
			}});
		return conv;
	}
}

class RecievedMessage{
	private String text;
	private long id;
	private String senderName;
	private Date date;
	public RecievedMessage(long id, String senderName, String text, Date date ){
		this.text = text;
		this.id = id;
		this.senderName = senderName;
		this.date = date;
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
	public Date getDate(){
		return date;
	}
	
}

class SentMessage{
	private String text;
	private long id;
	private String recipientName;
	private Date date;
	public SentMessage(long id, String recipientName, String text, Date date ){
		this.text = text;
		this.id = id;
		this.recipientName = recipientName;
		this.date = date;
	}
	public String getText(){
		return text;
	}
	public long getId(){
		return id;
	}
	public String getRecipientName(){
		return recipientName;
	}
	public Date getDate(){
		return date;
	}
}

class Conversation{
	private Date date;
	private String text;
	private boolean sent; // true - send message, false - received message
	Conversation(Date date, String text, boolean sent){
		this.date = date;
		this.text = text;
		this.sent = sent;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isSent() {
		return sent;
	}
	public void setSent(boolean sent) {
		this.sent = sent;
	}
	
}