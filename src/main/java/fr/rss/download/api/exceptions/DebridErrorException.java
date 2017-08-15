package fr.rss.download.api.exceptions;

import org.springframework.http.HttpStatus;

public class DebridErrorException extends ApiException {

	private static final long serialVersionUID = 1600942262103589752L;

	public DebridErrorException(HttpStatus code, String msg) {
		super(code, msg);
	}

}
