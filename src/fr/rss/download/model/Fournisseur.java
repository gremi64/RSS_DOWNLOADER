package fr.rss.download.model;

import java.util.ArrayList;
import java.util.List;

public class Fournisseur {
	String name;
	List<EpisodeFournisseur> episodes;

	public Fournisseur() {
		this.episodes = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<EpisodeFournisseur> getEpisodes() {
		return episodes;
	}

	public void setEpisodes(List<EpisodeFournisseur> episodes) {
		this.episodes = episodes;
	}

	@Override
	public String toString() {
		return "Fournisseur [name=" + name + ", episodes=" + episodes + "]";
	}

}
