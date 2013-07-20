package twitter.app;

import java.awt.EventQueue;
import twitter4j.Twitter;
import twitter4j.internal.logging.Logger;

public class Loader {

	public static void main(String[] args) {
		final Twitter twitter = new TwitterInstance().getTwitter();
		ResourceFilesChecker resource = new ResourceFilesChecker();
		boolean exist = resource.isTwitterPropertiesFileExist();
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
					ProgressBarFrame progressBarFrame = new ProgressBarFrame();
					TwitterResourcesInitialization init = new TwitterResourcesInitialization(progressBarFrame, twitter);
					init.execute();
					progressBarFrame.setVisible(true);
				}
			});
		}
	}
}
