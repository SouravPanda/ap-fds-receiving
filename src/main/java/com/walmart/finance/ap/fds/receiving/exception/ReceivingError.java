package com.walmart.finance.ap.fds.receiving.exception;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReceivingError {

    private int errorCode;

    private String errorMessage;

    private LocalDateTime timestamp;

    public ReceivingError(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.timestamp = LocalDateTime.now();
    }

}
