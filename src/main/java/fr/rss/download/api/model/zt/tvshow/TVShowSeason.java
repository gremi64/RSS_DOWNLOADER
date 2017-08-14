package fr.rss.download.api.model.zt.tvshow;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TVShowSeason {

	@JsonProperty(value = "saison")
	private String saison;

	@JsonProperty(value = "link")
	private String link;

	public TVShowSeason() {
		this("saison", "lien");
	}

	public TVShowSeason(String saison, String link) {
		this.saison = saison;
		this.link = link;
	}

	public String getSaison() {
		return saison;
	}

	public void setSaison(String saison) {
		this.saison = saison;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("\n");
		str.append("\t").append("\t").append("\t").append("saison = " + saison).append("\n");
		str.append("\t").append("\t").append("\t").append("link = " + link);
		return str.toString();
	}

}
