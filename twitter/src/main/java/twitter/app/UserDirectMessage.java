package twitter.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import twitter4j.DirectMessage;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.internal.logging.Logger;

public class UserDirectMessage {
	private final String sentMessagesFileLocation = System.getProperty("user.home") + "/TwitterApplication";
	private final String recievedMessagesFileLocation = System.getProperty("user.home") + "/TwitterApplication";
	private Logger log = Logger.getLogger(getClass());
	private Twitter twitter;
	private List<RecievedMessage> recieved = new LinkedList<RecievedMessage>();
	private List<SentMessage> sent = new LinkedList<SentMessage>();
	private List<Conversation> conv = new LinkedList<Conversation>();
	private RateLimitationChecker rl;

	UserDirectMessage(Twitter t) {
		this.twitter = t;
	}

	public List<RecievedMessage> getRecievedMessagesList() {
		return recieved;
	}

	public void setRecievedMessagesList() {
		rl = new RateLimitationChecker(twitter);
		int rateLimit = rl.checkLimitStatusForEndpoint("/direct_messages");
		if (rateLimit >= 2) {
			try {
				recieved.clear();
				ResponseList<DirectMessage> recievedMessages = twitter.getDirectMessages();
				for (DirectMessage m : recievedMessages) {
					recieved.add(new RecievedMessage(m.getId(), m.getSenderScreenName(), m.getText(), m.getCreatedAt()));
				}
				log.debug("Recieved Messages update");
			} catch (TwitterException e) {
				log.error("Error occurs while updating recieved messages: " + e.getStatusCode() + " " + e);
			}
		}
		if (rateLimit == 2) {
			log.debug("RecievedMessages.txt file created");
			createRecievedMessagesFile();
		}
		if (rateLimit < 2) {
			log.debug("RecievedMessages.txt file readeding...");
			readRecievedMessagesFile();
		}

	}

	public List<SentMessage> getSentMessagesList() {
		return sent;
	}

	public void setSentMessagesList() {
		rl = new RateLimitationChecker(twitter);
		int rateLimit = rl.checkLimitStatusForEndpoint("/direct_messages/sent");
		if (rateLimit >= 2) {
			try {
				sent.clear();
				ResponseList<DirectMessage> sentMessages = twitter.getSentDirectMessages();
				for (DirectMessage m : sentMessages) {
					sent.add(new SentMessage(m.getId(), m.getRecipientScreenName(), m.getText(), m.getCreatedAt()));
				}
				log.debug("Sent Messages update");
			} catch (TwitterException e) {
				log.error("Error occurs while updating sent messages" + e.getStatusCode() + " " + e);

			}
		}
		if (rateLimit == 2) {
			createSentMessagesFile();
			log.debug("SentMessages.txt file created");
		}
		if (rateLimit < 2) {
			readSentMessagesFile();
			log.debug("SentMessages file reading...");
		}

	}

	public boolean sentDirectMessageTo(String name, String text) {
		boolean complit = true;
		try {
			DirectMessage message = twitter.sendDirectMessage(twitter.showUser(name).getId(), text);
		} catch (TwitterException e) {
			log.error("Error while try sending direct message to friend: " + e.getStatusCode() + " " + e);
			complit = false;
		}
		return complit;
	}

	public Set<String> getConversationsMembersNamesList() {
		Set<String> list = new LinkedHashSet<String>();
		for (RecievedMessage rm : recieved) {
			list.add(rm.getSenderName());
		}
		for (SentMessage sm : sent) {
			list.add(sm.getRecipientName());
		}
		return list;
	}

	private void setConversationMessages(String name) {
		conv.clear();
		for (RecievedMessage rm : recieved) {
			if (rm.getSenderName().equals(name)) {
				conv.add(new Conversation(rm.getDate(), rm.getText(), false));
			}
		}
		for (SentMessage sm : sent) {
			if (sm.getRecipientName().equals(name)) {
				conv.add(new Conversation(sm.getDate(), sm.getText(), true));
			}
		}

		Collections.sort(conv, new Comparator<Conversation>() {

			public int compare(Conversation o1, Conversation o2) {
				if (o1.getDate() == o2.getDate()) {
					return 0;
				}
				return o1.getDate().getTime() < o2.getDate().getTime() ? -1 : 1;
			}
		});
	}

	public List<Conversation> getConversationMessages(String name) {
		setConversationMessages(name);
		return conv;
	}

