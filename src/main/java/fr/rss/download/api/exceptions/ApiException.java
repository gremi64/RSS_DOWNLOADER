package fr.rss.download.api.exceptions;

import org.springframework.http.HttpStatus;

public class ApiException extends Exception {

	private static final long serialVersionUID = -2174637728997742359L;

	private HttpStatus code;

	public ApiException(HttpStatus code, String msg) {
		super(msg);
		setCode(code);
	}

	public HttpStatus getCode() {
		return code;
	}

	public void setCode(HttpStatus code) {
		this.code = code;
	}
}
