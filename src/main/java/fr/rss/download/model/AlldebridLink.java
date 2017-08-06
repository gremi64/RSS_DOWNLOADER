package fr.rss.download.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AlldebridLink {

	@JsonProperty("link")
	String link;

	@JsonProperty("unrestrained_link")
	String unrestrainedLink;

	@JsonProperty("file_name")
	String fileName;

	@JsonProperty("file_size")
	String fileSize;

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getUnrestrainedLink() {
		return unrestrainedLink;
	}

	public void setUnrestrainedLink(String unrestrainedLink) {
		this.unrestrainedLink = unrestrainedLink;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	@Override
	public String toString() {
		return "AlldebridLink [link=" + link + ", unrestrainedLink=" + unrestrainedLink + ", fileName=" + fileName + ", fileSize=" + fileSize + "]";
	}

}
