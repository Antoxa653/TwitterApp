package twitter.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterInstance {
	private Logger log = Logger.getLogger(getClass().getName());
	private Twitter twitter;	
	TwitterInstance() {
		twitter = TwitterFactory.getSingleton();
	}

	public Twitter getTwitter() {
		return twitter;
	}

	public Twitter readProperties() {
		File propertiesFile = new File(new ResourceFilesPath().getTwitter4jProerptiesFile());		
		String[] a = new String[5];
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(propertiesFile));
			String line;
			int i = 0;
			while ((line = br.readLine()) != null) {
				line.trim();				
				a[i] = line.substring(line.indexOf("=") + 1, line.length());
				i++;
			}
		} catch (IOException e) {
			log.error("FriendList file don't found", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					log.error("Error while trying to close bufferedreader strram", e);

				}
			}
		}
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
				.setOAuthConsumerKey(a[1])
				.setOAuthConsumerSecret(a[2])
				.setOAuthAccessToken(a[3])
				.setOAuthAccessTokenSecret(a[4]);
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
		return twitter;
	}

}
