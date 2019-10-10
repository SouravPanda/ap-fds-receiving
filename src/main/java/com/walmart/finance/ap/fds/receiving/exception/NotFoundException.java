package com.walmart.finance.ap.fds.receiving.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = -3255314823136086298L;
    @Getter
    @Setter
    private final String errorMessage;

    public NotFoundException(String message, String errMessage) {
        super(message);
        this.errorMessage = errMessage;
    }
}

