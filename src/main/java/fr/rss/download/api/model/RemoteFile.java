package fr.rss.download.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RemoteFile {

	@JsonProperty("link")
	String link;

	@JsonProperty("file_name")
	String fileName;

	@JsonProperty("file_size")
	long fileSize;

	@JsonProperty("file_location")
	String fileLocation;

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	@Override
	public String toString() {
		return "RemoteFile [link=" + link + ", fileName=" + fileName + ", fileSize=" + fileSize + ", fileLocation=" + fileLocation + "]";
	}

}
