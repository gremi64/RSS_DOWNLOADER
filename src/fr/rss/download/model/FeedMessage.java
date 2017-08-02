package fr.rss.download.model;

/*
 * Represents one RSS message
 */
public class FeedMessage {

	String title;
	String link;
	String description;
	String category;
	String pubDate;

	public FeedMessage() {
	}

	public FeedMessage(String title, String link, String description, String category, String pubDate) {
		super();
		this.title = title;
		this.link = link;
		this.description = description;
		this.category = category;
		this.pubDate = pubDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	@Override
	public String toString() {
		return "FeedMessage [title=" + title + ", link=" + link + ", description=" + description + ", category="
				+ category + ", pubDate=" + pubDate + "]";
	}

}