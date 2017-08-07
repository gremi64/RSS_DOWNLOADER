package fr.rss.download.api.controller.impl;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import fr.rss.download.api.controller.IAllDebridApiController;
import fr.rss.download.api.exceptions.ApiException;
import fr.rss.download.api.service.IAlldebrid;
import fr.rss.download.model.AlldebridLink;

@Controller
public class AllDebridApiControllerImpl implements IAllDebridApiController {

	private static final Logger log = LoggerFactory.getLogger(AllDebridApiControllerImpl.class);

	@Autowired
	private IAlldebrid allDebrid;

	@Override
	public ResponseEntity<?> debridLink(String link) throws ApiException {
		AlldebridLink alldebridLink = new AlldebridLink();
		alldebridLink.setLink(link);

		// get the property value and print it out
		log.debug("Lien : " + link);

		log.debug(allDebrid.toString());

		alldebridLink = allDebrid.unrestrainLink(alldebridLink);

		try {
			log.debug("Download en cours " + alldebridLink.getFileName() + " vers " + allDebrid.getDownloadLocation());
			FileUtils.copyURLToFile(new URL(alldebridLink.getUnrestrainedLink()),
					new File(allDebrid.getDownloadLocation() + "\\" + alldebridLink.getFileName()));
			log.debug("Fin du téléchargement");
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Erreur lors du téléchargement du lien : " + alldebridLink);
		}

		return new ResponseEntity<AlldebridLink>(alldebridLink, HttpStatus.OK);
	}
}
