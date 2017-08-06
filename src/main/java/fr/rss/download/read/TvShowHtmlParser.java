package fr.rss.download.read;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.rss.download.model.EpisodeFournisseur;
import fr.rss.download.model.Fournisseur;

public class TvShowHtmlParser {

	private static final Logger log = LoggerFactory.getLogger(TvShowHtmlParser.class);

	List<Fournisseur> listFournisseurs;

	public TvShowHtmlParser(String urlRss) {
		try {
			Document document = Jsoup.connect(urlRss).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36").get();

			Elements divClassOnPage = document.select("div.postinfo");
			Elements informations = divClassOnPage.select("b");

			listFournisseurs = new ArrayList<>();
			Fournisseur fournisseur = new Fournisseur();

			for (Element information : informations) {
				// Si on a une div dans le "b"
				// alors c'est le nom du fournisseur
				if (!information.select("div").isEmpty()) {
					fournisseur = new Fournisseur();
					listFournisseurs.add(fournisseur);
					fournisseur.setName(information.text());
				} else if (!information.select("a[href]").isEmpty()) {
					EpisodeFournisseur episode = new EpisodeFournisseur();
					episode.setName(information.text());
					episode.setUrl(information.select("a[href]").attr("href"));
					fournisseur.getEpisodes().add(episode);
				}

			}
			log.debug(listFournisseurs.toString());

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}