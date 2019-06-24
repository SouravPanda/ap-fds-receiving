package com.walmart.finance.ap.fds.receiving.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ReceivingError {

    private boolean success;
    private LocalDateTime timeStamp;
    private int errorCode;
    private String errorMessage;
    private List<ErrorDetails> details;
}
