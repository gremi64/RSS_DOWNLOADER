package fr.rss.download.api.model;

public class EpisodeHebergeur {
	String name;
	String url;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "Episode [" + name + ", " + url + "]";
	}
}
