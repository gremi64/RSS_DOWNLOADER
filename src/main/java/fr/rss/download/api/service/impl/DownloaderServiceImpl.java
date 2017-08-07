package fr.rss.download.api.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import fr.rss.download.api.constantes.Const;
import fr.rss.download.api.exceptions.ApiException;
import fr.rss.download.api.service.IDownloaderService;
import fr.rss.download.model.AlldebridRemoteFile;
import fr.rss.download.model.RemoteFile;

@Service
public class DownloaderServiceImpl implements IDownloaderService {

	private static final String WINDOWS_SEPARATOR = "\\";

	private static final Logger log = LoggerFactory.getLogger(DownloaderServiceImpl.class);

	private String downloadLocation;

	public DownloaderServiceImpl(@Value("${alldebrid.download.location}") String downloadLocation) {
		if (!downloadLocation.endsWith(WINDOWS_SEPARATOR)) {
			downloadLocation = downloadLocation + WINDOWS_SEPARATOR;
		}
		this.downloadLocation = downloadLocation;
	}

	@Override
	public String getDownloadLocation() {
		return downloadLocation;
	}

	@Override
	public void download(AlldebridRemoteFile alldebridRemoteFile) throws ApiException {
		download(alldebridRemoteFile.getUnrestrainedLink(), alldebridRemoteFile.getFileName());
	}

	@Override
	public void download(RemoteFile remoteFile) throws ApiException {
		download(remoteFile.getLink(), remoteFile.getFileName(), downloadLocation);
	}

	@Override
	public void download(RemoteFile remoteFile, String location) throws ApiException {
		if (Const.DEFAULT_VALUE.equals(location)) {
			download(remoteFile);
		} else {
			download(remoteFile.getLink(), remoteFile.getFileName(), location);
		}
	}

	/**
	 * Téléchargement de n'importe quel lien à l'emplacement par défaut
	 *
	 * @param link
	 * @param fileName
	 * @throws ApiException
	 */
	@Override
	public void download(String link, String fileName) throws ApiException {
		download(link, fileName, downloadLocation);
	}

	/**
	 * Methode principale de téléchargement de n'importe quel lien
	 *
	 * @param link
	 * @param fileName
	 * @param location
	 * @throws ApiException
	 */
	@Override
	public void download(String link, String fileName, String location) throws ApiException {
		if (link == null) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Lien null, impossible de lancer le téléchargement");
		} else if (fileName == null) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Nom du fichier à télécharger null, impossible de lancer le téléchargement");
		} else if (location == null) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Futur emplacement du fichier à télécharger null, impossible de lancer le téléchargement");
		}

		if (!location.endsWith(WINDOWS_SEPARATOR)) {
			location = location + WINDOWS_SEPARATOR;
		}

		try {
			log.debug("Debut du download : " + location + fileName + " / link=" + link);
			FileUtils.copyURLToFile(new URL(link), new File(location + fileName));
			log.debug("Fin du download : " + location + fileName);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors du téléchargement : UnknownHostException : " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors du téléchargement : " + e.getMessage());
		}
	}

}
