package twitter.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class OAuth {
	private final String consumerKey = "QrLV7P1izPRAP5YwktX0g";
	private final String consumerSecret = "GXmGXmQblRkVtuuMiH1ZxneKaHt9OX3bdyVzb7i9w";
	private String userHomeDir = System.getProperty("user.home") + "/TwitterApplication";
	private Logger log = Logger.getLogger(getClass());
	private String access;
	private String accessTokenSecret;
	private RequestToken requestToken;
	private AccessToken accessToken;
	private Twitter twitter;

	public OAuth(Twitter twitter) {
		this.twitter = twitter;
		this.twitter.setOAuthConsumer(consumerKey, consumerSecret);
	}

	public OAuth() {

	}

	public String getOAuthAuthorizationURL() {
		try {
			requestToken = twitter.getOAuthRequestToken();
		} catch (TwitterException e) {
			log.error("Failed to receive a request token", e);
		}
		log.debug("Request token received");
		return requestToken.getAuthorizationURL();
	}

	public boolean OAuthSetup(String pin) {
		boolean complit = true;
		try {
			if (pin.length() > 0) {
				accessToken = twitter.getOAuthAccessToken(requestToken, pin);
			}
			else {
				log.debug("No PIN code entered");
				complit = false;
			}
			access = accessToken.getToken();
			accessTokenSecret = accessToken.getTokenSecret();
			printProperties(consumerKey, consumerSecret, access, accessTokenSecret);

		} catch (TwitterException te) {
			log.error("Unable to get the access token +" + te.getStatusCode() + " ", te);
			complit = false;
		}
		return complit;
	}

	public void printProperties(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
		File userDir = new File(userHomeDir);
		userDir.mkdirs();
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileOutputStream(userDir + "/twitter4j.properties"));
			pw.println("debug=false");
			pw.println("oauth.consumerKey=" + consumerKey);
			pw.println("oauth.consumerSecret=" + consumerSecret);
			pw.println("oauth.accessToken=" + accessToken);
			pw.println("oauth.accessTokenSecret=" + accessTokenSecret);

		} catch (FileNotFoundException e) {
			log.error("Can't create twitter4j.properties", e);

		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}
}
