package twitter.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.URLEntity;
import twitter4j.internal.logging.Logger;

public class HomeTimeLine {
	private final String timeLineFileLocation = System.getProperty("user.home") + "/TwitterApplication"
			+ "/TimeLine.xml";
	private Logger log = Logger.getLogger(getClass());
	private Twitter twitter;
	private List<Tweet> timeLineList = new ArrayList<Tweet>();
	private RateLimitationChecker rateLimitation;

	public HomeTimeLine(Twitter twitter) {
		this.twitter = twitter;
		rateLimitation = new RateLimitationChecker(twitter);
		setTimeLineList();
	}

	public List<Tweet> getTimeLineList() {
		return timeLineList;
	}

	private int getCurrentHomeTimeLineLimitStatus(String str) {
		return rateLimitation.checkLimitStatusForEndpoint(str);
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
							tweetIsReplyTo(status), getRetweetedStatusCreatorIdentifiers(status)));
				}
			} catch (TwitterException e) {
				log.error("Error while updating timeline" + e.getStatusCode() + " " + e);
			}
		}
		if (rateLimit == 2) {
			createTimeLineFile(timeLineFileLocation);
			log.debug("TimeLine writed in the TimeLine.xml file");
		}
		if (rateLimit < 2) {
			timeLineList.clear();
			readTimeLineFile(timeLineFileLocation);
			log.debug("TimeLine readed from the file TimeLine.xml");
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

	private List<String> tweetIsReplyTo(Status status) {
		int rateLimit = getCurrentHomeTimeLineLimitStatus("/statuses/show/:id");
		List<String> replyTo = new ArrayList<String>();
		if (rateLimit >= 2) {
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
				replyTo.add("-1");
			}
		}
		else {
			replyTo.add("-1");
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

	private void createTimeLineFile(String str) {
		try {
			File file = new File(timeLineFileLocation);
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("timeLine");
			doc.appendChild(rootElement);
			for (int i = 0; i < timeLineList.size(); i++) {
				//<timeline>

				//<tweet>
				Element tweetElement = doc.createElement("tweet");
				tweetElement.setAttribute("tweet", String.valueOf(i));
				rootElement.appendChild(tweetElement);

				//<status_id>
				Element statusIdElement = doc.createElement("status_Id");
				statusIdElement.appendChild(doc.createTextNode(String.valueOf(timeLineList.get(i).getStatusId())));
				tweetElement.appendChild(statusIdElement);

				//<satus_creator>
				Element statusCreatorElement = doc.createElement("status_creator");
				tweetElement.appendChild(statusCreatorElement);

				Element statusCreatorIdElement = doc.createElement("status_creator_id");
				statusCreatorIdElement.appendChild(doc
						.createTextNode(timeLineList.get(i).getStatusCreatorIdentifiers()[0]));
				statusCreatorElement.appendChild(statusCreatorIdElement);

				Element statusCreatorScreenNameElement = doc.createElement("status_creator_screen_name");
				statusCreatorScreenNameElement.appendChild(doc.createTextNode(timeLineList.get(i)
						.getStatusCreatorIdentifiers()[1]));
				statusCreatorElement.appendChild(statusCreatorScreenNameElement);

				Element statusCreatorNameElement = doc.createElement("status_creator_name");
				statusCreatorNameElement.appendChild(doc.createTextNode(timeLineList.get(i)
						.getStatusCreatorIdentifiers()[2]));
				statusCreatorElement.appendChild(statusCreatorNameElement);

				//<status_text>
				Element statusTextElement = doc.createElement("status_text");
				statusTextElement.appendChild(doc.createTextNode(timeLineList.get(i).getStatusText()));
				tweetElement.appendChild(statusTextElement);

				//<is_status_retweet>
				Element isStatusRetweetElement = doc.createElement("is_status_retweet");
				isStatusRetweetElement.appendChild(doc.createTextNode(String.valueOf(timeLineList.get(i)
						.isStatusRetweet())));
				tweetElement.appendChild(isStatusRetweetElement);

				//<is_status_retweeted>
				Element isStatusRetweetedElement = doc.createElement("is_status_retweeted");
				isStatusRetweetedElement.appendChild(doc.createTextNode(String.valueOf(timeLineList.get(i)
						.isStatusRetweeted())));
				tweetElement.appendChild(isStatusRetweetedElement);

				//<tweet_reply_to>
				Element tweetReplyToElement = doc.createElement("tweet_reply_to");
				tweetElement.appendChild(tweetReplyToElement);

				if (timeLineList.get(i).getTweetIsReplyTo().size() > 4) {
					for (int j = 0; j < timeLineList.get(i).getTweetIsReplyTo().size(); j += 5) {
						//<replytweet = i>
						Element replytweet = doc.createElement("reply_tweet");
						replytweet.setAttribute("reply_tweet", String.valueOf(j / 5));
						tweetReplyToElement.appendChild(replytweet);

						//<reply_to_status_status_id>
						Element replyToStatusIdElement = doc.createElement("reply_to_status_id");
						replyToStatusIdElement.appendChild(doc.createTextNode(String
								.valueOf(timeLineList.get(i).getTweetIsReplyTo().get(j))));
						replytweet.appendChild(replyToStatusIdElement);

						//<reply_to_user_screenname>
						Element replyToUserScreenNameElement = doc.createElement("reply_to_user_screenname");
						replyToUserScreenNameElement.appendChild(doc.createTextNode(String.valueOf(timeLineList.get(i)
								.getTweetIsReplyTo()
								.get(
										j + 1))));
						replytweet.appendChild(replyToUserScreenNameElement);

						//<reply_to_user_name>
						Element replyToUserNameElement = doc.createElement("reply_to_user_name");
						replyToUserNameElement.appendChild(doc.createTextNode(String
								.valueOf(timeLineList.get(i).getTweetIsReplyTo().get(j + 2))));
						replytweet.appendChild(replyToUserNameElement);

						//<reply_to_user_id>
						Element replyToUserIdElement = doc.createElement("reply_to_user_id");
						replyToUserIdElement
								.appendChild(doc.createTextNode(String.valueOf(timeLineList.get(i).getTweetIsReplyTo()
										.get(j + 3))));
						replytweet.appendChild(replyToUserIdElement);

						//<reply_text>
						Element replyTextElement = doc.createElement("reply_text");
						replyTextElement
								.appendChild(doc.createTextNode(String.valueOf(timeLineList.get(i).getTweetIsReplyTo()
										.get(j + 4))));
						replytweet.appendChild(replyTextElement);

					}
				}
				else {
					//<replytweet = i>
					Element replytweet = doc.createElement("reply_tweet");
					replytweet.setAttribute("reply_tweet", "1");
					tweetReplyToElement.appendChild(replytweet);

					//<reply_to_status_status_id>
					Element replyToStatusIdElement = doc.createElement("reply_to_status_id");
					replyToStatusIdElement
							.appendChild(doc.createTextNode(String.valueOf(timeLineList.get(i).getTweetIsReplyTo()
									.get(0))));
					replytweet.appendChild(replyToStatusIdElement);

					//<reply_to_user_screenname>
					Element replyToUserScreenNameElement = doc.createElement("reply_to_user_screenname");
					replyToUserScreenNameElement.appendChild(doc.createTextNode(String.valueOf(timeLineList.get(i)
							.getTweetIsReplyTo()
							.get(
									1))));
					replytweet.appendChild(replyToUserScreenNameElement);

					//<reply_to_user_name>
					Element replyToUserNameElement = doc.createElement("reply_to_user_name");
					replyToUserNameElement.appendChild(doc.createTextNode(String
							.valueOf(timeLineList.get(i).getTweetIsReplyTo().get(2))));
					replytweet.appendChild(replyToUserNameElement);

					//<reply_to_user_id>
					Element replyToUserIdElement = doc.createElement("reply_to_user_id");
					replyToUserIdElement
							.appendChild(doc.createTextNode(String.valueOf(timeLineList.get(i).getTweetIsReplyTo()
									.get(3))));
					replytweet.appendChild(replyToUserIdElement);

					//<reply_text>
					Element replyTextElement = doc.createElement("reply_text");
					replyTextElement.appendChild(doc.createTextNode(String.valueOf(timeLineList.get(i)
							.getTweetIsReplyTo().get(4))));
					replytweet.appendChild(replyTextElement);

				}

				//<retweeted_status_creator_identifiers>
				Element retweetedStatusCreatorIdentifiersElement = doc
						.createElement("retweeted_status_creator_identifiers");
				tweetElement.appendChild(retweetedStatusCreatorIdentifiersElement);

				//<retweeted_status_creator_id>
				Element retweetedStatusCreatorIdElement = doc.createElement("retweeted_status_creator_id");
				retweetedStatusCreatorIdElement.appendChild(doc.createTextNode(String.valueOf(timeLineList.get(i)
						.getRetweetedStatusCreatorIdentifiers()[0])));
				retweetedStatusCreatorIdentifiersElement.appendChild(retweetedStatusCreatorIdElement);

				//<retweeted_status_creator_screenname>
				Element retweetedStatusCreatorScreenNameElement = doc
						.createElement("retweeted_status_creator_screenname");
				retweetedStatusCreatorScreenNameElement.appendChild(doc.createTextNode(String.valueOf(timeLineList.get(
						i)
						.getRetweetedStatusCreatorIdentifiers()[1])));
				retweetedStatusCreatorIdentifiersElement.appendChild(retweetedStatusCreatorScreenNameElement);

				//<retweeted_status_creator_name>
				Element retweetedStatusCreatorNameElement = doc.createElement("retweeted_status_creator_name");
				retweetedStatusCreatorNameElement.appendChild(doc.createTextNode(String.valueOf(timeLineList.get(i)
						.getRetweetedStatusCreatorIdentifiers()[2])));
				retweetedStatusCreatorIdentifiersElement.appendChild(retweetedStatusCreatorNameElement);

				//<retweeted_status_text>
				Element retweetedStatusTextElement = doc.createElement("retweeted_status_text");
				retweetedStatusTextElement.appendChild(doc.createTextNode(String.valueOf(timeLineList.get(i)
						.getRetweetedStatusCreatorIdentifiers()[3])));
				retweetedStatusCreatorIdentifiersElement.appendChild(retweetedStatusTextElement);

			}
			//to file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(file);

			transformer.transform(source, result);

		} catch (ParserConfigurationException e) {
			log.error("Parser Configuration error while writing TimeLine.xml file", e);

		} catch (TransformerConfigurationException e) {
			log.error("Transformer Configuration error while writing TimeLine.xml file", e);
		} catch (TransformerException e) {
			log.error("Exception condition while transformation proccess ", e);

		}

	}

	private void readTimeLineFile(String filePath) {		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		long statusId = 0;
		//statusCreatorIdentifiers[0] = Id, statusCreatorIdentifiers[1] = @ScreenName, statusCreatorIdentifiers[2] = Name;
		String[] statusCreatorIdentifiers = new String[3];
		String statusText = null;
		boolean isStatusRetweet = false;
		boolean isStatusRetweeted = false;
		//getInReplyTo[0] = getInReplyToStatusId, getInReplyTo[1] = @getInReplyToScreenName, getInReplyTo[2] = getInReplyToUserName, getInReplyTo[3] = getUserID, getInReply[4] = getText;
		List<String> tweetIsReplyTo = new ArrayList<String>();
		//retweetedStatusCreatorIdentifiers[0] = Id, retweetedStatusCreatorIdentifiers[1] = @ScreenName, retweetedStatusCreatorIdentifiers[2] = Name, creatorIdentifiersArray[3] = getRetweetedStatus.getText();
		String[] retweetedStatusCreatorIdentifiers = new String[4];
		try {
			dbFactory = DocumentBuilderFactory.newInstance();
			dbFactory.setValidating(false);
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(filePath);

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("tweet");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				statusCreatorIdentifiers = new String[3];
				retweetedStatusCreatorIdentifiers = new String[4];
				tweetIsReplyTo = new ArrayList<String>();
				isStatusRetweet = false;
				isStatusRetweeted = false;
				
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					
					Element eElement = (Element) nNode;

					statusId = Long.parseLong(eElement.getElementsByTagName("status_Id").item(0).getTextContent());

					NodeList statusCreatorList = eElement.getElementsByTagName("status_creator");
					for (int j = 0; j < statusCreatorList.getLength(); j++) {
						Node sNode = statusCreatorList.item(j);
						if (sNode.getNodeType() == Node.ELEMENT_NODE) {
							Element sElement = (Element) sNode;
							statusCreatorIdentifiers[0] = sElement.getElementsByTagName("status_creator_id").item(0)
									.getTextContent();
							statusCreatorIdentifiers[1] = sElement.getElementsByTagName("status_creator_screen_name")
									.item(0)
									.getTextContent();
							statusCreatorIdentifiers[2] = sElement.getElementsByTagName("status_creator_screen_name")
									.item(0)
									.getTextContent();
						}
					}

					statusText = eElement.getElementsByTagName("status_text").item(0).getTextContent();

					isStatusRetweet = Boolean.parseBoolean(eElement.getElementsByTagName("is_status_retweet").item(0)
							.getTextContent());

					isStatusRetweeted = Boolean.parseBoolean(eElement.getElementsByTagName("is_status_retweeted")
							.item(0).getTextContent());

					NodeList replyToList = eElement.getElementsByTagName("reply_tweet");
					for (int k = 0; k < replyToList.getLength(); k++) {
						Node kNode = replyToList.item(k);
						if (kNode.getNodeType() == Node.ELEMENT_NODE) {
							Element kElement = (Element) kNode;

							tweetIsReplyTo.add(kElement.getElementsByTagName("reply_to_status_id").item(0)
									.getTextContent());
							tweetIsReplyTo.add(kElement.getElementsByTagName("reply_to_user_screenname")
									.item(0).getTextContent());
							tweetIsReplyTo.add(kElement.getElementsByTagName("reply_to_user_name")
									.item(0).getTextContent());
							tweetIsReplyTo.add(kElement.getElementsByTagName("reply_to_user_id").item(0)
									.getTextContent());
							tweetIsReplyTo.add(kElement.getElementsByTagName("reply_text").item(0).getTextContent());

						}
					}

					NodeList retweetedList = eElement.getElementsByTagName("retweeted_status_creator_identifiers");
					for (int j = 0; j < retweetedList.getLength(); j++) {
						Node dNode = retweetedList.item(j);
						if (dNode.getNodeType() == Node.ELEMENT_NODE) {
							Element dElement = (Element) dNode;
							retweetedStatusCreatorIdentifiers[0] = dElement
									.getElementsByTagName("retweeted_status_creator_id").item(0)
									.getTextContent();
							retweetedStatusCreatorIdentifiers[1] = dElement
									.getElementsByTagName("retweeted_status_creator_screenname")
									.item(0).getTextContent();
							retweetedStatusCreatorIdentifiers[2] = dElement
									.getElementsByTagName("retweeted_status_creator_name").item(0)
									.getTextContent();
							retweetedStatusCreatorIdentifiers[3] = dElement
									.getElementsByTagName("retweeted_status_text").item(0).getTextContent();

						}
					}

				}

				timeLineList.add(new Tweet(statusId, statusCreatorIdentifiers, statusText, isStatusRetweet,
						isStatusRetweeted, tweetIsReplyTo, retweetedStatusCreatorIdentifiers));
				

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public class Tweet {
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

		public List<String> getTweetIsReplyTo() {
			return tweetIsReplyTo;
		}

		public void setTweetIsReplyTo(List<String> getInReplyTo) {
			this.tweetIsReplyTo = getInReplyTo;
		}

	}
}
