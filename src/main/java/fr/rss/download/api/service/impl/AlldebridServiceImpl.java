package fr.rss.download.api.service.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import fr.rss.download.api.exceptions.ApiException;
import fr.rss.download.api.exceptions.DebridErrorException;
import fr.rss.download.api.exceptions.FinalLinkNotFoundException;
import fr.rss.download.api.model.AlldebridRemoteFile;
import fr.rss.download.api.service.IAlldebridService;
import fr.rss.download.api.utils.AllDebridUtils;

@Service
public class AlldebridServiceImpl implements IAlldebridService {

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML,like Gecko) Chrome/59.0.3071.115 Safari/537.36";

	private static final Logger log = LoggerFactory.getLogger(AlldebridServiceImpl.class);

	static final String URL_CONNEXION = "https://alldebrid.com/api.php";
	static final String URL_UNRESTRAIN = "https://alldebrid.com/service.php";
	static final String URL_THE_GREAT_EXTRACTOR = "https://alldebrid.fr/api/theGreatExtractor.php";

	String login;
	String mdp;
	String cookie;

	public AlldebridServiceImpl(@Value("${alldebrid.login}") String login, @Value("${alldebrid.pwd}") String mdp,
			@Value("${alldebrid.download.location}") String downloadLocation) {
		this.login = login;
		this.mdp = mdp;
		log.debug("AllDebrid() : login=" + login + " / pwd=" + mdp);
	}

	@Override
	public AlldebridRemoteFile unrestrainLink(AlldebridRemoteFile alldebridRemoteFile) throws ApiException {
		// Si il ne s'agit pas d'un lien "classique" d'un hebergeur de lien
		// Il faut utiliser le "greatExtractor" pour obtenir un lien "classique"
		if (AllDebridUtils.isFolder(alldebridRemoteFile.getLink())) {
			alldebridRemoteFile.setLink(greatExtractThisUrl(alldebridRemoteFile.getLink()));
		}

		// Dans tous les cas... on souhaite obtenir un lien "allDebrid"
		alldebridRemoteFile = debridLink(alldebridRemoteFile);

		log.debug(alldebridRemoteFile.toString());
		return alldebridRemoteFile;
	}

	/**
	 * Permet d'obtenir un lien "classique"
	 *
	 * @param link
	 * @return
	 * @throws ApiException
	 */
	private String greatExtractThisUrl(String link) throws ApiException {
		String debridLink = null;

		URIBuilder ub;
		try {
			ub = new URIBuilder(URL_THE_GREAT_EXTRACTOR);
		} catch (URISyntaxException e) {
			log.error("Url du greatExtractor invalide", e);
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Url du greatExtractor invalide : " + e.getMessage());
		}
		ub.addParameter("link", link.replaceAll("(\\r|\\n)", ""));
		ub.addParameter("json", "true");
		ub.addParameter("jd", "true");
		String urlDebridage = ub.toString();

		URL url;
		try {
			url = new URL(urlDebridage);
		} catch (MalformedURLException e) {
			log.error("Url du greatExtractor avec ses paramètres invalide", e);
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Url du greatExtractor avec ses paramètres invalide : " + e.getMessage());
		}
		URLConnection connection;
		try {
			connection = url.openConnection();
		} catch (IOException e) {
			log.error("Echec de l'uverture Url du greatExtractor avec ses paramètres", e);
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Echec de l'uverture Url du greatExtractor avec ses paramètres : " + e.getMessage());
		}

		if (cookie == null) {
			login();
		}

		connection.setRequestProperty("User-Agent", USER_AGENT);
		connection.setRequestProperty("Cookie", "uid=" + cookie);

		log.debug("Cookie : " + connection.getRequestProperty("Cookie"));

		String response;
		try {
			response = IOUtils.toString(connection.getInputStream(), "UTF-8");
		} catch (IOException e) {
			log.error("Echec de la lecture de la réponse de l'url du greatExtractor", e);
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Echec de la lecture de la réponse de l'url du greatExtractor : " + e.getMessage());
		}

		log.debug("URL_THE_GREAT_EXTRACTOR response : {}", response);
		JSONObject obj = new JSONObject(response);
		if (!StringUtils.isEmpty(obj.getString("error"))) {
			log.debug("Erreur retournée par AllDebrid : {}", obj.getString("error"));
			throw new DebridErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur retournée par AllDebrid : " + "\n" + "response : " + response);
		}

		if (obj.getJSONArray("finalLinks") == null || obj.getJSONArray("finalLinks").length() < 1) {
			log.debug("Pas de 'FinalLinks' ... ");
			throw new FinalLinkNotFoundException(HttpStatus.INTERNAL_SERVER_ERROR, "Pas de 'FinalLinks' ... " + "\n" + "response : " + response);
		}
		debridLink = (String) obj.getJSONArray("finalLinks").get(0);

		return debridLink;
	}

	/**
	 * Permet de récupérer un lien "allDébridé"
	 *
	 * @param alldebridRemoteFile
	 * @return
	 * @throws ApiException
	 */
	private AlldebridRemoteFile debridLink(AlldebridRemoteFile alldebridRemoteFile) throws ApiException {
		URIBuilder ub;
		try {
			ub = new URIBuilder(URL_UNRESTRAIN);
		} catch (URISyntaxException e) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Url de debridage invalide : " + e.getMessage());
		}
		ub.addParameter("link", alldebridRemoteFile.getLink());
		ub.addParameter("json", "true");
		ub.addParameter("jd", "true");
		String urlDebridage = ub.toString();

		URL url;
		try {
			url = new URL(urlDebridage);
		} catch (MalformedURLException e) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Url de debridage avec ses paramètres invalide : " + e.getMessage());
		}
		URLConnection connection;
		try {
			connection = url.openConnection();
		} catch (IOException e) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Echec de l'uverture Url de debridage avec ses paramètres : " + e.getMessage());
		}

		if (cookie == null) {
			login();
		}

		connection.setRequestProperty("User-Agent", USER_AGENT);
		connection.setRequestProperty("Cookie", "uid=" + cookie);

		log.debug("Cookie : " + connection.getRequestProperty("Cookie"));

		String response;
		try {
			response = IOUtils.toString(connection.getInputStream(), "UTF-8");
		} catch (IOException e) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Echec de la lecture de la réponse de l'url de debridage : " + e.getMessage());
		}

		log.debug("URL_UNRESTRAIN response : {}", response);

		JSONObject obj = new JSONObject(response);
		if (!StringUtils.isEmpty(obj.getString("error"))) {
			log.debug("Erreur retournée par AllDebrid : {}", obj.getString("error"));
			throw new DebridErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur retournée par AllDebrid : " + "\n" + "response : " + response);
		}

		alldebridRemoteFile.setFileSize(String.valueOf(obj.getInt("filesize")));
		alldebridRemoteFile.setFileName(obj.getString("filename"));
		alldebridRemoteFile.setUnrestrainedLink(obj.getString("link"));

		return alldebridRemoteFile;
	}

	@Override
	@PostConstruct
	public void login() throws ApiException {

		URIBuilder ub;
		try {
			ub = new URIBuilder(URL_CONNEXION);
		} catch (URISyntaxException e) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Url de connexion invalide : " + e.getMessage());
		}
		ub.addParameter("action", "info_user");
		ub.addParameter("login", login);
		ub.addParameter("pw", mdp);
		ub.addParameter("format", "json");
		String urlConnexion = ub.toString();

		URL url;
		try {
			url = new URL(urlConnexion);
		} catch (MalformedURLException e) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Url de connexion avec ses paramètres invalide : " + e.getMessage());
		}
		URLConnection connection;
		try {
			connection = url.openConnection();
		} catch (IOException e) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Echec de l'uverture Url de connexion avec ses paramètres : " + e.getMessage());
		}

		connection.setRequestProperty("User-Agent", USER_AGENT);

		String response;
		try {
			response = IOUtils.toString(connection.getInputStream(), "UTF-8");
		} catch (IOException e) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Echec de la lecture de la réponse de l'url de connexion : " + e.getMessage());
		}

		log.debug("Connexion response : " + response);

		if (response == null || !response.startsWith("{")) {
			// Erreur
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors du login : " + response);
		} else {
			// Login : OK
			JSONObject obj = new JSONObject(response);
			cookie = obj.getString("cookie");

			log.info("Login OK");
		}
	}

	@Override
	public String toString() {
		return "Alldebrid [login=" + login + ", mdp=" + mdp + ", cookie=" + cookie + "]";
	}

}
