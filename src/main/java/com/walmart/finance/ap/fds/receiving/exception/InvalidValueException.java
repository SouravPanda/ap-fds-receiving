package com.walmart.finance.ap.fds.receiving.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InvalidValueException extends RuntimeException {

    private static final long serialVersionUID = -741159283957103129L;

    private final String errorMessage;

    public InvalidValueException(String message, String errMessage) {
        super(message);
        this.errorMessage = errMessage;
    }
}