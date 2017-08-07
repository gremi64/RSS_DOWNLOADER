package fr.rss.download.model;

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
		return "AlldebridRemoteFile [link=" + link + ", unrestrainedLink=" + unrestrainedLink + ", fileName=" + fileName + ", fileSize=" + fileSize + "]";
	}

}
