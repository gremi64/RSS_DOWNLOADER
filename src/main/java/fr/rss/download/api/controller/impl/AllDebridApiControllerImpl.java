package fr.rss.download.api.controller.impl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import fr.rss.download.api.controller.IAllDebridApiController;
import fr.rss.download.api.exceptions.ApiException;
import fr.rss.download.model.Alldebrid;
import fr.rss.download.model.AlldebridLink;

@Controller
public class AllDebridApiControllerImpl implements IAllDebridApiController {

	@Value("${alldebrid.download.location}")
	private String DOWNLOAD_LOCATION;

	private static final Logger log = LoggerFactory.getLogger(AllDebridApiControllerImpl.class);

	@Value("${alldebrid.login}")
	private String ALLDEBRID_LOGIN;

	@Value("${alldebrid.pwd}")
	private String ALLDEBRID_PWD;

	@Override
	public ResponseEntity<?> debridLink(String link) throws ApiException {
		AlldebridLink alldebridLink = new AlldebridLink();
		alldebridLink.setLink(link);

		// get the property value and print it out
		log.debug("Lien : " + link);
		log.debug("Login : " + ALLDEBRID_LOGIN + "\n\t" + "Password : " + ALLDEBRID_PWD);

		if (ALLDEBRID_LOGIN == null || ALLDEBRID_PWD == null || ALLDEBRID_LOGIN.equals("") || ALLDEBRID_PWD.equals("")) {
			log.debug("Login ou Pwd non renseigné");
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Login ou Pwd non renseigné");
		}

		Alldebrid allDebrid;
		try {
			allDebrid = new Alldebrid(ALLDEBRID_LOGIN, ALLDEBRID_PWD);
		} catch (IOException | URISyntaxException e) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de l'initialisation de AllDebrid");
		}

		allDebrid.login();
		log.debug(allDebrid.toString());

		alldebridLink = allDebrid.unrestrainLink(alldebridLink);

		try {
			log.debug("Download en cours " + alldebridLink.getFileName() + " vers " + DOWNLOAD_LOCATION);
			FileUtils.copyURLToFile(new URL(alldebridLink.getUnrestrainedLink()), new File(DOWNLOAD_LOCATION + "\\" + alldebridLink.getFileName()));
			log.debug("Fin du téléchargement");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors du téléchargement du lien : " + alldebridLink);
		}

		return new ResponseEntity<AlldebridLink>(alldebridLink, HttpStatus.OK);
	}

	//	@Override
	//	public ResponseEntity<Void> getPetById(
	//			@ApiParam(value = "ID of pet to return", required = true) @PathVariable("petId") Long petId) {
	//		// do some magic!
	//		return new ResponseEntity<Void>(HttpStatus.OK);
	//	}
}
