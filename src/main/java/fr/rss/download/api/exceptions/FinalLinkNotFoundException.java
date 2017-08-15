package fr.rss.download.api.exceptions;

import org.springframework.http.HttpStatus;

public class FinalLinkNotFoundException extends ApiException {

	private static final long serialVersionUID = 8824902883580248304L;

	public FinalLinkNotFoundException(HttpStatus code, String msg) {
		super(code, msg);
	}

}
