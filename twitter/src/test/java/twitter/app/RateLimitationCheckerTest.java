package twitter.app;

import org.junit.Before;
import org.junit.Test;

import twitter4j.Twitter;

public class RateLimitationCheckerTest {
	private Twitter twitter;
	private String testWrongEndpointName = "/test/endpoint";	

	@Before
	public void testSetup() {
		twitter = new TwitterInstance().readProperties();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCheckLimitStatusForEndpointWrongEndpoint() {
		RateLimitationChecker checker = new RateLimitationChecker(twitter);
		checker.checkLimitStatusForEndpoint(testWrongEndpointName);
	}
}
