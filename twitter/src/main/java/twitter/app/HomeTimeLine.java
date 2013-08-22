package twitter.app;

import java.util.ArrayList;
import java.util.List;

import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.URLEntity;
import twitter4j.internal.logging.Logger;

public class HomeTimeLine {
	private final String timeLineFileLocation = System.getProperty("user.home") + "/TwitterApplication"
			+ "/TimeLine.txt";
	private Logger log = Logger.getLogger(getClass());
	private Twitter twitter;
	private List<Tweet> timeLineList = new ArrayList<Tweet>();
	private RateLimitationChecker rateLimatation;

	public HomeTimeLine(Twitter twitter) {
		this.twitter = twitter;
		rateLimatation = new RateLimitationChecker(twitter);
		setTimeLineList();
	}

	public List<Tweet> getTimeLineList() {
		return timeLineList;
	}

	private int getCurrentHomeTimeLineLimitStatus(String str) {
		return rateLimatation.checkLimitStatusForEndpoint(str);
	}

	public final void setTimeLineList() {
		int rateLimit = getCurrentHomeTimeLineLimitStatus("/statuses/home_timeline");
		List<Status> statusList = new ArrayList<Status>();
		if (rateLimit >= 2) {
			try {
				timeLineList.clear();
				statusList = twitter.getHomeTimeline();
				for (Status status : statusList) {

					timeLineList.add(new Tweet(getStatusId(status), getStatusCreatorIdentifiers(status),
							getStatusText(status), isStatusRetweet(status), isStatusRetweeted(status),
							getInReplyTo(status), getRetweetedStatusCreatorIdentifiers(status)));
				}
			} catch (TwitterException e) {
				log.error("Error while updating timeline" + e.getStatusCode() + " " + e);
			}
		}
		if (rateLimit == 2) {
			createTimeLineFile(timeLineFileLocation);
			log.debug("TimeLine writed in the TimeLine.txt file");
		}
		if (rateLimit < 2) {
			timeLineList.clear();
			readTimeLineFile(timeLineFileLocation);
			log.debug("TimeLine readed from the file TimeLine.txt");
		}
	}

	private long getStatusId(Status status) {
		return status.getId();
	}

	private String[] getStatusCreatorIdentifiers(Status status) {
		String[] identifiers = new String[3];
		identifiers[0] = String.valueOf(status.getUser().getId());
		identifiers[1] = status.getUser().getScreenName();
		identifiers[2] = status.getUser().getName();
		return identifiers;
	}

	private String getStatusText(Status status) {
		return parseStatusText(status.getText(), status.getURLEntities(), status.getMediaEntities());
	}

	private boolean isStatusRetweet(Status status) {
		return status.isRetweet();
	}

	private boolean isStatusRetweeted(Status status) {
		return status.isRetweeted();
	}

	private String[] getRetweetedStatusCreatorIdentifiers(Status status) {
		String[] creatorIdentifiersArray = new String[4];
		if (isStatusRetweet(status)) {
			creatorIdentifiersArray[0] = String.valueOf(status.getRetweetedStatus().getUser().getId());
			creatorIdentifiersArray[1] = status.getRetweetedStatus().getUser().getScreenName();
			creatorIdentifiersArray[2] = status.getRetweetedStatus().getUser().getName();
			creatorIdentifiersArray[3] = parseStatusText(status.getRetweetedStatus().getText(),
					status.getURLEntities(), status.getMediaEntities());

		}
		else {
			creatorIdentifiersArray[0] = "-1";
			creatorIdentifiersArray[1] = "-1";
			creatorIdentifiersArray[2] = "-1";
			creatorIdentifiersArray[3] = "-1";
		}
		return creatorIdentifiersArray;
	}

	private List<String> getInReplyTo(Status status) {
		int rateLimit = getCurrentHomeTimeLineLimitStatus("/statuses/show/:id");
		List<String> replyTo = new ArrayList<String>();
		if (rateLimit >= 1) {
			String inReplyToStatusId = String.valueOf(status.getInReplyToStatusId());
			if (!"-1".equals(inReplyToStatusId)) {
				do {
					replyTo.add(inReplyToStatusId);
					String inReplyToScreenName = status.getInReplyToScreenName();
					if (inReplyToScreenName == null) {
						replyTo.add("-1");
					}
					else {
						replyTo.add(inReplyToScreenName);
						try {
							replyTo.add(twitter.showUser(status.getInReplyToUserId()).getName());
						} catch (TwitterException e) {
							log.error("Cant obtain get in reply user name :", e);
						}
					}
					replyTo.add(String.valueOf(status.getInReplyToUserId()));
					try {
						Status str;
						str = twitter.showStatus(Long.parseLong(inReplyToStatusId));
						replyTo.add(parseStatusText(str.getText(), str.getURLEntities(), str.getMediaEntities()));
						status = str;
						inReplyToStatusId = String.valueOf(str.getInReplyToStatusId());
					} catch (TwitterException e) {
						log.debug("Error while getting the inReplyToStatusId status: " + e);
					}

				} while (!"-1".equals(inReplyToStatusId));
			}
			else {
				replyTo.add("-1");
				replyTo.add("-1");
				replyTo.add("-1");
				replyTo.add("-1");
			}
		}
		else {
			replyTo.add("-1");
			replyTo.add("-1");
			replyTo.add("-1");
			replyTo.add("-1");
		}

		return replyTo;
	}

