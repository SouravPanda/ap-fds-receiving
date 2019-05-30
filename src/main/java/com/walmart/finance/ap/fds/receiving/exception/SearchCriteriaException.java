package com.walmart.finance.ap.fds.receiving.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SearchCriteriaException extends RuntimeException {

    private static final long serialVersionUID = -1266556740546743027L;
    @Getter
    private final String message;

}

