package com.walmart.finance.ap.fds.receiving.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ReceivingError {

    private int errorCode;
    private String errorMessage;
    private List<ErrorDetails> details;
}
