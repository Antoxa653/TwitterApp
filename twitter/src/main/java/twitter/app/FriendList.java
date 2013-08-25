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

import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.internal.logging.Logger;

public class FriendList {
	private final String friendListFileLocation = System.getProperty("user.home") + "/TwitterApplication"
			+ "/FriendList.txt";
	private Logger log = Logger.getLogger(FriendList.class.getClass());
	private Twitter twitter;
	private Set<Friend> friendList = new LinkedHashSet<Friend>();
	private ResourceFilesChecker file;
	private int userFriendNumber;
	private RateLimitationChecker rateLimit;

	public FriendList(Twitter twitter) {
		this.twitter = twitter;
		file = new ResourceFilesChecker();
		rateLimit = new RateLimitationChecker(twitter);
		int updateRateLimit = rateLimit.checkLimitStatusForEndpoint("/friends/list");		
		boolean exist = file.isFriendListFileExist();
		if (exist) {
			if (getCurrentNumberOfFriends(friendListFileLocation) != getFriendsCount() && updateRateLimit >= 2) {
				createFriendList();
				createFriendListFile(friendListFileLocation);
			}
			else {
				readFriendListFile(friendListFileLocation);
			}

		}
		else {
			createFriendList();
			createFriendListFile(friendListFileLocation);
		}
	}

	public Set<Friend> getFriendList() {
		return friendList;
	}

	private int getFriendsCount() {
		int friendNumber = -1;
		int showLimit = rateLimit.checkLimitStatusForEndpoint("/users/show/:id");
		int getIdLimit = rateLimit.checkLimitStatusForEndpoint("/statuses/home_timeline");

		if (getIdLimit >= 2 && showLimit >= 2 ) {
			try {
				long id = twitter.getId();
				friendNumber = twitter.showUser(id).getFriendsCount();
			} catch (IllegalStateException e) {
				log.error(
						"Java environment or Java application is not in an appropriate state for the requested operation",
						e);
			} catch (TwitterException e) {
				log.error("Error while try to get Number of Authendificated user favorites", e);
			}
		}

		return friendNumber;

	}

	private void createFriendList() {
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

		} catch (TwitterException te) {
			log.error("Twitter Exception :" + te.getStatusCode(), te);
		}

	}

	public void deleteFriend(String selectedFriend) {
		File userDir = new File(friendListFileLocation);
		userDir.mkdirs();
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
				File friendListFile = new File(userDir + "/FriendList.txt");
				friendListFile.delete();
				createFriendListFile(friendListFileLocation);
			} catch (TwitterException te) {
				log.error("Twitter Exception :" + te.getStatusCode(), te);
			}
		}
		else {
			log.error("FriendFile missing pls restart the application");
		}
	}

	public void addFriend(String newFriendName) {
		File userDir = new File(friendListFileLocation);
		userDir.mkdirs();
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

				pw = new PrintWriter(new FileOutputStream(userDir + "/FriendList.txt", true));
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

	private void createFriendListFile(String filePath) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(filePath, "UTF-8");
			pw.println(getFriendsCount());
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

	private int getCurrentNumberOfFriends(String filePath) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filePath));
			//Read friends number
			userFriendNumber = Integer.parseInt(br.readLine());
			log.debug("FriendList.txt number of friends read correctly");
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
		return userFriendNumber;
	}

	private void readFriendListFile(String filePath) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filePath));
			//Skip line with friends number info
			br.readLine();
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

	public void updateFriendList() {
		int remainingLimit = rateLimit.checkLimitStatusForEndpoint("/friends/list");
		if (file.isFriendListFileExist()) {
			if (getCurrentNumberOfFriends(friendListFileLocation) == getFriendsCount()) {
				return;
			}
			else {
				if (remainingLimit >= 2) {
					createFriendList();
					createFriendListFile(friendListFileLocation);
				}
			}
		}
		else {
			if (remainingLimit >= 2) {
				createFriendList();
				createFriendListFile(friendListFileLocation);
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
