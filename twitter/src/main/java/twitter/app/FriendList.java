package twitter.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class FriendList {

	private Logger log = Logger.getLogger(getClass().getName());
	private Twitter twitter;
	private Set<Friend> friendList = new LinkedHashSet<Friend>();

	public FriendList(Twitter twitter) {
		this.twitter = twitter;
		updateFriendList();
	}

	public Set<Friend> getFriendList() {
		return friendList;
	}

	public final void updateFriendList() {		
		//int remainingLookUpLimit = new RateLimitationChecker(twitter).checkLimitStatusForEndpoint("/users/lookup");
		int remainingGetFriendsIdsLimit = new RateLimitationChecker(twitter).checkLimitStatusForEndpoint("/friends/ids");

		if (remainingGetFriendsIdsLimit > 2) {
			initializeFriendList();
		}
		if (remainingGetFriendsIdsLimit == 2) {
			initializeFriendList();
			saveFriendListToFile(new ResourceFilesPath().getFriendlistFile());
		}
		if (remainingGetFriendsIdsLimit < 2) {
			readFriendListFromFile(new ResourceFilesPath().getFriendlistFile());
		}
	}

	private void initializeFriendList() {
		friendList.clear();
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
			log.debug("friendList initialized correctly");
		} catch (TwitterException te) {
			log.error("Twitter Exception whyle trying yo update friendList :", te);
		}
	}

	public void deleteFriend(String selectedFriend) {
		ResourceFilesChecker file = new ResourceFilesChecker();
		boolean exist = ResourceFilesChecker.isFileExist(new ResourceFilesPath().getFriendlistFile());
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
				File friendListFile = new File(selectedFriend);
				friendListFile.delete();
				saveFriendListToFile(new ResourceFilesPath().getFriendlistFile());
			} catch (TwitterException te) {
				log.error("Twitter Exception :" + te.getStatusCode(), te);
			}
		}
		else {
			log.error("FriendFile missing pls restart the application");
		}
	}

	public void addFriend(String newFriendName) {
		boolean exist = ResourceFilesChecker.isFileExist(new ResourceFilesPath().getFriendlistFile());
		if (exist) {
			PrintWriter pw = null;
			try {
				String friendsScreenName = newFriendName.substring(0, newFriendName.length());
				twitter.createFriendship(friendsScreenName);

				long friendId = twitter.showUser(friendsScreenName).getId();
				String friendName = twitter.showUser(friendsScreenName).getName();
				friendList.add(new Friend(friendId, friendName, friendsScreenName));

				pw = new PrintWriter(new FileOutputStream(newFriendName, true));
				pw.println(friendId + " " + friendName + "@" + friendsScreenName);
			} catch (TwitterException te) {
				log.error("Twitter Exception :" + te.getStatusCode(), te);
			} catch (FileNotFoundException e) {
				log.error("FriendList.txt file not found :", e);
				e.printStackTrace();
			} finally {
				if (pw != null) {
					pw.close();
				}
			}
		}
		else {
			log.error("FriendFile missing pls restart the application");
		}
	}

	private void saveFriendListToFile(String filePath) {
		File file = new File(filePath);
		file.getParentFile().mkdirs();
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(file, "UTF-8");
			for (Friend f : friendList) {
				pw.println(f.getId() + " " + f.getName() + "@" + f.getScreenName());
			}
		} catch (FileNotFoundException e) {
			log.warn("Can't create file");
		} catch (UnsupportedEncodingException e) {
			log.error("Unsupported encoding type exception using while creating FriendList.txt file", e);

		} finally {
			if (pw != null) {
				pw.flush();
			}
		}
	}

	private void readFriendListFromFile(String filePath) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filePath));
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
