package twitter.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import javax.swing.DefaultListModel;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.internal.logging.Logger;

public class FriendList {
	private Twitter twitter;		
	private LinkedHashSet<Friend> friendList = new LinkedHashSet<Friend>();
	public final static Logger LOG = Logger.getLogger(FriendList.class);
	
	public FriendList(Twitter twitter){
		this.twitter = twitter;
		FriendListFileExist file = new FriendListFileExist();
		boolean exist = file.isFriendListFileExist();
		if(exist)readFriendListFile();
		if(!exist){
			createFriendList();
			createFriendListFile();
			
		}
		
	}
	
	private void createFriendList(){
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
	
	public LinkedHashSet<Friend> getFriendList(){		
		return friendList;		
	}
	
	public void deleteFriend(String name) {
		try {
			Iterator<Friend> it = friendList.iterator();
			do{
				Friend element = it.next();
				if(element.getName() == name){
					twitter.destroyFriendship(element.getID());
					friendList.remove(element);
				}
			}
			while(it.hasNext());
			File file =new File("F:\\git\\TwitterApp\\twitter\\FriendList.txt");
			file.delete();
			createFriendListFile();
		} 
		catch (TwitterException e) {			
			LOG.warn("Twitter exception");
		}
		catch(ConcurrentModificationException t){
			LOG.warn("ConcurrentModificationException occurs, but FriendListFile was create");
			createFriendListFile();
		}
		
	}
	
	public void addFriend(String name){		
		try {
			twitter.createFriendship(name);
			long id = twitter.showUser(name).getId(); 	
			friendList.add(new Friend(id, name));
			PrintWriter pw = null;
			pw = new PrintWriter(new FileOutputStream("F:\\git\\TwitterApp\\twitter\\FriendList.txt", true));
			pw.println(String.valueOf(id)+" "+name);
			pw.close();
		} catch (TwitterException e) {			
			e.printStackTrace();
		}		
		catch (FileNotFoundException e) {
			LOG.warn("FriendList file don't found");
		}
	}
	
	public void createFriendListFile()
    {
         PrintWriter pw = null;
         try {
			pw = new PrintWriter(new FileOutputStream("FriendList.txt"));
			for(Friend f : friendList){
	        	 pw.println(f.getID()+" "+f.getName());
	         }        
	         pw.close();
		} catch (FileNotFoundException e) {
			LOG.warn("Can't create file");
		}
         
     }
	public void readFriendListFile(){
		friendList = new LinkedHashSet<Friend>();
		try{
		BufferedReader br = new BufferedReader(new FileReader("F:\\git\\TwitterApp\\twitter\\FriendList.txt"));
		String line;
		while((line = br.readLine()) != null){
			line.trim();
			long id =Long.parseLong(line.substring(0, line.indexOf(" ")));
			String name = line.substring(line.indexOf(" "), line.length());
			friendList.add(new Friend(id, name));
		}
		LOG.info("Friend list read correctly");
		br.close();
		}
		catch(FileNotFoundException e){
			LOG.warn("FriendList file don't found");
		}
		catch(IOException i){
			LOG.warn("FriendList file don't found");
		}
	}
}

class Friend{
	private long id;
	private String name;
	Friend(long id, String name){
		this.id = id;
		this.name = name.trim();
	}
	public long getID(){
		return id;
	}
	public String getName(){
		return name;
	}	
	public void setId(long id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
}

