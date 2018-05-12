package fr.rss.download.api.controller.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

import fr.rss.download.api.constantes.Const;
import fr.rss.download.api.constantes.LANGUE;
import fr.rss.download.api.constantes.QUALITE;
import fr.rss.download.api.controller.IDownloadApiController;
import fr.rss.download.api.exceptions.ApiException;
import fr.rss.download.api.exceptions.DebridErrorException;
import fr.rss.download.api.exceptions.FinalLinkNotFoundException;
import fr.rss.download.api.model.AlldebridRemoteFile;
import fr.rss.download.api.model.EpisodeHebergeur;
import fr.rss.download.api.model.Hebergeur;
import fr.rss.download.api.model.RemoteFile;
import fr.rss.download.api.model.zt.tvshow.TVShow;
import fr.rss.download.api.model.zt.tvshow.TVShowSeason;
import fr.rss.download.api.service.IAlldebridService;
import fr.rss.download.api.service.IDownloaderService;
import fr.rss.download.api.service.ITvShowService;
import io.swagger.annotations.ApiParam;

@CrossOrigin(origins = { "http://localhost:4200", "http://192.168.1.2:4200" })
@Controller
public class DownloadApiControllerImpl implements IDownloadApiController {

	private static final Logger log = LoggerFactory.getLogger(DownloadApiControllerImpl.class);

	@Autowired
	private IDownloaderService downloaderService;

	@Autowired
	private ITvShowService tvShowService;

	@Autowired
	private IAlldebridService alldebridService;

	@Value("${zt.tvshow.download.location}")
	String tvShowDownloadLocation;

	@Override
	public ResponseEntity<?> downloadLink(@ApiParam(value = "Lien à débrider", required = true) @RequestParam("link") String link,
			@ApiParam(value = "Nom du fichier à télécharger", required = true) @RequestParam("filename") String filename,
			@ApiParam(value = "Destination du fichier à télécharger", required = false, defaultValue = Const.DEFAULT_VALUE) @RequestParam("destination") String destination)
					throws ApiException {
		RemoteFile remoteFile = new RemoteFile();
		remoteFile.setLink(link);
		remoteFile.setFileName(filename);

		log.debug(remoteFile.toString());

		remoteFile.setFileLocation(downloaderService.download(remoteFile, destination));

		return new ResponseEntity<RemoteFile>(remoteFile, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> downloadTvShow(@ApiParam(value = "Nom de la serie TV", required = true) @RequestParam("tvShowName") String tvShowName,
			@ApiParam(value = "Qualité (HDTV, HD720P, HD1080P)", required = true, defaultValue = "HD720P") @RequestParam("qualite") String qualite,
			@ApiParam(value = "Langue (VOSTFR, FRENCH, MULTI, ...)", required = true, defaultValue = "VOSTFR") @RequestParam("langue") String langue,
			@ApiParam(value = "Numero de la saison (1, 2, 3, ...)", required = true) @RequestParam("saison") String saison,
			@ApiParam(value = "Numero de l'épisode (1, 2, 3, ...)", required = true) @RequestParam("episode") String episode) throws ApiException {

		log.debug("Paramètres : tvShowName = {} - qualite = {} - langue = {} - saison = {} - episode = {}", tvShowName, qualite, langue, saison,
				episode);

		// Si un parametre est null : erreur
		if (StringUtils.isEmpty(tvShowName)
				|| StringUtils.isEmpty(qualite)
				|| StringUtils.isEmpty(langue)
				|| StringUtils.isEmpty(saison)
				|| StringUtils.isEmpty(episode)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Tous les paramètres doivent être renseignés");
		}

		tvShowName = tvShowName.replace("\"", "");
		qualite = qualite.replace("\"", "");
		langue = langue.replace("\"", "");
		saison = saison.replace("\"", "");
		episode = episode.replace("\"", "");

		saison = saison.replaceAll("[^\\d]", "");
		episode = episode.replaceAll("[^\\d]", "");

		// Si la saison ou l'episode n'est pas un nombre : erreur
		if (!NumberUtils.isCreatable(saison)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "La saison doit être un nombre");
		}

		if (!NumberUtils.isCreatable(episode)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "L'épisode doit être un nombre");
		}

		QUALITE qual = QUALITE.valueOf(qualite.toUpperCase());
		LANGUE lang = LANGUE.valueOf(langue.toUpperCase());
		tvShowName = tvShowName.toUpperCase().replaceAll("\\s", "");

		log.debug("Paramètres : Name = {} - Qualite = {} - Langue = {} - Saison = {} - Episode = {}", tvShowName, qual, lang, saison, episode);

		List<Hebergeur> listHebergeur = getListHebergeur(tvShowName, qual, lang, saison);

		// Liste des liens de chaque hebergeur pour l'episode demandé
		List<EpisodeHebergeur> listEpisodeHebergeur = new ArrayList<>();

		// On parcourt la liste des hébergeurs
		for (Hebergeur hebergeur : listHebergeur) {
			// Si la liste des episodes de l'hébergeur n'est pas nulle, on la parcourt
			if (hebergeur.getEpisodes() != null && !hebergeur.getEpisodes().isEmpty()) {
				for (EpisodeHebergeur episodeHebergeur : hebergeur.getEpisodes()) {
					// Si le numéro de l'épisode correspond à celui qu'on cherche, on l'ajoute à la liste
					if (episode.equals(episodeHebergeur.getName().replaceAll("[^\\d]", ""))) {
						listEpisodeHebergeur.add(episodeHebergeur);
					}
				}
			}
		}

		// Si la liste ne contient aucun lien, l'épisode n'est pas trouvé : erreur
		if (listEpisodeHebergeur.isEmpty()) {
			throw new ApiException(HttpStatus.NOT_FOUND, "L'épisode n'a pas été trouvé");
		}

		// On parcourt la liste des hebergeurs pour télécharger via allDebrid
		// Si un téléchargement échoue, on relance avec l'épisode suivant, sinon on sort
		AlldebridRemoteFile alldebridRemoteFile = null;

		for (EpisodeHebergeur episodeHebergeur : listEpisodeHebergeur) {
			try {
				alldebridRemoteFile = new AlldebridRemoteFile();
				alldebridRemoteFile.setLink(episodeHebergeur.getUrl());
				alldebridRemoteFile = alldebridService.unrestrainLink(alldebridRemoteFile);
				alldebridRemoteFile.setFileLocation(downloaderService.download(alldebridRemoteFile, tvShowDownloadLocation));
				break;
			} catch (DebridErrorException e) {
				log.debug("Erreur de débridage", e);
			} catch (FinalLinkNotFoundException e) {
				log.debug("Lien inutilisable", e);
			}
		}

		return new ResponseEntity<AlldebridRemoteFile>(alldebridRemoteFile, HttpStatus.OK);
	}

