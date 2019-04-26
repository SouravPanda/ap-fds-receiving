package com.walmart.store.receive.exception;

public class FieldValidationException extends RuntimeException {

	private static final long serialVersionUID = 1280584456245967943L;

	private final String fieldName;

	private final String errorCode;

    private final String message;

    public FieldValidationException(String message, String errorCode, String fieldName) {
        super(message);
        this.errorCode = errorCode;
        this.fieldName = fieldName;
        this.message = message;
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
