package fr.rss.download.api.service;

import java.util.List;

import fr.rss.download.api.constantes.LANGUE;
import fr.rss.download.api.constantes.QUALITE;
import fr.rss.download.api.model.Hebergeur;
import fr.rss.download.api.model.zt.tvshow.TVShow;

public interface ITvShowService {

	/**
	 * Initialise le fichier avec les séries contenues dans le code
	 *
	 * @return
	 */
	List<TVShow> creerSerieFichier();

	/**
	 * Ajoute une série dans le fichier
	 *
	 * @param tvShowName
	 * @param qualite
	 * @param langue
	 * @param seasonNumber
	 * @param seasonLink
	 * @return
	 */
	List<TVShow> ajouterSerieFichier(String tvShowName, QUALITE qualite, LANGUE langue, String seasonNumber, String seasonLink);

	/**
	 * Met à jour la liste par rapport au fichier
	 *
	 * @return
	 */
	List<TVShow> recupererTvShowList();

	/**
	 * Retourne le nouveau chemin du fichier
	 *
	 * @param fileName
	 * @return
	 */
	String modifierFichierDestination(String fileName);

	/**
	 * Renvoie la liste des hebergeurs et de leurs liens
	 * 
	 * @param link
	 * @return
	 */
	List<Hebergeur> parseZtTvShowLink(String link);

}
