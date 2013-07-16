package twitter.app;

import java.util.Map;

import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.internal.logging.Logger;

public class RateLimitation {
	public static final Logger LOG = Logger.getLogger(RateLimitation.class);
	private Twitter twitter;
	private Map<String, RateLimitStatus> rateLimit;

	RateLimitation(Twitter t) {
		this.twitter = t;
		setRateLimit();
	}

	public Map<String, RateLimitStatus> getRateLimit() {
		return rateLimit;
	}

	private void setRateLimit() {
		try {
			rateLimit = twitter.getRateLimitStatus();
		} catch (TwitterException e) {
			LOG.error("Error while trying to get rate limits: " + e.getStatusCode() + " " + e);

		}
	}

	public int checkLimitStatusForEndpoint(String str) {
		int limit = 180;
		for (String endpoint : rateLimit.keySet()) {
			if (endpoint.equals(str)) {
				RateLimitStatus status = rateLimit.get(endpoint);
				LOG.debug("Endpoint: " + endpoint);
				LOG.debug("Remaining: " + status.getRemaining());
				limit = status.getRemaining();
				break;
			}
		}
		return limit;
	}

	public void checkLimitStatusForEndpoint() {
		for (String endpoint : rateLimit.keySet()) {
			RateLimitStatus status = rateLimit.get(endpoint);
			LOG.debug("Endpoint: " + endpoint);
			LOG.debug("Limit: " + status.getLimit());
			LOG.debug("Remaining: " + status.getRemaining());
			LOG.debug("ResetTimeInSeconds: " + status.getResetTimeInSeconds());
			LOG.debug("SecondsUntilReset: " + status.getSecondsUntilReset());
		}
	}
}
