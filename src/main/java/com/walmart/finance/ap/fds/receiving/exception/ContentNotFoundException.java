package com.walmart.finance.ap.fds.receiving.exception;

import lombok.Getter;
import lombok.Setter;

/*
 * Exception Class for checked exceptions thrown by the application
 */
@Setter
@Getter
public class ContentNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -9178657694123604112L;

	private final String errorMessage;

	public ContentNotFoundException(String message, String errMessage) {
		super(message);
		this.errorMessage = errMessage;
	}
}
