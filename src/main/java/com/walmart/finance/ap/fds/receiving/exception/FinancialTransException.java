package com.walmart.finance.ap.fds.receiving.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FinancialTransException extends RuntimeException {

    public FinancialTransException(String message) {
        super(message);
    }
}

