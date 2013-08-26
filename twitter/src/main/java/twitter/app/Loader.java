package twitter.app;

import java.awt.EventQueue;

import twitter4j.Twitter;
import twitter4j.internal.logging.Logger;

public final class Loader {
	private static Logger log = Logger.getLogger(Loader.class);

	public static void main(String[] args) {
		log.debug("Start");
		boolean exist = ResourceFilesChecker.isFileExist(new ResourceFilesPath().getTwitter4jProerptiesFile());
		InterfaceStyle interfaceStyle = new InterfaceStyle();
		interfaceStyle.init();
		if (!exist) {
			final Twitter twitter = new TwitterInstance().getTwitter();
			EventQueue.invokeLater(new Runnable() {
				public void run() {
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

	private Loader() {

	}
}
