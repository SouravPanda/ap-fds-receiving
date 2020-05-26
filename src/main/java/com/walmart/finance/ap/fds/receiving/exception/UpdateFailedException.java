package com.walmart.finance.ap.fds.receiving.exception;

import lombok.Getter;
import lombok.Setter;

/*
 * Exception Class for checked exceptions thrown by the application
 */
@Setter
@Getter
public class UpdateFailedException extends RuntimeException {

    private static final long serialVersionUID = 1862183279553718330L;

    private final String message;

    public UpdateFailedException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}