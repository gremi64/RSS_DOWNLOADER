package fr.rss.download.api.controller.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

import fr.rss.download.api.controller.IAllDebridApiController;
import fr.rss.download.api.exceptions.ApiException;
import fr.rss.download.api.model.AlldebridRemoteFile;
import fr.rss.download.api.service.IAlldebridService;
import fr.rss.download.api.service.IDownloaderService;
import io.swagger.annotations.ApiParam;

@CrossOrigin(origins = { "http://localhost:4200", "http://192.168.1.2:4200" })
@Controller
public class AllDebridApiControllerImpl implements IAllDebridApiController {

	private static final Logger log = LoggerFactory.getLogger(AllDebridApiControllerImpl.class);

	@Autowired
	private IAlldebridService alldebridService;

	@Autowired
	private IDownloaderService downloaderService;

	@Override
	public ResponseEntity<?> allDebridDownload(@ApiParam(value = "Lien à débrider", required = true) @RequestParam("link") String link)
			throws ApiException {
		AlldebridRemoteFile alldebridRemoteFile = new AlldebridRemoteFile();
		alldebridRemoteFile.setLink(link.replaceAll("(\\r|\\n)", ""));

		log.debug(alldebridService.toString());

		alldebridRemoteFile = alldebridService.unrestrainLink(alldebridRemoteFile);

		alldebridRemoteFile.setFileLocation(downloaderService.download(alldebridRemoteFile));

		return new ResponseEntity<AlldebridRemoteFile>(alldebridRemoteFile, HttpStatus.OK);
	}
}
