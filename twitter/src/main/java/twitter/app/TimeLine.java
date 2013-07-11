package twitter.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.internal.logging.Logger;

public class TimeLine{
	private Twitter twitter;	
	private LinkedList<Tweets> timeLineList;
	public static final Logger LOG = Logger.getLogger(TimeLine.class);
	private RateLimitation rl;
	public TimeLine(Twitter twitter){
		this.twitter = twitter;
		timeLineList = new LinkedList<Tweets>();
		
	}
	
	public void setTimeLineList(){
		rl = new RateLimitation(twitter);
		int rateLimit = rl.checkLimitStatusForEndpoint("/statuses/home_timeline");
		if(rateLimit >= 2){
			try {
				timeLineList.clear();
				List<Status> statusList = twitter.getHomeTimeline();
				for(Status status: statusList){			 				
					timeLineList.add(new Tweets(status.getId(),status.getUser().getScreenName(),status.getText()));	
				}		
			} 
			catch (TwitterException e) {			
				LOG.warn("Error while updating timeline"+e.getStatusCode());
				//e.printStackTrace();
			}
		}
		if(rateLimit == 2){
			LOG.info("TimeLine writed in the TimeLine.txt file");
			printTimeLineList();
			
		}
		if(rateLimit <= 1){
			LOG.info("TimeLineReaded from the file TimeLine.txt");
			readTimeLineList();
			
		}
		
	}

	public LinkedList<Tweets> getTimeLineList() {
		return timeLineList;
	}
	
	public void printTimeLineList(){
		PrintWriter pw = null;
		try{
			pw = new PrintWriter(new FileOutputStream("TimeLine.txt"));
			for(Tweets t : timeLineList){
				pw.println(t.getId()+"@"+t.getName()+"(text)"+t.getText());
			}
			pw.close();
		}
		catch(FileNotFoundException e){
			LOG.warn("Can't create file");
		}
		
	}
	
	public void readTimeLineList(){		
		try{
			timeLineList.clear();
			BufferedReader br = new BufferedReader(new FileReader("TimeLine.txt"));
			String line;
			while((line = br.readLine()) != null){
				line.trim();
				long id = Long.parseLong(line.substring(0, line.indexOf("@")));
				String name = line.substring(line.indexOf("@")+1, line.indexOf("(text)"));
				String text = line.substring(line.indexOf("(text)")+6, line.length());
				timeLineList.add(new Tweets(id, name, text));
			}
			LOG.info("TimeLineList read correctly");
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

class Tweets{
	private long id;
	private String name;
	private String text;
	Tweets(long id, String name, String text){
		this.id = id;
		this.name = name;
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
	public String getName() {
		return name;
	}
	
	public long getId() {
		return id;
	}		
}



