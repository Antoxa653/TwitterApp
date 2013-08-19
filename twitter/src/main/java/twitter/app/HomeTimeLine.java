package twitter.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.internal.logging.Logger;

public class HomeTimeLine {
	private final String timeLineFileLocation = System.getProperty("user.home") + "/TwitterApplication";
	private Logger log = Logger.getLogger(getClass());
	private Twitter twitter;
	private List<Tweets> timeLineList = new ArrayList<Tweets>();
	private RateLimitationChecker rateLimatation;

	public HomeTimeLine(Twitter twitter) {
		this.twitter = twitter;

	}

	public List<Tweets> getTimeLineList() {
		return timeLineList;
	}

	public void setTimeLineList() {
		rateLimatation = new RateLimitationChecker(twitter);
		int rateLimit = rateLimatation.checkLimitStatusForEndpoint("/statuses/home_timeline");
		if (rateLimit >= 2) {
			try {
				timeLineList.clear();
				List<Status> statusList = twitter.getHomeTimeline();
				for (Status status : statusList) {
					timeLineList.add(new Tweets(status.getId(), status.getUser().getScreenName(), status.getText()));

				}

			} catch (TwitterException e) {
				log.error("Error while updating timeline" + e.getStatusCode() + " " + e);
			}
		}
		if (rateLimit == 2) {
			createTimeLineFile();
			log.debug("TimeLine writed in the TimeLine.txt file");
		}
		if (rateLimit < 2) {
			readTimeLineFile();
			log.debug("TimeLine readed from the file TimeLine.txt");
		}

	}

	public void createTimeLineFile() {
		File userDir = new File(timeLineFileLocation);
		userDir.mkdirs();
		PrintWriter pw = null;
		StringBuilder sb = new StringBuilder();
		try {
			pw = new PrintWriter(new FileOutputStream(userDir + "/TimeLine.txt"));
			for (Tweets t : timeLineList) {
				sb.append("(ID)");
				sb.append(t.getId());
				sb.append("@");
				sb.append(t.getName());
				sb.append("(text)");
				sb.append(t.getText());
				pw.println(sb.toString());
				sb.setLength(0);
			}
		} catch (FileNotFoundException e) {
			log.error("Can't create TimeLine.txt file");
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	public void readTimeLineFile() {
		timeLineList.clear();

		Scanner in = null;
		try {
			in = new Scanner(new File(timeLineFileLocation + "/TimeLine.txt"));

			String line;
			boolean broken = false;
			while ((line = in.nextLine()) != null) {

				if ("".equals(line)) {
					broken = true;
					continue;
				}

				if (!broken & "(ID)".equals(line.substring(0, 4))) {
					line.trim();
					long id = Long.parseLong(line.substring(line.indexOf("(ID)") + 4, line.indexOf("@")));
					String name = line.substring(line.indexOf("@") + 1, line.indexOf("(text)"));
					String text = line.substring(line.indexOf("(text)") + 6, line.length());
					timeLineList.add(new Tweets(id, name, text));
				}

				else if (broken & !"(ID)".equals(line.substring(0, 4))) {
					Tweets tweet = timeLineList.get(timeLineList.size() - 1);
					String str = tweet.getText();
					tweet.setText(str + " " + line);
					broken = false;
				}
			}
			log.debug("TimeLineList read correctly");
		} catch (FileNotFoundException e) {
			log.error("TimeLine.txt file not found", e);
		} finally {
			if (in != null) {
				in.close();
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

		public void setText(String str) {
			this.text = str;
		}
	}
}
