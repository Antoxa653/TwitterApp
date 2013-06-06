package twitter.app;

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
		for(int latestTweets = 0;latestTweets < statusList.size();latestTweets++){
			StringBuilder builder = new StringBuilder();			
			builder.append(statusList.get(latestTweets).getUser().getName());			
			builder.append(statusList.get(latestTweets).getText());			
			listModel.addElement(builder.toString());								
		}	
		
	}
	
	public DefaultListModel<String> getUpdatedTimeLine() throws TwitterException{
		updateTimeLine();
		return listModel;
	}

}
