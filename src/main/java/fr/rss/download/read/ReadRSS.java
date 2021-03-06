package fr.rss.download.read;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.rss.download.api.model.zt.fluxrss.FeedMessage;
import fr.rss.download.api.model.zt.fluxrss.FeedMessageEpisode;

public class ReadRSS {

	private static final Logger log = LoggerFactory.getLogger(ReadRSS.class);

	private static final String PATTERN_QUALITE_LANGAGE_EPISODE_SAISON = ".*? Qualité (.*?) \\| (.*?)  Episode (\\d+) \\| Saison (\\d+) ";
	private static List<String> listTVShow = new ArrayList<String>();
	private static String myQuality = "HDTV";
	// private static String myQuality = "HD720P";
	private static String myLangage = "VOSTFR";
	// private static String myLangage = "FRENCH";

	//	public static void main(String[] args) throws Exception {
	//
	//		listTVShow.add("Game of Thrones");
	//		listTVShow.add("In the Dark");
	//		listTVShow.add("Snowfall");
	//		listTVShow.add("The Fosters");
	//		listTVShow.add("Flash (2014)");
	//		listTVShow.add("Killjoys");
	//		listTVShow.add("Demain nous appartient");
	//		listTVShow.add("Will");
	//
	//		String urlHost = "www.zone-telechargement.ws";
	//		String urlRss = "http://" + urlHost + "/telecharger-series/rss.xml";
	//
	//		RSSFeedParser parser = new RSSFeedParser(urlHost, urlRss);
	//		Feed feed = parser.readFeed();
	//
	//		if (feed == null) {
	//			throw new Exception("Feed null, impossible de continuer");
	//		}
	//
	//		log.debug(feed.toString());
	//
	//		if (feed.getMessages() == null) {
	//			throw new Exception("Le feed ne contient aucun message, impossible de continuer");
	//		}
	//
	//		List<FeedMessage> listFeedMessage = new ArrayList<FeedMessage>();
	//
	//		for (FeedMessage feedMessage : feed.getMessages()) {
	//			log.debug(feedMessage.toString());
	//			if (isItInMyTVShowList(feedMessage.getTitle())) {
	//				listFeedMessage.add(feedMessage);
	//			}
	//		}
	//
	//		log.debug("--------------------------------" + "\n" + "-------------TRI----------------" + "\n" + "--------------------------------");
	//
	//		List<FeedMessageEpisode> listEpisodes = getFullEpisodesOnly(listFeedMessage);
	//
	//		listEpisodes = getMyLangageAndQuality(listEpisodes);
	//
	//		for (FeedMessageEpisode feedMessageEpisode : listEpisodes) {
	//			log.debug(feedMessageEpisode.toString());
	//			//			TvShowHtmlParser tvShowHtmlParser = new TvShowHtmlParser(feedMessageEpisode.getLink());
	//		}
	//
	//	}

	private static List<FeedMessageEpisode> getMyLangageAndQuality(List<FeedMessageEpisode> listEpisodes) {
		ArrayList<FeedMessageEpisode> myListEpisodes = new ArrayList<FeedMessageEpisode>();
		for (FeedMessageEpisode feedMessageEpisode : listEpisodes) {
			if (myLangage.equals(feedMessageEpisode.getLangage()) && myQuality.equals(feedMessageEpisode.getQuality())) {
				myListEpisodes.add(feedMessageEpisode);
			}
		}
		return myListEpisodes;
	}

	public static List<FeedMessageEpisode> getFullEpisodesOnly(List<FeedMessage> listFeedMessage) {
		List<FeedMessageEpisode> listEpisode = new ArrayList<>();

		for (FeedMessage feedMessage : listFeedMessage) {
			String line = feedMessage.getDescription();
			Pattern pattern = Pattern.compile(PATTERN_QUALITE_LANGAGE_EPISODE_SAISON);
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				FeedMessageEpisode feedMessageEpisode = new FeedMessageEpisode(feedMessage);
				feedMessageEpisode.setQuality(matcher.group(1).toUpperCase().trim().replace(" ", ""));
				feedMessageEpisode.setLangage(matcher.group(2).toUpperCase().trim().replace(" ", ""));
				feedMessageEpisode.setSaison(matcher.group(4).toUpperCase().trim().replace(" ", ""));
				feedMessageEpisode.setEpisode(matcher.group(3).toUpperCase().trim().replace(" ", ""));
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