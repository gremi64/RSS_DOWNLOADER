package fr.rss.download.api.model.zt.tvshow;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class TVShow {

	@JsonProperty(value = "name")
	private String name;

	@JsonProperty(value = "qualite")
	private String qualite;

	@JsonProperty(value = "langue")
	private String langue;

	@JsonProperty(value = "tvShowSeason")
	private List<TVShowSeason> tvShowSeason;

	public TVShow() {
		this("name", "link", "qualite");
	}

	public TVShow(String name, String qualite, String langue) {
		this.name = name;
		this.qualite = qualite;
		this.langue = langue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQualite() {
		return qualite;
	}

	public void setQualite(String qualite) {
		this.qualite = qualite;
	}

	public String getLangue() {
		return langue;
	}

	public void setLangue(String langue) {
		this.langue = langue;
	}

	public List<TVShowSeason> getTvShowSeason() {
		if (tvShowSeason == null) {
			tvShowSeason = new ArrayList<>();
		}
		return tvShowSeason;
	}

	public void setTvShowSeason(List<TVShowSeason> tvShowSeason) {
		this.tvShowSeason = tvShowSeason;
	}

	public void addTvShowSeason(TVShowSeason tvShowSeason) {
		if (this.tvShowSeason == null) {
			this.tvShowSeason = new ArrayList<>();
		}
		this.tvShowSeason.add(tvShowSeason);
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("\n");
		str.append("\t").append("\t").append(name).append("\n");
		str.append("\t").append("\t").append(qualite).append("\n");
		str.append("\t").append("\t").append(langue).append("\n");
		str.append("\t").append("\t").append("tvShowSeason = " + tvShowSeason);
		return str.toString();
	}

}