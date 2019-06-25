package com.walmart.finance.ap.fds.receiving.exception;

public class FieldValidationException extends RuntimeException {

	private static final long serialVersionUID = 1280584456245967943L;

	private final String fieldName;

	private final String errorCode;

    private final String message;

    public FieldValidationException(String message, String errorMessage,String errorCode, String fieldName) {
        super(message);
        this.errorCode = errorCode;
        this.message=errorMessage;
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
