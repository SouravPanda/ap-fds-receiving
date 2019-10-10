package com.walmart.finance.ap.fds.receiving.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class SearchCriteriaException extends RuntimeException {

    private static final long serialVersionUID = -1266556740546743027L;
    @Getter
    @Setter
    private final String errorMessage;
    public SearchCriteriaException(String message, String errMessage) {
        super(message);
        this.errorMessage = errMessage;
    }
}

