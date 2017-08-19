package fr.rss.download.api.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import fr.rss.download.api.exceptions.ApiException;
import fr.rss.download.api.model.ApiErrorResponse;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

	private HttpHeaders headers = new HttpHeaders();

	@ExceptionHandler(value = { ApiException.class })
	protected ResponseEntity<Object> handleApiException(ApiException ex, WebRequest request) {
		log.error("ApiException : ", ex);

		ApiErrorResponse apiError = new ApiErrorResponse(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage());

		return handleExceptionInternal(ex, apiError, headers, apiError.getStatus(), request);
	}

	@ExceptionHandler(value = { Exception.class })
	protected ResponseEntity<Object> handleApiException(Exception ex,
			HttpStatus status,
			WebRequest request) {
		log.error("Exception : ", ex);

		ApiErrorResponse apiError = new ApiErrorResponse(HttpStatus.BAD_REQUEST, "Erreur ... " + ex.getLocalizedMessage());

		return handleExceptionInternal(ex, apiError, headers, apiError.getStatus(), request);
	}

	//	@ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class })
	//	protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
	//		String bodyOfResponse = "This should be application specific";
	//		return handleExceptionInternal(ex, bodyOfResponse,
	//				new HttpHeaders(), HttpStatus.CONFLICT, request);
	//	}
}