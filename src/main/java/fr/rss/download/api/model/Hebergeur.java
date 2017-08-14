package fr.rss.download.api.model;

import java.util.ArrayList;
import java.util.List;

public class Hebergeur {
	String name;
	List<EpisodeHebergeur> episodes;

	public Hebergeur() {
		episodes = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<EpisodeHebergeur> getEpisodes() {
		return episodes;
	}

	public void setEpisodes(List<EpisodeHebergeur> episodes) {
		this.episodes = episodes;
	}

	@Override
	public String toString() {
		return "Hebergeur [" + name + ", " + episodes + "]";
	}

}
