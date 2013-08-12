package twitter.app;

import java.awt.EventQueue;

import twitter4j.Twitter;

public class Loader {

	public static void main(String[] args) {
		ResourceFilesChecker resource = new ResourceFilesChecker();
		boolean exist = resource.isTwitterPropertiesFileExist();
		if (!exist) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					final Twitter twitter = new TwitterInstance().getTwitter();
					OAuthFrame oa = new OAuthFrame(twitter);
					oa.setVisible(true);
				}
			});
		}
		else {
			final Twitter twitter = new TwitterInstance().readProperties();
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
