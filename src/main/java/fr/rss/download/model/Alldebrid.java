package fr.rss.download.model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import fr.rss.download.api.exceptions.ApiException;

public class Alldebrid {

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML,like Gecko) Chrome/59.0.3071.115 Safari/537.36";

	private static final Logger log = LoggerFactory.getLogger(Alldebrid.class);

	static final String URL_CONNEXION = "https://alldebrid.com/api.php";
	static final String URL_UNRESTRAIN = "https://alldebrid.com/service.php";
	static final String URL_THE_GREAT_EXTRACTOR = "https://alldebrid.fr/api/theGreatExtractor.php";

	String login;
	String mdp;
	String cookie;

	public Alldebrid(String login, String mdp) throws IOException, URISyntaxException {
		this.login = login;
		this.mdp = mdp;
	}

	public AlldebridLink unrestrainLink(AlldebridLink alldebridLink) throws ApiException {
		// Si il ne s'agit pas d'un lien "classique" d'un hebergeur de lien
		// Il faut utiliser le "greatExtractor" pour obtenir un lien "classique"
		if (isFolder(alldebridLink.getLink())) {
			alldebridLink.setLink(greatExtractThisUrl(alldebridLink.getLink()));
		}

		// Dans tous les cas... on souhaite obtenir un lien "allDebrid"
		alldebridLink = debridLink(alldebridLink);

		log.debug(alldebridLink.toString());
		return alldebridLink;
	}

	/**
	 * Permet d'obtenir un lien "classique"
	 *
	 * @param link
	 * @return
	 * @throws ApiException
	 */
	public String greatExtractThisUrl(String link) throws ApiException {
		String debridLink = null;

		URIBuilder ub;
		try {
			ub = new URIBuilder(URL_THE_GREAT_EXTRACTOR);
		} catch (URISyntaxException e) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Url du greatExtractor invalide : " + e.getMessage());
		}
		ub.addParameter("link", link);
		ub.addParameter("json", "true");
		ub.addParameter("jd", "true");
		String urlDebridage = ub.toString();

		URL url;
		try {
			url = new URL(urlDebridage);
		} catch (MalformedURLException e) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Url du greatExtractor avec ses paramètres invalide : " + e.getMessage());
		}
		URLConnection connection;
		try {
			connection = url.openConnection();
		} catch (IOException e) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Echec de l'uverture Url du greatExtractor avec ses paramètres : " + e.getMessage());
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
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Echec de la lecture de la réponse de l'url du greatExtractor : " + e.getMessage());
		}

		log.error("URL_THE_GREAT_EXTRACTOR response : " + response);
		JSONObject obj = new JSONObject(response);
		if (obj.getJSONArray("finalLinks") == null || obj.getJSONArray("finalLinks").length() < 1) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Pas de 'FinalLinks' ... " + "\n" + "response : " + response);
		}
		debridLink = (String) obj.getJSONArray("finalLinks").get(0);

		return debridLink;
	}

	/**
	 * Permet de récupérer un lien "allDébridé"
	 *
	 * @param debridUrl
	 * @param alldebridLink.g
	 * @return
	 * @throws ApiException
	 */
	public AlldebridLink debridLink(AlldebridLink alldebridLink) throws ApiException {
		URIBuilder ub;
		try {
			ub = new URIBuilder(URL_UNRESTRAIN);
		} catch (URISyntaxException e) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Url de debridage invalide : " + e.getMessage());
		}
		ub.addParameter("link", alldebridLink.getLink());
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

		log.error("URL_UNRESTRAIN response : " + response);
		JSONObject obj = new JSONObject(response);
		alldebridLink.setFileSize(String.valueOf(obj.getInt("filesize")));
		alldebridLink.setFileName(obj.getString("filename"));
		alldebridLink.setUnrestrainedLink(obj.getString("link"));

		return alldebridLink;
	}

	public static boolean isFolder(String link) {
		link = link.replaceAll("^https?://(www\\.)?", "");

		ArrayList<String> redirectorsRegexp = new ArrayList<>();
		redirectorsRegexp.add("^(zytpirwai.net/[a-zA-Z0-9]+)$");
		redirectorsRegexp.add("^(linkbucks.com/([a-zA-Z0-9]+))$");
		redirectorsRegexp.add("^(adf.ly/[0-9]+/(.+))$");
		redirectorsRegexp.add("^(adf.ly/[0-9a-zA-Z]+)$");
		redirectorsRegexp.add("^(bit.ly/[0-9a-zA-Z]+)$");
		redirectorsRegexp.add("^(tinyurl.com/[0-9a-zA-Z-]+)$");
		redirectorsRegexp.add("^(safelinking.net/[0-9a-zA-Z]+)$");
		redirectorsRegexp.add("^(extreme-protect.net/[0-9A-Za-z]+)$");
		redirectorsRegexp.add("^(ed-protect.org/[0-9A-Za-z]+)$");
		redirectorsRegexp.add("^(dl-protecte.com/[0-9a-zA-Z]+)");
		redirectorsRegexp.add("^(liencaptcha.com/lien.php?idupload=[0-9]+&ad=[0-9]+&pos=[0-9]+&link=[0-9]+)");
		redirectorsRegexp.add("^(protect.ddl-island.su/[0-9a-zA-Z]+)$");

		for (String regexp : redirectorsRegexp) {
			if (link.matches(regexp)) {
				return true;
			}
		}
		return false;
	}

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
