package com.walmart.finance.ap.fds.receiving.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ReceivingError {

    private boolean success;
    private LocalDateTime timeStamp;
    private ErrorDetails error;
}
