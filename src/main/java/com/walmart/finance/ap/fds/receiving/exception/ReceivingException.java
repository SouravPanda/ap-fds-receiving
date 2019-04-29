package com.walmart.finance.ap.fds.receiving.exception;

public class ReceivingException extends Exception {

	private static final long serialVersionUID = -841157203957108129L;
	
	private final String message;

    public ReceivingException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
