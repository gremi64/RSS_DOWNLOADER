package fr.rss.download.api.controller.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import fr.rss.download.api.constantes.Const;
import fr.rss.download.api.controller.IDownloadApiController;
import fr.rss.download.api.exceptions.ApiException;
import fr.rss.download.api.model.RemoteFile;
import fr.rss.download.api.service.IDownloaderService;
import io.swagger.annotations.ApiParam;

@Controller
public class DownloadApiControllerImpl implements IDownloadApiController {

	private static final Logger log = LoggerFactory.getLogger(DownloadApiControllerImpl.class);

	@Autowired
	private IDownloaderService downloaderService;

	@Override
	public ResponseEntity<?> downloadLink(@ApiParam(value = "Lien à débrider", required = true) @RequestParam("link") String link,
			@ApiParam(value = "Nom du fichier à télécharger", required = true) @RequestParam("filename") String filename,
			@ApiParam(value = "Destination du fichier à télécharger", required = false, defaultValue = Const.DEFAULT_VALUE) @RequestParam("destination") String destination) throws ApiException {
		RemoteFile remoteFile = new RemoteFile();
		remoteFile.setLink(link);
		remoteFile.setFileName(filename);

		log.debug(remoteFile.toString());

		downloaderService.download(remoteFile, destination);

		return new ResponseEntity<RemoteFile>(remoteFile, HttpStatus.OK);
	}
}
