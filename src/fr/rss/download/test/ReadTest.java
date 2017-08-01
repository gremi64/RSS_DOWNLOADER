package fr.rss.download.test;

import fr.rss.download.model.Feed;
import fr.rss.download.model.FeedMessage;
import fr.rss.download.read.RSSFeedParser;

public class ReadTest {
	public static void main(String[] args) {

		RSSFeedParser parser = new RSSFeedParser("https://www.zone-telechargement.ws/telecharger-series/rss.xml");
		Feed feed = parser.readFeed();
		System.out.println(feed);

		for (FeedMessage message : feed.getMessages()) {
			System.out.println(message);
		}
	}
}