package fr.rss.download.api.service;

import fr.rss.download.api.exceptions.ApiException;
import fr.rss.download.api.model.AlldebridRemoteFile;
import fr.rss.download.api.model.RemoteFile;

public interface IDownloaderService {

	String getDownloadLocation();

	void download(AlldebridRemoteFile alldebridRemoteFile) throws ApiException;

	void download(RemoteFile remoteFile) throws ApiException;

	void download(RemoteFile remoteFile, String location) throws ApiException;

	void download(String link, String fileName) throws ApiException;

	void download(String link, String fileName, String location) throws ApiException;

}
