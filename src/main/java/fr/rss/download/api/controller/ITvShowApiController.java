package fr.rss.download.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import fr.rss.download.api.exceptions.ApiException;
import fr.rss.download.api.model.zt.tvshow.TVShow;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "tvShow")
public interface ITvShowApiController {

	@ApiOperation(value = "Récupère la liste des séries présentes dans le fichier xml", response = TVShow.class, responseContainer = "List", tags = {
			"tvShow" })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Ajout ok", response = TVShow.class),
			@ApiResponse(code = 400, message = "Erreur dans la requete", response = ApiException.class),
			@ApiResponse(code = 500, message = "Erreur lors du traitement", response = ApiException.class) })

	@RequestMapping(value = "/getTvShow", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<?> getTvShow() throws ApiException;

	@ApiOperation(value = "Ajoute une serie dans le fichier xml", response = TVShow.class, responseContainer = "List", tags = { "tvShow" })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Ajout ok", response = TVShow.class),
			@ApiResponse(code = 400, message = "Erreur dans la requete", response = ApiException.class),
			@ApiResponse(code = 500, message = "Erreur lors du traitement", response = ApiException.class) })

	@RequestMapping(value = "/addTvShow", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<?> addTvShow(@ApiParam(value = "Nom de la serie TV", required = true) @RequestParam("tvShowName") String tvShowName,
			@ApiParam(value = "Qualité (HDTV, HD720P, HD1080P)", required = true) @RequestParam("qualite") String qualite,
			@ApiParam(value = "Langue (VOSTFR, FRENCH, MULTI, ...)", required = true) @RequestParam("langue") String langue,
			@ApiParam(value = "Numero de la saison (1, 2, 3, ...)", required = true) @RequestParam("saison") String saison,
			@ApiParam(value = "Lien de la page ZT de la saison", required = true) @RequestParam("link") String link) throws ApiException;

}