	private String parseStatusText(String str, URLEntity[] urlEntity, MediaEntity[] mediaEntities) {
		String text = str;
		URLEntity[] uEntity = urlEntity;
		MediaEntity[] mEntity = mediaEntities;
		String regex = "http://{1}[a-zA-Z0-9./-]*[^http://][\\s]*|RT{1}\\s{1}@{1}[a-zA-Z0-9]*:{1}|https://{1}[a-zA-Z0-9./-]*[^http://][\\s]*";
		String[] statusTextArray = text.split(regex);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < statusTextArray.length; i++) {
			if (!"".equals(statusTextArray[i])) {
				sb.append(statusTextArray[i]);
			}
		}
		for (int i = 0; i < uEntity.length; i++) {
			sb.append(" ");
			//sb.insert(urlEntity[i].getStart(), urlEntity[i].getURL() + " ");
			sb.append(uEntity[i].getURL());
		}
		for (int i = 0; i < mEntity.length; i++) {
			sb.append(" ");
			sb.append(mEntity[i].getURL());
		}
		return sb.toString().trim();
	}

	private void createTimeLineFile(String filePath) {

	}

	private void readTimeLineFile(String filePath) {

	}

	protected class Tweet {
		private long statusId;
		//statusCreatorIdentifiers[0] = Id, statusCreatorIdentifiers[1] = @ScreenName, statusCreatorIdentifiers[2] = Name;
		private String[] statusCreatorIdentifiers;
		private String statusText;
		private boolean isStatusRetweet;
		private boolean isStatusRetweeted;
		//getInReplyTo[0] = getInReplyToStatusId, getInReplyTo[1] = @getInReplyToScreenName, getInReplyTo[2] = getInReplyToUserName, getInReplyTo[3] = getUserID, getInReply[4] = getText;
		private List<String> tweetIsReplyTo;
		//retweetedStatusCreatorIdentifiers[0] = Id, retweetedStatusCreatorIdentifiers[1] = @ScreenName, retweetedStatusCreatorIdentifiers[2] = Name, creatorIdentifiersArray[3] = getRetweetedStatus.getText();
		private String[] retweetedStatusCreatorIdentifiers;

		Tweet(long statusId, String[] statusCreatorIdentifiers, String statusText, boolean isStatusRetweet,
				boolean isStatusRetweeted, List<String> tweetIsReplyTo, String[] retweetedStatusCreatorIdentifiers) {
			this.statusId = statusId;
			this.statusCreatorIdentifiers = statusCreatorIdentifiers;
			this.statusText = statusText;
			this.isStatusRetweet = isStatusRetweet;
			this.isStatusRetweeted = isStatusRetweeted;
			this.tweetIsReplyTo = tweetIsReplyTo;
			this.retweetedStatusCreatorIdentifiers = retweetedStatusCreatorIdentifiers;

		}

		public String[] getRetweetedStatusCreatorIdentifiers() {
			return retweetedStatusCreatorIdentifiers;
		}

		public void setRetweetedStatusCreatorIdentifiers(String[] retweetedStatusCreatorIdentifiers) {
			this.retweetedStatusCreatorIdentifiers = retweetedStatusCreatorIdentifiers;
		}

		public long getStatusId() {
			return statusId;
		}

		public void setStatusId(long statusId) {
			this.statusId = statusId;
		}

		public String[] getStatusCreatorIdentifiers() {
			return statusCreatorIdentifiers;
		}

		public void setStatusCreatorIdentifiers(String[] statusCreatorIdentifiers) {
			this.statusCreatorIdentifiers = statusCreatorIdentifiers;
		}

		public String getStatusText() {
			return statusText;
		}

		public void setStatusText(String statusText) {
			this.statusText = statusText;
		}

		public boolean isStatusRetweet() {
			return isStatusRetweet;
		}

		public void setStatusRetweet(boolean isStatusRetweet) {
			this.isStatusRetweet = isStatusRetweet;
		}

		public boolean isStatusRetweeted() {
			return isStatusRetweeted;
		}

		public void setStatusRetweeted(boolean isStatusRetweeted) {
			this.isStatusRetweeted = isStatusRetweeted;
		}

		public List<String> getGetInReplyTo() {
			return tweetIsReplyTo;
		}

		public void setGetInReplyTo(List<String> getInReplyTo) {
			this.tweetIsReplyTo = getInReplyTo;
		}

	}
}
