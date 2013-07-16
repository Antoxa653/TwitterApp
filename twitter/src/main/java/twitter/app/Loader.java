package twitter.app;

import java.awt.EventQueue;
import twitter4j.Twitter;
import twitter4j.internal.logging.Logger;

public class Loader {
	private Logger LOG = Logger.getLogger(getClass());

	public static void main(String[] args) {
		final Twitter twitter = new TwitterInstance().getTwitter();
		PropertiesExist prop = new PropertiesExist();
		boolean exist = prop.isPropertiesExist();
		if (!exist) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					OAuthFrame oa = new OAuthFrame(twitter);
					oa.setVisible(true);
				}
			});
		}
		else {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					ProgressBarFrame pbf = new ProgressBarFrame();
					Initialization init = new Initialization(pbf, twitter);
					init.execute();
					pbf.setVisible(true);
				}
			});
		}
	}
}
