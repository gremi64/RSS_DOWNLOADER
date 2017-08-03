package fr.rss.download.model;

public class EpisodeFournisseur {
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
		return "EpisodeFournisseur [name=" + name + ", url=" + url + "]";
	}
}
