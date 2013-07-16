package twitter.app;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.LinkedList;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.internal.logging.Logger;

public class TimeLine {
	private Logger LOG = Logger.getLogger(getClass());
	private Twitter twitter;
	private LinkedList<Tweets> timeLineList = new LinkedList<Tweets>();;
	private RateLimitation rl;

	public TimeLine(Twitter twitter) {
		this.twitter = twitter;
	}

	public LinkedList<Tweets> getTimeLineList() {
		return timeLineList;
	}

	public void setTimeLineList() {
		rl = new RateLimitation(twitter);
		int rateLimit = rl.checkLimitStatusForEndpoint("/statuses/home_timeline");
		if (rateLimit >= 2) {
			try {
				timeLineList.clear();
				List<Status> statusList = twitter.getHomeTimeline();
				for (Status status : statusList) {
					timeLineList.add(new Tweets(status.getId(), status.getUser().getScreenName(), status.getText()));
				}
			} catch (TwitterException e) {
				LOG.error("Error while updating timeline" + e.getStatusCode() + " " + e);
			}
		}
		if (rateLimit == 2) {
			LOG.debug("TimeLine writed in the TimeLine.txt file");
			printTimeLineList();
		}
		if (rateLimit <= 1) {
			LOG.debug("TimeLine readed from the file TimeLine.txt");
			readTimeLineList();
		}
	}

	public void printTimeLineList() {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileOutputStream("TimeLine.txt"));
			for (Tweets t : timeLineList) {
				pw.println(t.getId() + "@" + t.getName() + "(text)" + t.getText());
			}
		} catch (FileNotFoundException e) {
			LOG.error("Can't create TimeLine.txt file");
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	public void readTimeLineList() {
		timeLineList.clear();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("TimeLine.txt"));
			String line;
			while ((line = br.readLine()) != null) {
				line.trim();
				long id = Long.parseLong(line.substring(0, line.indexOf("@")));
				String name = line.substring(line.indexOf("@") + 1, line.indexOf("(text)"));
				String text = line.substring(line.indexOf("(text)") + 6, line.length());
				timeLineList.add(new Tweets(id, name, text));
			}
			LOG.debug("TimeLineList read correctly");
		} catch (FileNotFoundException e) {
			LOG.error("TimeLine.txt file not found", e);
		} catch (IOException i) {
			LOG.error("FriendList file don't found", i);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LOG.error("Error while trying to close bufferedreader strram", e);

				}
			}
		}
	}

	protected class Tweets {
		private long id;
		private String name;
		private String text;

		Tweets(long id, String name, String text) {
			this.id = id;
			this.name = name;
			this.text = text;
		}

		public String getText() {
			return text;
		}

		public String getName() {
			return name;
		}

		public long getId() {
			return id;
		}
	}
}
