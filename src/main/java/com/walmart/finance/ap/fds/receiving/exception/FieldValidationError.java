package com.walmart.finance.ap.fds.receiving.exception;

import lombok.Data;

@Data
public class FieldValidationError {

    private String fieldName;

    private String message;

    private boolean status;

    private String errorCode;

    public FieldValidationError(FieldValidationException e) {
        this.fieldName = e.getFieldName();
        this.message = e.getMessage();
        this.errorCode = e.getErrorCode();
        this.status = false;
    }
}
