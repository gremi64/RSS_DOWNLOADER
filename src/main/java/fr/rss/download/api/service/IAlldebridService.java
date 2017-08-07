package fr.rss.download.api.service;

import fr.rss.download.api.exceptions.ApiException;
import fr.rss.download.model.AlldebridRemoteFile;

public interface IAlldebridService {

	/**
	 * Permet de se logger auprès de allDebrid
	 *
	 * @throws ApiException
	 */
	void login() throws ApiException;

	/**
	 * Permet d'obtenir un lien "allDebrid" depuis n'importe quel lien "allDebridable"
	 *
	 * @param alldebridRemoteFile
	 * @return
	 * @throws ApiException
	 */
	AlldebridRemoteFile unrestrainLink(AlldebridRemoteFile alldebridRemoteFile) throws ApiException;

}
