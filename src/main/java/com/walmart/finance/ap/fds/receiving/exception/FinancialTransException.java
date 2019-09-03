package com.walmart.finance.ap.fds.receiving.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class FinancialTransException extends RuntimeException {

    public FinancialTransException(String message) {
        super(message);
    }
}

