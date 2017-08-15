package fr.rss.download.api.service;

import fr.rss.download.api.exceptions.ApiException;
import fr.rss.download.api.model.AlldebridRemoteFile;
import fr.rss.download.api.model.RemoteFile;

public interface IDownloaderService {

	String getDownloadLocation();

	String download(AlldebridRemoteFile alldebridRemoteFile) throws ApiException;

	String download(AlldebridRemoteFile alldebridRemoteFile, String location) throws ApiException;

	String download(RemoteFile remoteFile) throws ApiException;

	String download(RemoteFile remoteFile, String location) throws ApiException;

	String download(String link, String fileName) throws ApiException;

	String download(String link, String fileName, String location) throws ApiException;

}
