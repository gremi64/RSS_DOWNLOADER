package fr.rss.download.alldebrid;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;

public class AlldebridApi {

	static final String URL_CONNEXION = "https://alldebrid.com/api.php";
	static final String URL_UNRESTRAIN = "https://alldebrid.com/service.php?link=thedownloadlink&json=true&jd=true";

	String login;
	String mdp;
	String cookie;
	String urlConnexion;

	public static void main(String[] args) throws Exception {

		Properties prop = new Properties();
		InputStream input = null;

		try {

			String filename = "config.properties";
			input = AlldebridApi.class.getClassLoader().getResourceAsStream(filename);
			if (input == null) {
				System.out.println("Sorry, unable to find " + filename);
				return;
			}

			// load a properties file from class path, inside static method
			prop.load(input);

			String myLogin = prop.getProperty("login");
			String myPwd = prop.getProperty("pwd");

			// get the property value and print it out
			System.out.println("Login : " + myLogin);
			System.out.println("Password : " + myPwd);

			if (myLogin == null || myPwd == null) {
				throw new Exception("Login or password can't be found in " + filename);
			}

			AlldebridApi allDebrid = new AlldebridApi(myLogin, myPwd);
			allDebrid.login();

			System.out.println(allDebrid);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public AlldebridApi(String login, String mdp) throws IOException, URISyntaxException {
		this.login = login;
		this.mdp = mdp;

	}

	public String unrestrainLink(String url) throws URISyntaxException {
		String unrestrainLink = null;

		URIBuilder ub = new URIBuilder(URL_UNRESTRAIN);
		ub.addParameter("link", url);
		ub.addParameter("json", "true");
		ub.addParameter("jd", "true");
		urlConnexion = ub.toString();

		return unrestrainLink;
	}

	public void login() throws Exception {

		URIBuilder ub = new URIBuilder(URL_CONNEXION);
		ub.addParameter("action", "info_user");
		ub.addParameter("login", login);
		ub.addParameter("pw", mdp);
		ub.addParameter("format", "json");
		urlConnexion = ub.toString();

		URL url = new URL(urlConnexion);
		URLConnection connection = url.openConnection();

		connection.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML,like Gecko) Chrome/59.0.3071.115 Safari/537.36");

		String response = IOUtils.toString(connection.getInputStream(), "UTF-8");
		System.out.println(response);

		if (response == null || !response.startsWith("{")) {
			// Erreur
			throw new Exception("Erreur lors du login : " + response);
		} else {
			// Login : OK
			JSONObject obj = new JSONObject(response);
			cookie = obj.getString("cookie");

			System.out.println("Login OK");
		}
	}

	@Override
	public String toString() {
		return "AlldebridApi [login=" + login + ", mdp=" + mdp + ", cookie=" + cookie + ", urlConnexion=" + urlConnexion
				+ "]";
	}

}
