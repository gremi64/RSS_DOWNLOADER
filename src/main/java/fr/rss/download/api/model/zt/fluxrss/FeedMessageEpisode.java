package fr.rss.download.api.model.zt.fluxrss;

/*
 * Represents one RSS message
 */
public class FeedMessageEpisode extends FeedMessage {

	String quality;
	String langage;
	String saison;
	String episode;

	public FeedMessageEpisode() {
		super();
	}

	public FeedMessageEpisode(FeedMessage feedMessage) {
		super(feedMessage.getTitle(), feedMessage.getLink(), feedMessage.getDescription(), feedMessage.getCategory(),
				feedMessage.getPubDate());
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public String getLangage() {
		return langage;
	}

	public void setLangage(String langage) {
		this.langage = langage;
	}

	public String getSaison() {
		return saison;
	}

	public void setSaison(String saison) {
		this.saison = saison;
	}

	public String getEpisode() {
		return episode;
	}

	public void setEpisode(String episode) {
		this.episode = episode;
	}

	@Override
	public String toString() {
		return "FeedMessageEpisode [title=" + title + ", link=" + link + ", category=" + category + ", pubDate="
				+ pubDate + ", quality=" + quality + ", langage=" + langage + ", saison=" + saison + ", episode="
				+ episode + ", description=" + description + "]";
	}

}