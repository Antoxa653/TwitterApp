package twitter.app;

import java.util.Map;

import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.internal.logging.Logger;

public class RateLimitationChecker {
	private Logger log = Logger.getLogger(RateLimitationChecker.class);
	private Twitter twitter;
	private Map<String, RateLimitStatus> rateLimits;

	RateLimitationChecker(Twitter t) {
		this.twitter = t;
		setMapOfRateLimits();
	}

	public Map<String, RateLimitStatus> getRateLimits() {
		return rateLimits;
	}

	private void setMapOfRateLimits() {
		try {
			rateLimits = twitter.getRateLimitStatus();
		} catch (TwitterException e) {
			log.error("Error while trying to get rate limits: " + e.getStatusCode() + " " + e);

		}
	}

	public int checkLimitStatusForEndpoint(String str) {
		int limit = 180;
		for (String endpoint : rateLimits.keySet()) {
			if (endpoint.equals(str)) {
				RateLimitStatus status = rateLimits.get(endpoint);
				log.debug("Endpoint: " + endpoint);
				log.debug("Remaining: " + status.getRemaining());
				limit = status.getRemaining();
				break;
			}
		}
		return limit;
	}

	public void checkLimitStatusForEndpoints() {
		for (String endpoint : rateLimits.keySet()) {
			RateLimitStatus status = rateLimits.get(endpoint);
			log.debug("Endpoint: " + endpoint);
			log.debug("Limit: " + status.getLimit());
			log.debug("Remaining: " + status.getRemaining());
			log.debug("ResetTimeInSeconds: " + status.getResetTimeInSeconds());
			log.debug("SecondsUntilReset: " + status.getSecondsUntilReset());
		}
	}
}
