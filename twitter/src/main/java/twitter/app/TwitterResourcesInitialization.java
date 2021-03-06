package twitter.app;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import twitter4j.Twitter;


public class TwitterResourcesInitialization extends SwingWorker<TwitterInitialization, Object> {
	private final Twitter twitter;
	private ProgressBarFrame progressBarFrame;
	private MainFrame mainFrame;
	private Logger log = Logger.getLogger(getClass().getName());

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
		try {			
			mainFrame = new MainFrame(get());
			mainFrame.init();
			mainFrame.setVisible(true);
			log.debug("Initialization done");
		} catch (InterruptedException e) {
			log.error("Thread is interrupted before or during the activity", e);
		} catch (ExecutionException e) {
			log.error("Attempt to retrieve the result of task that aborted by throwing an exception", e);
		} finally {
			progressBarFrame.dispose();
		}
	}

}
