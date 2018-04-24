package fr.rss.download.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AlldebridRemoteFile extends RemoteFile {

	@JsonProperty("unrestrained_link")
	String unrestrainedLink;

	public String getUnrestrainedLink() {
		return unrestrainedLink;
	}

	public void setUnrestrainedLink(String unrestrainedLink) {
		this.unrestrainedLink = unrestrainedLink;
	}

	@Override
	public String toString() {
		return "AlldebridRemoteFile [link=" + link + ", unrestrainedLink=" + unrestrainedLink + ", fileName=" + fileName + ", fileSize=" + fileSize
				+ "]";
	}

	public String tempsDl() {

		float fSko = fileSize / 1024f;
		float fSmo = fSko / 1024f;
		float fSgo = fSmo / 1024f;

		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("Taille totale :	" + "\n");
		sb.append("		" + fSmo + "mo" + "\n");
		sb.append("		" + fSgo + "go" + "\n");
		sb.append("Temps estimé :	" + "\n");
		float fibreMoS = 12f;
		float adslPlusMoS = 2f;
		sb.append("		Adsl  (2mo/s)  = " + fSmo / adslPlusMoS / 60 + "minutes" + "\n");
		sb.append("		Fibre (12mo/s) = " + fSmo / fibreMoS / 60 + "minutes" + "\n");

		return sb.toString();
	}

}
