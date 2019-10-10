package com.walmart.finance.ap.fds.receiving.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = 65206971152990447L;

    @Getter
    private final String errorMessage;

    public BadRequestException(String message, String errorMessage) {
        super(message);
        this.errorMessage = errorMessage;
    }
}
