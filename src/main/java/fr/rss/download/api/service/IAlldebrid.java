package fr.rss.download.api.service;

import fr.rss.download.api.exceptions.ApiException;
import fr.rss.download.model.AlldebridLink;

public interface IAlldebrid {

	AlldebridLink unrestrainLink(AlldebridLink alldebridLink) throws ApiException;

	/**
	 * Permet d'obtenir un lien "classique"
	 *
	 * @param link
	 * @return
	 * @throws ApiException
	 */
	String greatExtractThisUrl(String link) throws ApiException;

	/**
	 * Permet de récupérer un lien "allDébridé"
	 *
	 * @param debridUrl
	 * @param alldebridLink.g
	 * @return
	 * @throws ApiException
	 */
	AlldebridLink debridLink(AlldebridLink alldebridLink) throws ApiException;

	boolean isFolder(String link);

	void login() throws ApiException;

	String getDownloadLocation();

}