	public void createSentMessagesFile() {
		File userDir = new File(sentMessagesFileLocation);
		userDir.mkdirs();
		PrintWriter pw = null;
		StringBuilder sb = new StringBuilder();
		try {
			pw = new PrintWriter(new FileOutputStream(userDir + "/SentMessages.txt"));
			for (SentMessage s : sent) {
				sb.append(s.getId());
				sb.append("@");
				sb.append(s.getRecipientName());
				sb.append("(text)");
				sb.append(s.getText());
				sb.append("(Date)");
				sb.append(s.getDate());
				//pw.println(s.getId() + "@" + s.getRecipientName() + "(text)" + s.getText() + "(Date)" + s.getDate());
				pw.println(sb.toString());
				sb.setLength(0);
			}
		} catch (FileNotFoundException e) {
			log.error("Error while try to create SentMessages.txt file", e);
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

	}

	private void readSentMessagesFile() {
		sent.clear();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(sentMessagesFileLocation + "/SentMessages.txt"));
			String line;
			while ((line = br.readLine()) != null) {
				line.trim();
				long id = Long.parseLong(line.substring(0, line.indexOf("@")));
				String name = line.substring(line.indexOf("@") + 1, line.indexOf("(text)"));
				String text = line.substring(line.indexOf("(text)") + 6, line.indexOf("(Date)"));
				String dateString = line.substring(line.indexOf("(Date)") + 6, line.length());
				DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.UK);
				Date convertedDate = format.parse(dateString);
				sent.add(new SentMessage(id, name, text, convertedDate));
			}
			log.debug("SentMessages read correctly");
		} catch (FileNotFoundException e) {
			log.error("SentMessages.txt file not found", e);
		} catch (ParseException pe) {
			log.error("ParseException while parsing Date object", pe);

		} catch (NumberFormatException e) {
			log.error(
					"Thrown to indicate that the application has attempted to convert a string to one of the numeric types",
					e);
		} catch (IOException e) {
			log.error("IOException while reading SentMessages.txt file: ", e);
		}
	}

	private void createRecievedMessagesFile() {
		File userDir = new File(recievedMessagesFileLocation);
		userDir.mkdirs();
		PrintWriter pw = null;
		StringBuilder sb = new StringBuilder();
		try {
			pw = new PrintWriter(new FileOutputStream(userDir + "/RecievedMessages.txt"));
			for (RecievedMessage r : recieved) {
				sb.append(r.getId());
				sb.append("@");
				sb.append(r.getSenderName());
				sb.append("(text)");
				sb.append(r.getText());
				sb.append("(Date)");
				sb.append(r.getDate());
				//pw.println(r.getId() + "@" + r.getSenderName() + "(text)" + r.getText() + "(Date)" + r.getDate());
				pw.println(sb.toString());
				sb.setLength(0);
			}
		} catch (FileNotFoundException e) {
			log.error("Error while creating RecievedMessages.txt file", e);
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

	}

	private void readRecievedMessagesFile() {
		recieved.clear();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(recievedMessagesFileLocation + "/RecievedMessages.txt"));
			String line;
			while ((line = br.readLine()) != null) {
				line.trim();
				long id = Long.parseLong(line.substring(0, line.indexOf("@")));
				String name = line.substring(line.indexOf("@") + 1, line.indexOf("(text)"));
				String text = line.substring(line.indexOf("(text)") + 6, line.indexOf("(Date)"));
				String dateString = line.substring(line.indexOf("(Date)") + 6, line.length());
				DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.UK);
				Date convertedDate = format.parse(dateString);
				recieved.add(new RecievedMessage(id, name, text, convertedDate));
			}
			log.debug("RecievedMessages read correctly");

		} catch (FileNotFoundException e) {
			log.error("RecievedMessages.txt file not found", e);
		} catch (ParseException pe) {
			log.error("ParseException occurs while parsing Date object", pe);
		} catch (NumberFormatException e) {
			log.error(
					"Thrown to indicate that the application has attempted to convert a string to one of the numeric types",
					e);
		} catch (IOException e) {
			log.error("IOException occurs", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					log.error("Error while trying to close bufferedreader stream: ", e);
				}
			}
		}
	}

	protected class RecievedMessage {
		private long id;
		private String text;
		private String senderName;
		private Date date;

		public RecievedMessage(long id, String senderName, String text, Date date) {
			this.text = text;
			this.id = id;
			this.senderName = senderName;
			this.date = date;
		}

		public String getText() {
			return text;
		}

		public long getId() {
			return id;
		}

		public String getSenderName() {
			return senderName;
		}

		public Date getDate() {
			return date;
		}
	}

	protected class SentMessage {
		private long id;
		private String text;
		private String recipientName;
		private Date date;

		public SentMessage(long id, String recipientName, String text, Date date) {
			this.text = text;
			this.id = id;
			this.recipientName = recipientName;
			this.date = date;
		}

		public String getText() {
			return text;
		}

		public long getId() {
			return id;
		}

		public String getRecipientName() {
			return recipientName;
		}

		public Date getDate() {
			return date;
		}
	}

	protected class Conversation {
		private Date date;
		private String text;
		private boolean sent; // true - send message, false - received message

		Conversation(Date date, String text, boolean sent) {
			this.date = date;
			this.text = text;
			this.sent = sent;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public boolean isSent() {
			return sent;
		}

		public void setSent(boolean sent) {
			this.sent = sent;
		}
	}

}