package fr.rss.download.api.utils;

import java.util.ArrayList;

public class AllDebridUtils {

	public static boolean isFolder(String link) {
		link = link.replaceAll("(\\r|\\n)", "").replaceAll("^https?://(www\\.)?", "");

		ArrayList<String> redirectorsRegexp = new ArrayList<>();
		redirectorsRegexp.add("^(zytpirwai.net/[0-9a-zA-Z.-]+)$");
		redirectorsRegexp.add("^(linkbucks.com/([0-9a-zA-Z.-]+))$");
		redirectorsRegexp.add("^(adf.ly/[0-9]+/(.+))$");
		redirectorsRegexp.add("^(adf.ly/[0-9a-zA-Z.-]+)$");
		redirectorsRegexp.add("^(bit.ly/[0-9a-zA-Z.-]+)$");
		redirectorsRegexp.add("^(tinyurl.com/[0-9a-zA-Z.-]+)$");
		redirectorsRegexp.add("^(safelinking.net/[0-9a-zA-Z.-]+)$");
		redirectorsRegexp.add("^(extreme-protect.net/[0-9a-zA-Z.-]+)$");
		redirectorsRegexp.add("^(ed-protect.org/[0-9a-zA-Z.-]+)$");
		redirectorsRegexp.add("^(dl-protecte.com/[0-9a-zA-Z.-]+)");
		redirectorsRegexp.add("^(liencaptcha.com/lien.php?idupload=[0-9]+&ad=[0-9]+&pos=[0-9]+&link=[0-9]+)");
		redirectorsRegexp.add("^(protect.ddl-island.su/[0-9a-zA-Z.-]+)$");

		for (String regexp : redirectorsRegexp) {
			if (link.matches(regexp)) {
				return true;
			}
		}
		return false;
	}
}
