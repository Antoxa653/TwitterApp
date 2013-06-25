package twitter.app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultListModel;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TimeLineUpdater {
	private Twitter twitter;
	private List<Tweets> list = new ArrayList<Tweets>();
	
	public TimeLineUpdater(){
		this.twitter = TwitterFactory.getSingleton();
	}

	private void updateTimeLine() throws TwitterException{
		List<Status> statusList = twitter.getHomeTimeline();		
		for(Status status: statusList){
			Tweets t = new Tweets(status.getId(),status.getUser().getName(),status.getText());			
			list.add(t);	
		}
		
	}
		
	public List<Tweets> getUpdatedTimeLine() throws TwitterException{
		updateTimeLine();
		return list;
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

