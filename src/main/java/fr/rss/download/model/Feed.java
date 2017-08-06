package fr.rss.download.model;

import java.util.ArrayList;
import java.util.List;

/*
 * Stores an RSS feed
 */
public class Feed {

	String title;
	String link;
	String language;
	String description;

	List<FeedMessage> entries = new ArrayList<FeedMessage>();

	public Feed(String title, String link, String language, String description) {
		this.title = title;
		this.link = link;
		this.language = language;
		this.description = description;
	}

	public List<FeedMessage> getMessages() {
		return entries;
	}

	public String getTitle() {
		return title;
	}

	public String getLink() {
		return link;
	}

	public String getDescription() {
		return description;
	}

	public String getLanguage() {
		return language;
	}

	@Override
	public String toString() {
		return "Feed [title=" + title + ", link=" + link + ", language=" + language + ", description=" + description
				+ "]";
	}

}