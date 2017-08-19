package fr.rss.download.api.model;

import org.springframework.http.HttpStatus;

public class ApiErrorResponse {

	private HttpStatus status;
	private String message;

	public ApiErrorResponse(HttpStatus status, String message) {
		setStatus(status);
		setMessage(message);
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
