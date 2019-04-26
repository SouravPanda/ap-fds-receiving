package com.walmart.store.receive.exception;

/*
 * Exception Class for checked exceptions thrown by the application
 */
public class ContentNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -9178657694123604112L;

	private final String message;

	public ContentNotFoundException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

}
