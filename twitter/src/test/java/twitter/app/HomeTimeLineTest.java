package twitter.app;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import twitter4j.MediaEntity;
import twitter4j.Twitter;
import twitter4j.URLEntity;

public class HomeTimeLineTest {
	private Twitter twitter;

	@Before
	public void testSetup() {

	}

	@Test
	public void test() {
		HomeTimeLine homeTimeLine = new HomeTimeLine();
		List<String> tweetText = initTweetText();
		List<URLEntity[]> tweetURLs = initTweetURLs();
		List<MediaEntity[]> tweetMedia = initTweetMedia();
		List<String> tweetExpectedText = expectTweetText();
		for (int i = 0; i < tweetText.size(); i++) {
			assertEquals("tweetText.get[" + i + "] expect tweetExpectText[" + i + "]", tweetExpectedText.get(i),
					homeTimeLine.parseStatusText(tweetText.get(i), tweetURLs.get(i), tweetMedia.get(i)));
		}

	}

	private List<String> initTweetText() {
		List<String> tweetText = new ArrayList<String>();
		tweetText.add("SomeText");
		tweetText.add("Какой-то текст");
		tweetText.add("@ScreenName SomeText");
		tweetText.add("@ScreenName Какой-то текст");
		tweetText.add("@ScreenName SomeText!? SomeText:?#$@!%$^%^&*(*&) ");
		tweetText.add("@ScreenName http://translate.google.ru/ SomeText");
		tweetText.add("RT @TobiWanDOTA: @v1lat good to see you waiting until the official release time like always :/");
		tweetText
				.add("@ScreenName@ScreenName @ScreenName SomeText: http://translate.google.ru/http://translate.google.ru/");
		tweetText
				.add("@ScreenName@ScreenName @ScreenName SomeText: http://translate.google.ru/http://translate.google.ru/  http://translate.google.ru/");
		tweetText
				.add("@ScreenName https://twitter.com/HERALDOVILLA/status/373073670471974913/photo/1 SomeText https://twitter.com/HERALDOVILLA/status/373073670471974913/photo/1 SomeText");

		return tweetText;
	}

	private List<URLEntity[]> initTweetURLs() {
		List<URLEntity[]> tweetURLs = new ArrayList<URLEntity[]>();
		for (int i = 0; i < 10; i++) {
			tweetURLs.add(new URLEntity[0]);
		}
		return tweetURLs;
	}

	private List<MediaEntity[]> initTweetMedia() {
		List<MediaEntity[]> tweetMedia = new ArrayList<MediaEntity[]>();
		for (int i = 0; i < 10; i++) {
			tweetMedia.add(new MediaEntity[0]);
		}
		return tweetMedia;
	}

	private List<String> expectTweetText() {
		List<String> tweetExpectedText = new ArrayList<String>();
		tweetExpectedText.add("SomeText");
		tweetExpectedText.add("Какой-то текст");
		tweetExpectedText.add("@ScreenName SomeText");
		tweetExpectedText.add("@ScreenName Какой-то текст");
		tweetExpectedText.add("@ScreenName SomeText!? SomeText:?#$@!%$^%^&*(*&) ");
		tweetExpectedText.add("@ScreenName SomeText");
		tweetExpectedText.add("@v1lat good to see you waiting until the official release time like always :/");
		tweetExpectedText.add("@ScreenName@ScreenName @ScreenName SomeText ");
		tweetExpectedText.add("@ScreenName@ScreenName @ScreenName SomeText");
		tweetExpectedText.add("@ScreenName SomeText SomeText");
		return tweetExpectedText;
	}

}
