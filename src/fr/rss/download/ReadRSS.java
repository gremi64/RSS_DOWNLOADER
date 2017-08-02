package fr.rss.download;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.rss.download.model.Feed;
import fr.rss.download.model.FeedMessage;
import fr.rss.download.model.FeedMessageEpisode;
import fr.rss.download.read.RSSFeedParser;

public class ReadRSS {

	private static final String PATTERN_QUALITE_LANGAGE_EPISODE_SAISON = ".*? Qualité (.*?) \\| (.*?)  Episode (\\d+) \\| Saison (\\d+) ";
	private static List<String> listTVShow = new ArrayList<String>();

	public static void main(String[] args) throws Exception {

		listTVShow.add("In the Dark");
		listTVShow.add("Will");

		for (int i = 1; i <= 1; i++) {
			try {
				String urlHost = "www.zone-telechargement.ws";
				String urlRss = "http://" + urlHost + "/telecharger-series/rss.xml";

				RSSFeedParser parser = new RSSFeedParser(urlHost, urlRss);
				Feed feed = parser.readFeed();

				System.out.println(feed);
				if (feed == null) {
					throw new Exception("Feed null, impossible de continuer");
				}

				if (feed.getMessages() == null) {
					throw new Exception("Le feed ne contient aucun message, impossible de continuer");
				}

				List<FeedMessage> listFeedMessage = new ArrayList<FeedMessage>();

				for (FeedMessage message : feed.getMessages()) {
					System.out.println(message);
					if (isItInMyTVShowList(message.getTitle())) {
						listFeedMessage.add(message);
					}
				}

				System.out.println("--------------------------------");
				System.out.println("-------------TRI----------------");
				System.out.println("--------------------------------");

				List<FeedMessageEpisode> listEpisode = getFullEpisodes(listFeedMessage);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static List<FeedMessageEpisode> getFullEpisodes(List<FeedMessage> listFeedMessage) {
		List<FeedMessageEpisode> listEpisode = new ArrayList<>();

		for (FeedMessage feedMessage : listFeedMessage) {
			// System.out.println(feedMessage);

			String line = feedMessage.getDescription();
			Pattern pattern = Pattern.compile(PATTERN_QUALITE_LANGAGE_EPISODE_SAISON);
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				// System.out.println("Title : " + feedMessage.getTitle());
				// System.out.println("Qualité : " + matcher.group(1));
				// System.out.println("Langue : " + matcher.group(2));
				// System.out.println("Episode : " + matcher.group(3));
				// System.out.println("Saison : " + matcher.group(4));

				FeedMessageEpisode feedMessageEpisode = new FeedMessageEpisode(feedMessage);
				feedMessageEpisode.setQuality(matcher.group(1));
				feedMessageEpisode.setLangage(matcher.group(2));
				feedMessageEpisode.setSaison(matcher.group(4));
				feedMessageEpisode.setEpisode(matcher.group(3));

				System.out.println(feedMessageEpisode);

				listEpisode.add(feedMessageEpisode);
			}

		}
		return listEpisode;
	}

	public static boolean isItInMyTVShowList(String title) {
		for (String tvShow : listTVShow) {
			if (title.contains(tvShow)) {
				return true;
			}
		}
		return false;
	}

}