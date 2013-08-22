package twitter.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.internal.logging.Logger;

public class RateLimitationChecker {
	private Logger log = Logger.getLogger(RateLimitationChecker.class);
	private Twitter twitter;
	private Map<String, RateLimitStatus> rateLimits;

	RateLimitationChecker(Twitter t) {
		this.twitter = t;
		try {
			rateLimits = twitter.getRateLimitStatus();
			//checkLimitStatusForEndpoints();

		} catch (TwitterException e) {
			log.error("Error while trying to get rate limits: ", e);

		}		
	}

	public Map<String, RateLimitStatus> getRateLimits() {
		return rateLimits;
	}
	
	public int checkLimitStatusForEndpoint(String str) {
		int limit = -1;
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
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File("C://endpointsLimits.txt"));
			for (String endpoint : rateLimits.keySet()) {
				RateLimitStatus status = rateLimits.get(endpoint);
				pw.println("Endpoint: " + endpoint + " Limit: " + status.getLimit() + " Remaining: "
						+ status.getRemaining() + " ResetTimeInSeconds: " + status.getResetTimeInSeconds()
						+ " SecondsUntilReset: " + status.getSecondsUntilReset());
			}
		} catch (FileNotFoundException e) {
			log.error("File C://endpointsLimits.txt no found ", e);
		} finally {
			pw.close();
		}

	}
}
