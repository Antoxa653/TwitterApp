package twitter.app;

import static org.junit.Assert.assertNotNull;

import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import twitter4j.Twitter;

public class FriendListTest {
	private static Twitter twitter;

	@BeforeClass
	public static void testSetup() {
		TwitterInstance tInstance = new TwitterInstance();
		twitter = tInstance.readProperties();
	}

	@Test
	public void testGetFriendList() {
		FriendList f = new FriendList(twitter);
		f.updateFriendList();
		Set<twitter.app.FriendList.Friend> list = f.getFriendList();
		for (twitter.app.FriendList.Friend friend : list) {
			assertNotNull("Friends Id must be not null", friend.getId());
			assertNotNull("Friends Name must be not null", friend.getName());
			assertNotNull("Friends ScreenName must be not null", friend.getScreenName());
		}
	}
}
