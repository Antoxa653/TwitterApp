package twitter.app;

import java.util.List;

import javax.swing.DefaultListModel;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class UpdateTimeLine {
	Twitter twitter = TwitterFactory.getSingleton();
	public DefaultListModel<String> getTimeLine(DefaultListModel<String> l) throws TwitterException{
		List<Status> ls = twitter.getHomeTimeline();
		DefaultListModel<String> listModel = l;
		for(int i=0;i<20;i++){
			listModel.addElement(ls.get(i).getId()+"  " +ls.get(i).getText());
			System.out.println(ls.get(i).getText());
		}
		return listModel;
	}

}
