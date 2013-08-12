package twitter.app;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import twitter4j.Twitter;
import twitter4j.internal.logging.Logger;

public class TwitterResourcesInitialization extends SwingWorker<TwitterInitialization, Object> {
	private final Twitter twitter;
	private ProgressBarFrame progressBarFrame;
	private MainFrame mainFrame;
	private Logger log = Logger.getLogger(getClass());

	public TwitterResourcesInitialization(ProgressBarFrame pbf, Twitter twitter) {
		this.progressBarFrame = pbf;
		this.twitter = twitter;
	}

	@Override
	protected TwitterInitialization doInBackground() {
		TwitterInitialization init = new TwitterInitialization(twitter);
		return init;

	}

	@Override
	protected void done() {
		log.debug("Initialization done");
		try {
			mainFrame = new MainFrame(get());
			mainFrame.init();
			mainFrame.setVisible(true);
		} catch (InterruptedException e) {
			log.error("Thread is interrupted before or during the activity", e);
		} catch (ExecutionException e) {
			log.error("Attempt to retrieve the result of task that aborted by throwing an exception", e);
		} finally {
			progressBarFrame.dispose();
		}
	}

}
