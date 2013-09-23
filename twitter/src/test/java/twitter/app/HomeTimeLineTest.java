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
		assertEquals("tweetText.get[0] expect tweetExpectText[0]", tweetExpectedText.get(0),
				homeTimeLine.parseStatusText(tweetText.get(0), tweetURLs.get(0), tweetMedia.get(0)));
		assertEquals("tweetText.get[1] expect tweetExpectText[1]", tweetExpectedText.get(1),
				homeTimeLine.parseStatusText(tweetText.get(1), tweetURLs.get(1), tweetMedia.get(1)));
		assertEquals("tweetText.get[2] expect tweetExpectText[2]", tweetExpectedText.get(2),
				homeTimeLine.parseStatusText(tweetText.get(2), tweetURLs.get(2), tweetMedia.get(2)));
		assertEquals("tweetText.get[3] expect tweetExpectText[3]", tweetExpectedText.get(3),
				homeTimeLine.parseStatusText(tweetText.get(3), tweetURLs.get(3), tweetMedia.get(3)));
		assertEquals("tweetText.get[4] expect tweetExpectText[4]", tweetExpectedText.get(4),
				homeTimeLine.parseStatusText(tweetText.get(4), tweetURLs.get(4), tweetMedia.get(4)));
		assertEquals("tweetText.get[5] expect tweetExpectText[5]", tweetExpectedText.get(5),
				homeTimeLine.parseStatusText(tweetText.get(5), tweetURLs.get(5), tweetMedia.get(5)));
		assertEquals("tweetText.get[6] expect tweetExpectText[6]", tweetExpectedText.get(6),
				homeTimeLine.parseStatusText(tweetText.get(6), tweetURLs.get(6), tweetMedia.get(6)));
		assertEquals("tweetText.get[7] expect tweetExpectText[7]", tweetExpectedText.get(7),
				homeTimeLine.parseStatusText(tweetText.get(7), tweetURLs.get(7), tweetMedia.get(7)));
		assertEquals("tweetText.get[8] expect tweetExpectText[8]", tweetExpectedText.get(8),
				homeTimeLine.parseStatusText(tweetText.get(8), tweetURLs.get(8), tweetMedia.get(8)));
		assertEquals("tweetText.get[9] expect tweetExpectText[9]", tweetExpectedText.get(9),
				homeTimeLine.parseStatusText(tweetText.get(9), tweetURLs.get(9), tweetMedia.get(9)));

	}

	private List<String> initTweetText() {
		List<String> tweetText = new ArrayList<String>();
		tweetText.add("SomeText");
		tweetText
				.add("Также в новом номере текст @Alexey_Andronov о кризисе кавказских клубов и материал @KazakovIlya1 о @sergeygalitskiy. http://t.co/oYOlJwkPDh");
		tweetText.add("@ScreenName SomeText");
		tweetText.add("@ScreenName Какой-то текст");
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

	private List<String> expectTweetText() {
		List<String> tweetExpectedText = new ArrayList<String>();
		tweetExpectedText.add("SomeText");
		tweetExpectedText
				.add("Также в новом номере текст @Alexey_Andronov о кризисе кавказских клубов и материал @KazakovIlya1 о @sergeygalitskiy.");
		tweetExpectedText.add("@ScreenName SomeText");
		tweetExpectedText.add("@ScreenName Какой-то текст");
		tweetExpectedText.add("@ScreenName SomeText!? SomeText:?#$@!%$^%^&*(*&)");
		tweetExpectedText.add("@ScreenName SomeText");
		tweetExpectedText.add("@v1lat good to see you waiting until the official release time like always :/");
		tweetExpectedText.add("@ScreenName@ScreenName @ScreenName SomeText");
		tweetExpectedText.add("@ScreenName@ScreenName @ScreenName SomeText");
		tweetExpectedText.add("@ScreenName SomeText SomeText");
		return tweetExpectedText;
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

}
