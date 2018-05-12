package fr.rss.download.api.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationHome;
import org.springframework.stereotype.Service;

import fr.rss.download.api.constantes.LANGUE;
import fr.rss.download.api.constantes.QUALITE;
import fr.rss.download.api.model.EpisodeHebergeur;
import fr.rss.download.api.model.Hebergeur;
import fr.rss.download.api.model.zt.tvshow.TVShow;
import fr.rss.download.api.model.zt.tvshow.TVShowSeason;
import fr.rss.download.api.service.ITvShowService;
import fr.rss.download.api.utils.XMLTools;

@Service
public class TvShowServiceImpl implements ITvShowService {

	private static final Logger log = LoggerFactory.getLogger(TvShowServiceImpl.class);

	private static final String ZT_LINK = "https://ww2.zone-telechargement1.com";

	List<TVShow> tvShowList;
	String tvShowListFile;

	public TvShowServiceImpl(@Value("${zt.tvshow.filename}") String fileName) {
		tvShowList = new ArrayList<>();
		modifierFichierDestination(fileName);

		File f = new File(tvShowListFile);
		if (!f.exists()) {
			log.debug("Le fichier des séries '{}' n'existe pas encore => CREATION", tvShowListFile);
			creerSerieFichier();
		}
	}

	/**
	 * Parse une page ZT et récupère tous les liens de chaque hébergeur
	 *
	 * @param link
	 * @return
	 */
	@Override
	public List<Hebergeur> parseZtTvShowLink(String link) {
		List<Hebergeur> listFournisseurs = new ArrayList<>();
		try {
			Document document = Jsoup.connect(link)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36")
					.get();

			Elements divClassOnPage = document.select("div.postinfo");
			Elements informations = divClassOnPage.select("b");

			listFournisseurs = new ArrayList<>();
			Hebergeur fournisseur = new Hebergeur();

			for (Element information : informations) {
				// Si on a une div dans le "b"
				// alors c'est le nom du fournisseur
				if (!information.select("div").isEmpty()) {
					fournisseur = new Hebergeur();
					listFournisseurs.add(fournisseur);
					fournisseur.setName(information.text());
				} else if (!information.select("a[href]").isEmpty()) {
					EpisodeHebergeur episode = new EpisodeHebergeur();
					episode.setName(information.text());
					episode.setUrl(information.select("a[href]").attr("href").replaceAll("(\\r|\\n)", ""));
					fournisseur.getEpisodes().add(episode);
				}
			}
			log.debug(listFournisseurs.toString());

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return listFournisseurs;
	}

	/**
	 * Initialisation du fichier si il n'existe pas
	 * ATTENTION : cette méthode effacera tout le contenu du fichier si il existe...
	 *
	 * @param fileName
	 * @return
	 */
	@Override
	public List<TVShow> creerSerieFichier() {
		try {
			// TODO : ajouter ici les séries que vous souhaitez
			TVShow tvShow = new TVShow();

			String gameOfThrones = "Game of Thrones";
			tvShow = new TVShow(gameOfThrones, QUALITE.HD720P.name(), LANGUE.VOSTFR.name());
			tvShow.addTvShowSeason(
					new TVShowSeason("6", ZT_LINK + "/15860-telecharger-game-of-thrones-saison-6-hd-720p-vostfr.html"));
			tvShow.addTvShowSeason(
					new TVShowSeason("7", ZT_LINK + "/26987-game-of-thrones-saison-7-vostfr-hd-720p-streaming.html"));
			tvShowList.add(tvShow);

			tvShow = new TVShow(gameOfThrones, QUALITE.HDTV.name(), LANGUE.VOSTFR.name());
			tvShow.addTvShowSeason(
					new TVShowSeason("6", ZT_LINK + "/15859-game-of-thrones-saison-6-vostfr-hdtv-streaming.html"));
			tvShow.addTvShowSeason(
					new TVShowSeason("7", ZT_LINK + "/26988-game-of-thrones-saison-7-vostfr-hdtv-streaming.html"));
			tvShowList.add(tvShow);

			String fearTheWalkingDead = "Fear The Walking Dead";
			tvShow = new TVShow(fearTheWalkingDead, QUALITE.HDTV.name(), LANGUE.VOSTFR.name());
			tvShow.addTvShowSeason(new TVShowSeason("3", ZT_LINK + "/25432-fear-the-walking-dead-saison-3-hdtv-vostfr.html"));
			tvShowList.add(tvShow);

			tvShow = new TVShow(fearTheWalkingDead, QUALITE.HDTV.name(), LANGUE.FRENCH.name());
			tvShow.addTvShowSeason(new TVShowSeason("3", ZT_LINK + "/25619-fear-the-walking-dead-saison-3-hdtv-french.html"));
			tvShowList.add(tvShow);

			tvShow = new TVShow(fearTheWalkingDead, QUALITE.HD720P.name(), LANGUE.VOSTFR.name());
			tvShow.addTvShowSeason(
					new TVShowSeason("3", ZT_LINK + "/25431-fear-the-walking-dead-saison-3-hd-720p-vostfr.html"));
			tvShowList.add(tvShow);

			tvShow = new TVShow(fearTheWalkingDead, QUALITE.HD720P.name(), LANGUE.FRENCH.name());
			tvShow.addTvShowSeason(
					new TVShowSeason("3", ZT_LINK + "/25615-fear-the-walking-dead-saison-3-hd-720p-french.html"));
			tvShowList.add(tvShow);

			log.debug(tvShowList.toString());

			//Encode
			XMLTools.encodeToFile(tvShowList, tvShowListFile);

			log.info("Fichier des series créé sans erreur : {}", tvShowListFile);

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Erreur lors de l'ajout d'une série TV dans le fichier : " + tvShowListFile, e);
		}

		return tvShowList;
	}

	/**
	 * Permet d'ajouter une serie avec son lien dans le fichier
	 *
	 * @param tvShowName
	 * @param qualite
	 * @param langue
	 * @param seasonNumber
	 * @param seasonLink
	 * @return
	 */
	@Override
	public List<TVShow> ajouterSerieFichier(String tvShowName, QUALITE qualite, LANGUE langue, String seasonNumber, String seasonLink) {

		// MAJ de this.tvShowList
		recupererTvShowList();

		try {
			boolean foundInList = false;
			TVShow newTvShow = new TVShow(tvShowName, qualite.name(), langue.name());

			// Est-ce que la série existe déja dans la liste des séries ?
			for (TVShow tvShow : tvShowList) {
				// Si on trouve la serie (par rapport a son nom, sa qualite et sa langue)
				// On quitte la boucle
				if (tvShowName.replaceAll("\\s", "").equals(tvShow.getName().toUpperCase().replaceAll("\\s", ""))
						&& qualite.equals(QUALITE.valueOf(tvShow.getQualite().toUpperCase()))
						&& langue.equals(LANGUE.valueOf(tvShow.getLangue().toUpperCase()))) {
					newTvShow = tvShow;
					foundInList = true;
					log.debug("Serie {} déjà présente dans la liste", tvShowName);
					break;
				}
			}

			// Si c'est déja dans la liste, on vérifie que la saison n'est pas déja présente, si oui, on la supprime (= MAJ au final)
			if (foundInList) {
				// Si la saison est déja présente
				if (newTvShow.getTvShowSeason().stream().anyMatch(ls -> ls.getSaison().equals(seasonNumber))) {
					for (TVShowSeason tvShowSeason : newTvShow.getTvShowSeason()) {
						if (tvShowSeason.getSaison().equals(seasonNumber)) {
							// On supprime la saison si déja existante
							log.debug("Serie {} : suppression de la saison {}", tvShowName, seasonNumber);
							newTvShow.getTvShowSeason().remove(tvShowSeason);
							break;
						}
					}
				}
			}

			// Ajout de la nouvelle saison
			log.debug("Serie {} : ajout de la saison {}", tvShowName, seasonNumber);
			newTvShow.addTvShowSeason(new TVShowSeason(seasonNumber, seasonLink));

			// Si on ne vient pas de la liste, on l'ajoute
			if (!foundInList) {
				tvShowList.add(newTvShow);
			}

			log.debug(tvShowList.toString());

			//Encode
			XMLTools.encodeToFile(tvShowList, tvShowListFile);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Erreur lors de l'ajout d'une série TV dans le fichier : " + tvShowListFile, e);
		}

		return tvShowList;
	}

	/**
	 * Permet de mettre à jour la liste des séries TV par rapport au fichier
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PostConstruct
	@Override
	public List<TVShow> recupererTvShowList() {
		try {
			// Decode
			tvShowList = (ArrayList<TVShow>) XMLTools.decodeFromFile(tvShowListFile);
			log.debug(tvShowList.toString());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Erreur lors de la récuperation des séries TV via le fichier : " + tvShowListFile, e);
		}

		return tvShowList;
	}

	/**
	 * Permet de modifier le fichier de destination (lecture/ecriture) de la serdes
	 *
	 * @return
	 */
	@Override
	public String modifierFichierDestination(String fileName) {
		ApplicationHome home = new ApplicationHome(this.getClass());
		File jarDir = home.getDir();
		File file = new File(jarDir, fileName);
		tvShowListFile = file.toString();

		return tvShowListFile;
	}

	public void addTvShowList(TVShow tvShow) {
		if (tvShowList == null) {
			tvShowList = new ArrayList<>();
		}
		tvShowList.add(tvShow);
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("TVShowList :").append("\n");
		str.append("\t").append("TVShow = " + tvShowList);
		return str.toString();
	}

}
