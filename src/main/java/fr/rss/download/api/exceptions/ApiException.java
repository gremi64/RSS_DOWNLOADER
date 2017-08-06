package fr.rss.download.api.exceptions;

import org.springframework.http.HttpStatus;

public class ApiException extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = -2174637728997742359L;
	private HttpStatus code;

	public ApiException(HttpStatus code, String msg) {
		super(msg);
		this.code = code;
	}
}
