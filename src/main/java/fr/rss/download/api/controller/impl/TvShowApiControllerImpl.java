package fr.rss.download.api.controller.impl;

import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

import fr.rss.download.api.constantes.LANGUE;
import fr.rss.download.api.constantes.QUALITE;
import fr.rss.download.api.controller.ITvShowApiController;
import fr.rss.download.api.exceptions.ApiException;
import fr.rss.download.api.model.zt.tvshow.TVShow;
import fr.rss.download.api.service.ITvShowService;
import io.swagger.annotations.ApiParam;

@CrossOrigin(origins = "http://localhost:4200")
@Controller
public class TvShowApiControllerImpl implements ITvShowApiController {

	private static final Logger log = LoggerFactory.getLogger(TvShowApiControllerImpl.class);

	@Autowired
	private ITvShowService tvShowService;

	@Override
	public ResponseEntity<List<TVShow>> addTvShow(
			@ApiParam(value = "Nom de la serie TV", required = true) @RequestParam("tvShowName") String tvShowName,
			@ApiParam(value = "Qualité (HDTV, HD720P, HD1080P)", required = true, defaultValue = "HD720P") @RequestParam("qualite") String qualite,
			@ApiParam(value = "Langue (VOSTFR, FRENCH, MULTI, ...)", required = true, defaultValue = "VOSTFR") @RequestParam("langue") String langue,
			@ApiParam(value = "Numero de la saison (1, 2, 3, ...)", required = true) @RequestParam("saison") String saison,
			@ApiParam(value = "Lien de la page ZT de la saison", required = true) @RequestParam("link") String link) throws ApiException {

		if (StringUtils.isEmpty(tvShowName)
				|| StringUtils.isEmpty(qualite)
				|| StringUtils.isEmpty(langue)
				|| StringUtils.isEmpty(saison)
				|| StringUtils.isEmpty(link)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Tous les paramètres doivent être renseignés");
		}

		tvShowName = tvShowName.replace("\"", "");
		qualite = qualite.replace("\"", "");
		langue = langue.replace("\"", "");
		saison = saison.replace("\"", "");
		link = link.replace("\"", "");

		if (!NumberUtils.isCreatable(saison)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "La saison doit être un nombre");
		}

		QUALITE qual = QUALITE.valueOf(qualite.toUpperCase());
		LANGUE lang = LANGUE.valueOf(langue.toUpperCase());
		tvShowName = tvShowName.toUpperCase();

		log.debug("Paramètres : Name = {} - Qualite = {} - Langue = {} - Saison = {} - Lien = {}", tvShowName, qual, lang, saison, link);

		List<TVShow> res = tvShowService.ajouterSerieFichier(tvShowName, qual, lang, saison, link);

		return new ResponseEntity<List<TVShow>>(res, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<TVShow>> getTvShow() throws ApiException {
		List<TVShow> res = tvShowService.recupererTvShowList();

		return new ResponseEntity<List<TVShow>>(res, HttpStatus.OK);
	}
}
