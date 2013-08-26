package twitter.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

import org.apache.log4j.Logger;

import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class RateLimitationChecker {
	private Logger log = Logger.getLogger(getClass().getName());
	private Twitter twitter;
	private Map<String, RateLimitStatus> rateLimits;

	RateLimitationChecker(Twitter tTwitter) {
		this.twitter = tTwitter;
	}

	public int checkLimitStatusForEndpoint(String str) {
		int limit = -1;
		try {
			rateLimits = twitter.getRateLimitStatus();

			for (String endpoint : rateLimits.keySet()) {
				if (endpoint.equals(str)) {
					RateLimitStatus status = rateLimits.get(endpoint);
					log.debug("Endpoint: " + endpoint);
					log.debug("Remaining: " + status.getRemaining());
					limit = status.getRemaining();
					break;
				}
			}
		} catch (TwitterException e) {
			log.debug("FUCK");
		}
		return limit;
	}

	public void checkLimitStatusForEndpoints() {
		PrintWriter pw = null;
		try {
			rateLimits = twitter.getRateLimitStatus();
			pw = new PrintWriter(new File("C://endpointsLimits.txt"));
			for (String endpoint : rateLimits.keySet()) {
				RateLimitStatus status = rateLimits.get(endpoint);
				pw.println("Endpoint: " + endpoint + " Limit: " + status.getLimit() + " Remaining: "
						+ status.getRemaining() + " ResetTimeInSeconds: " + status.getResetTimeInSeconds()
						+ " SecondsUntilReset: " + status.getSecondsUntilReset());
			}
		} catch (FileNotFoundException e) {
			log.error("File C://endpointsLimits.txt no found ", e);
		} catch (TwitterException e) {
			log.debug("Error while updating Map<String,RateLimitStatus> rateLimits ", e);
		} finally {
			pw.close();
		}
	}
}
