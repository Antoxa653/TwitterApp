package twitter.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.internal.logging.Logger;

public class FriendList {
	private final String friendListFileLocation = "target/classes/FriendList.txt";
	private Logger log = Logger.getLogger(FriendList.class.getClass());
	private Twitter twitter;
	private LinkedHashSet<Friend> friendList = new LinkedHashSet<Friend>();

	public FriendList(Twitter twitter) {
		this.twitter = twitter;
		ResourceFilesChecker file = new ResourceFilesChecker();
		boolean exist = file.isFriendListFileExist();
		if (exist) {
			readFriendListFile();
		}
		else {
			createFriendList();
			createFriendListFile();
		}
	}

	public LinkedHashSet<Friend> getFriendList() {
		return friendList;
	}

	private void createFriendList() {
		try {
			long[] friendsIDs = twitter.getFriendsIDs(-1).getIDs();
			for (int i = 0; i < friendsIDs.length; i += 100) {
				int startIndex = i;
				int endIndex = i + 100;
				long[] temp;
				temp = Arrays.copyOfRange(friendsIDs, startIndex, endIndex);
				ResponseList<User> users = twitter.lookupUsers(temp);
				for (User u : users) {
					friendList.add(new Friend(u.getId(), u.getName(), u.getScreenName()));
				}
			}

		} catch (TwitterException te) {
			log.error("Twitter Exception :" + te.getStatusCode(), te);
		}
	}

	public void deleteFriend(String selectedFriend) {
		ResourceFilesChecker file = new ResourceFilesChecker();
		boolean exist = file.isFriendListFileExist();
		if (exist) {
			try {
				String selectedFriendName = selectedFriend.substring(0, selectedFriend.indexOf("@")).trim();
				for (Friend f : friendList) {
					if (f.getName().equals(selectedFriendName)) {
						twitter.destroyFriendship(f.getId());
						friendList.remove(f);
						break;
					}
				}
				File friendListFile = new File("FriendList.txt");
				friendListFile.delete();
				createFriendListFile();
			} catch (TwitterException te) {
				log.error("Twitter Exception :" + te.getStatusCode(), te);
			}
		}
		else {
			log.error("FriendFile missing pls restart the application");
		}
	}

	public void addFriend(String newFriendName) {
		ResourceFilesChecker file = new ResourceFilesChecker();
		boolean exist = file.isFriendListFileExist();
		if (exist) {
			PrintWriter pw = null;
			try {
				String friendsScreenName = newFriendName.substring(0, newFriendName.length());
				twitter.createFriendship(friendsScreenName);

				long friendId = twitter.showUser(friendsScreenName).getId();
				String friendName = twitter.showUser(friendsScreenName).getName();
				friendList.add(new Friend(friendId, friendName, friendsScreenName));

				pw = new PrintWriter(new FileOutputStream("FriendList.txt", true));
				pw.println(friendId + " " + friendName + "@" + friendsScreenName);
			} catch (TwitterException te) {
				log.error("Twitter Exception :" + te.getStatusCode(), te);
			} catch (FileNotFoundException e) {
				log.error("FriendList.txt file not found :", e);
				e.printStackTrace();
			}

			finally {
				if (pw != null) {
					pw.close();
				}

			}
		}
		else {
			log.error("FriendFile missing pls restart the application");
		}
	}

	private void createFriendListFile() {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileOutputStream(friendListFileLocation));
			for (Friend f : friendList) {
				pw.println(f.getId() + " " + f.getName() + "@" + f.getScreenName());
			}
		} catch (FileNotFoundException e) {
			log.warn("Can't create file");
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	private void readFriendListFile() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(friendListFileLocation));
			String line;
			while ((line = br.readLine()) != null) {
				line.trim();
				long id = Long.parseLong(line.substring(0, line.indexOf(" ")));
				String name = line.substring(line.indexOf(" "), line.indexOf("@")).trim();
				String screenName = line.substring(line.indexOf("@") + 1, line.length());
				friendList.add(new Friend(id, name, screenName));
			}
			log.debug("FriendList.txt file read correctly");
		} catch (FileNotFoundException e) {
			log.error("FriendList file not found", e);
		} catch (IOException i) {
			log.error("FriendList file not found", i);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					log.error("Stream cant be close in finally block", e);
				}
			}
		}
	}

	protected class Friend {
		private long id;
		private String name;
		private String screenName;

		Friend(long id, String name, String screenName) {
			if (name == null | screenName == null) {
				throw new IllegalArgumentException(
						"Parameter name and screenName should not be null or empty. Current value is " + name + " "
								+ screenName);
			}
			else {

			}
			this.id = id;
			this.name = name.trim();
			this.screenName = screenName.trim();
		}

		public long getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public void setId(long id) {
			this.id = id;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getScreenName() {
			return screenName;
		}

		public void setScreenName(String screenName) {
			this.screenName = screenName;
		}
	}
}
