package twitter.app;

import java.io.File;

public class CheckAuthorizationProperties {
	public boolean check(){
		File file = new File("F:\\git\\TwitterApp\\twitter\\twitter4j.properties");
		boolean exists = file.exists();
		return exists;
	}
}
