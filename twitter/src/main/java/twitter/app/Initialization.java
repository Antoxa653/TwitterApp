package twitter.app;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import twitter4j.Twitter;
import twitter4j.internal.logging.Logger;

public class Initialization extends SwingWorker<TwitterInitialization, Object> {
	private final Twitter twitter;
	private ProgressBarFrame pbf;
	private MainFrame mf;
	private Logger LOG = Logger.getLogger(getClass());

	public Initialization(ProgressBarFrame pbf, Twitter twitter) {
		this.pbf = pbf;
		this.twitter = twitter;
	}

	@Override
	protected TwitterInitialization doInBackground() {
		TwitterInitialization init = new TwitterInitialization(twitter);
		return init;

	}

	@Override
	protected void done() {
		LOG.debug("Initialization done");
		pbf.dispose();
		try {
			mf = new MainFrame(get());
			mf.setVisible(true);
		} catch (InterruptedException e) {
			LOG.error("Thread is interrupted before or during the activity", e);
		} catch (ExecutionException e) {
			LOG.error("Attempt to retrieve the result of task that aborted by throwing an exception", e);
		}
	}

}


