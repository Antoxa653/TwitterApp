package twitter.app;

import java.io.File;

public class LogOut {
	public void doLogout() {
		File f = new File("FriendList.txt");
		f.delete();
		File s = new File("twitter4j.properties");
		s.delete();
		System.exit(0);
	}

}
