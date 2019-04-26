package com.walmart.store.receive.exception;

public class InvalidValueException extends RuntimeException {

    private static final long serialVersionUID = -741159283957103129L;

    private final String message;

    public InvalidValueException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
