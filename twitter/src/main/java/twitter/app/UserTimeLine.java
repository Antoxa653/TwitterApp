package twitter.app;

import java.util.List;

import javax.swing.DefaultListModel;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class UserTimeLine {
	Twitter twitter;
	public UserTimeLine(){
		this.twitter = TwitterFactory.getSingleton();
	}

	public DefaultListModel<String> getTimeLine() throws TwitterException{
		List<Status> statusList = twitter.getHomeTimeline();
		DefaultListModel<String> listModel = new DefaultListModel<String>();			
		for(int i = 0;i < statusList.size();i++){
			listModel.addElement(statusList.get(i).getUser().getName()+"       " +statusList.get(i).getText());			
		}
	    return listModel;
	}

}
