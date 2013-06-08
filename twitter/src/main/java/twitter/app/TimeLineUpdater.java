package twitter.app;

import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultListModel;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TimeLineUpdater {
	private Twitter twitter;
	private DefaultListModel<String> listModel;
	
	public TimeLineUpdater(){
		this.twitter = TwitterFactory.getSingleton();
	}

	private void updateTimeLine() throws TwitterException{
		List<Status> statusList = twitter.getHomeTimeline();
		listModel = new DefaultListModel<String>();	
		Iterator<Status> iterator = statusList.iterator();
		while(iterator.hasNext()){
			StringBuilder builder = new StringBuilder();			
			builder.append(iterator.next().getUser().getName());
			builder.append(iterator.next().getText());
			listModel.addElement(builder.toString());								
		}	
		
	}
		
	public DefaultListModel<String> getUpdatedTimeLine() throws TwitterException{
		updateTimeLine();
		return listModel;
	}

}
