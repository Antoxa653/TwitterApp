package twitter.app;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

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
	private LinkedList<Conversation> conv = new LinkedList<Conversation>();
	private RateLimitation rl;
	UserDirectMessage(Twitter t){
		this.twitter = t;
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
		rl = new RateLimitation(twitter);
		int rateLimit = rl.checkLimitStatusForEndpoint("/direct_messages");
		if(rateLimit >= 2){
			try {
				recieved.clear();
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
		}
		if(rateLimit == 2){
			LOG.info("RecievedMessages file created");
			printRecieved();
		}
		if(rateLimit <= 1){
			LOG.info("RecievedMessages file readeding...");
			readRecieved();
		}
		
		return recieved;
	}		
	
	public LinkedList<SentMessage> setSent(){
		rl = new RateLimitation(twitter);
		int rateLimit = rl.checkLimitStatusForEndpoint("/direct_messages/sent");
		if(rateLimit >=2){
			try{
				sent.clear();
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
		}
		if(rateLimit == 2){
			LOG.info("SentMessages file created");
			printSent();
		}
		if(rateLimit <= 1){
			LOG.info("SentMessages file reading...");
			readSent();
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
	
	public LinkedList<Conversation> setConversationMessages(String name){		
		conv.clear();
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
	
	public void printSent(){
		PrintWriter pw = null;
		try{
			pw = new PrintWriter(new FileOutputStream("SentMessages.txt"));
			for(SentMessage s : sent){
				pw.println(s.getId() + "@" + s.getRecipientName() + "(text)" + s.getText() + "(Date)" + s.getDate());
			}
			pw.close();
		}
		catch(FileNotFoundException e){
			LOG.warn("Can't create file");
		}
		
	}
	
	public void readSent(){		
		try{
			sent.clear();
			BufferedReader br = new BufferedReader(new FileReader("SentMessages.txt"));
			String line;
			while((line = br.readLine()) != null){
				line.trim();
				long id = Long.parseLong(line.substring(0, line.indexOf("@")));
				String name = line.substring(line.indexOf("@")+1, line.indexOf("(text)"));
				String text = line.substring(line.indexOf("(text)")+6, line.indexOf("(Date)"));
				String dateString = line.substring(line.indexOf("(Date)")+6, line.length());				
				DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.UK);
				
				Date convertedDate = format.parse(dateString);
				sent.add(new SentMessage(id, name, text, convertedDate));
			}
			LOG.info("SentMessages read correctly");
			br.close();
		}
		catch(FileNotFoundException e){
			LOG.warn("SentMessages file don't found");
		}
		catch(IOException i){
			LOG.warn("SentMessages file don't found");
		}
		catch(ParseException pe){
			pe.printStackTrace();
			LOG.warn("ParseException");
		}
	}
	
	public void printRecieved(){
		PrintWriter pw = null;
		try{
			pw = new PrintWriter(new FileOutputStream("RecievedMessages.txt"));
			for(RecievedMessage r : recieved){
				pw.println(r.getId() + "@" + r.getSenderName()+ "(text)" + r.getText() + "(Date)" + r.getDate());
			}
			pw.close();
		}
		catch(FileNotFoundException e){
			LOG.warn("Can't create file");
		}
		
	}
	
	public void readRecieved(){		
		try{
			recieved.clear();
			BufferedReader br = new BufferedReader(new FileReader("RecievedMessages.txt"));
			String line;
			while((line = br.readLine()) != null){
				line.trim();
				long id = Long.parseLong(line.substring(0, line.indexOf("@")));
				String name = line.substring(line.indexOf("@")+1, line.indexOf("(text)"));
				String text = line.substring(line.indexOf("(text)")+6, line.indexOf("(Date)"));
				String dateString = line.substring(line.indexOf("(Date)")+6, line.length());
				DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.UK);			
				Date convertedDate = format.parse(dateString);
				recieved.add(new RecievedMessage(id, name, text, convertedDate));
			}
			LOG.info("RecievedMessages read correctly");
			br.close();
		}
		catch(FileNotFoundException e){
			LOG.warn("RecievedMessages file don't found");
		}
		catch(IOException i){
			LOG.warn("RecievedMessages file don't found");
		}
		catch(ParseException pe){
			LOG.warn("ParseException");
		}
	}
	
}

class RecievedMessage{
	private long id;
	private String text;	
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
	private long id;
	private String text;	
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