	/**
	 * Récupère la liste des hebergeurs et leurs liens
	 *
	 * @param tvShowName
	 * @param qual
	 * @param lang
	 * @param saison
	 * @return
	 * @throws ApiException
	 */
	private List<Hebergeur> getListHebergeur(String tvShowName, QUALITE qual, LANGUE lang, String saison) throws ApiException {
		// On parcours toutes les séries TV
		List<TVShow> tvShowList = tvShowService.recupererTvShowList();
		TVShow myTvShow = null;
		for (TVShow tvShow : tvShowList) {
			// Si on trouve la serie (par rapport a son nom, sa qualite et sa langue)
			// On quitte la boucle
			if (tvShowName.equals(tvShow.getName().toUpperCase().replaceAll("\\s", ""))
					&& qual.equals(QUALITE.valueOf(tvShow.getQualite().toUpperCase()))
					&& lang.equals(LANGUE.valueOf(tvShow.getLangue().toUpperCase()))) {
				myTvShow = tvShow;
				break;
			}
		}

		// Si on ne trouve pas la série ou qu'elle n'a pas de saison : erreur
		if (myTvShow == null || myTvShow.getTvShowSeason() == null || myTvShow.getTvShowSeason().isEmpty()) {
			throw new ApiException(HttpStatus.NOT_FOUND, "Série non trouvée");
		}

		// On parcours toutes les saisons de la série
		String lienPageZt = null;
		for (TVShowSeason tvShowSeason : myTvShow.getTvShowSeason()) {
			// Si on arrive sur la bonne saison, on tient notre lien de page !
			if (saison.equals(tvShowSeason.getSaison())) {
				lienPageZt = tvShowSeason.getLink();
				break;
			}
		}

		// Si le lien est null, soit on a pas trouvé la saison demandée, soit il n'y a pas de lien
		if (lienPageZt == null) {
			throw new ApiException(HttpStatus.NOT_FOUND, "Saison/Lien non trouvé(e)");
		}

		List<Hebergeur> res = tvShowService.parseZtTvShowLink(lienPageZt);
		return res;
	}
}
