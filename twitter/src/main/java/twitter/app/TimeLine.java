package twitter.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
	private LinkedList<Tweets> timeLineList = new LinkedList<Tweets>();
	public static final Logger LOG = Logger.getLogger(TimeLine.class);
	public TimeLine(Twitter twitter){
		this.twitter = twitter;
		setTimeLineList();
	}
	
	private void setTimeLineList(){		
		try {
			List<Status> statusList = twitter.getHomeTimeline();
			for(Status status: statusList){			 				
				timeLineList.add(new Tweets(status.getId(),status.getUser().getName(),status.getText()));	
			}		
		} 
		catch (TwitterException e) {
			LOG.warn("Error while updating timeline"+e.getStatusCode());
			e.printStackTrace();
		}
	}

	public LinkedList<Tweets> getTimeLineList() {
		return timeLineList;
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



