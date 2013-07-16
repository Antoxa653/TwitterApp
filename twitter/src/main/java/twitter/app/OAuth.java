package twitter.app;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class OAuth {
	public static final Logger LOG = Logger.getLogger(OAuth.class);
	private final String consumerKey = "QrLV7P1izPRAP5YwktX0g";
	private final String consumerSecret = "GXmGXmQblRkVtuuMiH1ZxneKaHt9OX3bdyVzb7i9w";
	private String access;
	private String accessTokenSecret;
	private RequestToken requestToken;
	private AccessToken accessToken;
	private Twitter twitter;

	public OAuth(Twitter twitter) {
		this.twitter = twitter;
		this.twitter.setOAuthConsumer(consumerKey, consumerSecret);
	}

	public String getOAuthAuthorizationURL() {
		try {
			requestToken = twitter.getOAuthRequestToken();
		} catch (TwitterException e) {
			LOG.error("Failed to receive a request token", e);
		}
		LOG.debug("Request token received");
		return requestToken.getAuthorizationURL();
	}

	public boolean OAuthSetup(String pin) {
		boolean complit = true;
		try {
			if (pin.length() > 0) {
				accessToken = twitter.getOAuthAccessToken(requestToken, pin);
			}
			else {
				LOG.debug("No PIN code entered");
				complit = false;
			}
			access = accessToken.getToken();
			accessTokenSecret = accessToken.getTokenSecret();
			print(consumerKey, consumerSecret, access, accessTokenSecret);

		} catch (TwitterException te) {
			LOG.error("Unable to get the access token +" + te.getStatusCode() + " ", te);
			complit = false;
		}
		return complit;
	}

	public boolean spellCheckPIN(String pin) {
		boolean complit = true;
		try {
			int num = Integer.parseInt(pin);
		} catch (NumberFormatException e) {
			LOG.error("Bad number format");
			complit = false;
		}
		return complit;
	}

	public void print(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileOutputStream("twitter4j.properties"));
			pw.println("debug=false");
			pw.println("oauth.consumerKey=" + consumerKey);
			pw.println("oauth.consumerSecret=" + consumerSecret);
			pw.println("oauth.accessToken=" + accessToken);
			pw.println("oauth.accessTokenSecret=" + accessTokenSecret);

		} catch (FileNotFoundException e) {
			LOG.error("Can't create twitter4j.properties", e);

		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}
}
