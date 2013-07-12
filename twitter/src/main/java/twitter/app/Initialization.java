package twitter.app;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import twitter4j.Twitter;

public class Initialization extends SwingWorker <TwitterInit, Object> {
	private final Twitter twitter;
	private ProgressBarFrame pbf;
	private MainFrame mf;
    public Initialization(ProgressBarFrame pbf, Twitter twitter) {
        this.pbf = pbf; 
        this.twitter = twitter;
    }
	@Override
	protected TwitterInit doInBackground() {
		TwitterInit init = new TwitterInit(twitter);
		return init;

	}
	@Override
	protected void done() {
			pbf.dispose();
			try {
				mf = new MainFrame(get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {				
				e.printStackTrace();
			}
			mf.setVisible(true);
	}

}

class TwitterInit {
	private FriendList fl;
	private UserDirectMessage udm;
	private UserStatus us;
	private TimeLine tlu;
	private Twitter twitter;
	TwitterInit(Twitter t) {
		this.twitter = t;
		fl = new FriendList(twitter);
		udm = new UserDirectMessage(twitter);
		us = new UserStatus(twitter);
		tlu = new TimeLine(twitter);
	}

	public FriendList getFl() {
		return fl;
	}

	public void setFl(FriendList fl) {
		this.fl = fl;
	}

	public UserDirectMessage getUdm() {
		return udm;
	}

	public void setUdm(UserDirectMessage udm) {
		this.udm = udm;
	}

	public UserStatus getUs() {
		return us;
	}

	public void setUs(UserStatus us) {
		this.us = us;
	}

	public Twitter getTwitter() {
		return twitter;
	}

	public void setTwitter(Twitter twitter) {
		this.twitter = twitter;
	}

	public TimeLine getTlu() {
		return tlu;
	}

	public void setTlu(TimeLine tlu) {
		this.tlu = tlu;
	}
}


