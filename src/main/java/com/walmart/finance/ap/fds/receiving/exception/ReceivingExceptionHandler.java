package com.walmart.finance.ap.fds.receiving.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ReceivingExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({FieldValidationException.class})
    public ResponseEntity<Object> fieldValidation(
            Exception ex, WebRequest request) {

        return new ResponseEntity<>(
                new FieldValidationError((FieldValidationException) ex), new HttpHeaders(), HttpStatus.OK);
    }


    @ExceptionHandler({InvalidValueException.class})
    public ResponseEntity<Object> invalidValueExceptionHandler(
            Exception ex, WebRequest request) {
        return new ResponseEntity<>(
                new ReceivingError(200, ex.getMessage(), LocalDateTime.now()), new HttpHeaders(), HttpStatus.OK);
    }


    @ExceptionHandler({ContentNotFoundException.class})
    public ResponseEntity<Object> contentNotFoundExceptionHandler(
            Exception ex, WebRequest request) {
        return new ResponseEntity<>(
                new ReceivingError(204, ex.getMessage(), LocalDateTime.now()), new HttpHeaders(), HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<Object> notFoundExceptionHandler(
            Exception ex, WebRequest request) {
        return new ResponseEntity<>(
                new ReceivingError(204, ex.getMessage(), LocalDateTime.now()), new HttpHeaders(), HttpStatus.OK);
    }

    @ExceptionHandler({SearchCriteriaException.class})
    public ResponseEntity<Object> searchCriteriaExceptionHandler(
            Exception ex, WebRequest request) {
        return new ResponseEntity<>(
                new ReceivingError(200, ex.getMessage(), LocalDateTime.now()), new HttpHeaders(), HttpStatus.OK);
    }

}
