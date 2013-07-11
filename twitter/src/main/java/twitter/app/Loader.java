package twitter.app;

import java.awt.EventQueue;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.internal.logging.Logger;

public class Loader {	
	public static final Logger LOG = Logger.getLogger(Loader.class);	
	public static void main(String[] args){	
		PropertiesExist prop = new PropertiesExist();
		final Twitter twitter = new TwitterInstance().getTwitter();		
		boolean exist = prop.isPropertiesExist();
		if(!exist){
			EventQueue.invokeLater(new Runnable(){
				public void run(){
					OAuthFrame oa = new OAuthFrame(twitter);
					oa.setVisible(true);			
				}
			});
		}
		if(exist){
			EventQueue.invokeLater(new Runnable(){
				public void run(){
					ProgressBarFrame pbf = new ProgressBarFrame();					
					Initialization init = new Initialization(pbf,twitter);
					init.execute();
					pbf.setVisible(true);
				}
			});
			
		}		
	}
}