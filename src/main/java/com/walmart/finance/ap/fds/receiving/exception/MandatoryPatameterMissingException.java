package com.walmart.finance.ap.fds.receiving.exception;

import lombok.Getter;

public class MandatoryPatameterMissingException extends RuntimeException {

    @Getter
    private final String errorMessage;

    public MandatoryPatameterMissingException(String message, String errorMessage) {
        super(message);
        this.errorMessage = errorMessage;
    }
}
