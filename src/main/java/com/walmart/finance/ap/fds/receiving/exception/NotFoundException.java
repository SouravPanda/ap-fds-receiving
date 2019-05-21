package com.walmart.finance.ap.fds.receiving.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 8508533131733120983L;

    @Getter
    private final String message;

}
