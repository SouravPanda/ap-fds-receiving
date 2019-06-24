package com.walmart.finance.ap.fds.receiving.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ReceivingExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({FieldValidationException.class})
    public ResponseEntity<Object> fieldValidation(
            Exception ex, FieldValidationException e, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage() + " " + e.getMessage());
        ErrorDetails detailsOfErr = new ErrorDetails(105, ex.getMessage(), details);
        return new ResponseEntity<>(
                new ReceivingError(false, LocalDateTime.now(), detailsOfErr), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({InvalidValueException.class})
    public ResponseEntity<Object> invalidValueExceptionHandler(
            Exception ex, InvalidValueException e, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage() + " " + e.getErrorMessage());
        ErrorDetails detailsOfErr = new ErrorDetails(105, ex.getMessage(), details);
        return new ResponseEntity<>(
                new ReceivingError(false, LocalDateTime.now(), detailsOfErr), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({ContentNotFoundException.class})
    public ResponseEntity<Object> contentNotFoundExceptionHandler(
            Exception ex, ContentNotFoundException e, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage() + " " + e.getErrorMessage());
        ErrorDetails detailsOfErr = new ErrorDetails(0, ex.getMessage(), details);
        return new ResponseEntity<>(
                new ReceivingError(false, LocalDateTime.now(), detailsOfErr), new HttpHeaders(), HttpStatus.OK);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<Object> notFoundExceptionHandler(
            Exception ex, NotFoundException e, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage() + " " + e.getErrorMessage());
        ErrorDetails detailsOfErr = new ErrorDetails(0, ex.getMessage(), details);
        return new ResponseEntity<>(
                new ReceivingError(false, LocalDateTime.now(), detailsOfErr), new HttpHeaders(), HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler({SearchCriteriaException.class})
    public ResponseEntity<Object> searchCriteriaExceptionHandler(
            Exception ex, SearchCriteriaException e, WebRequest request) {
        List<String> details = new ArrayList<>();
        ErrorDetails detailsOfErr = new ErrorDetails(0, ex.getMessage(), details);
        return new ResponseEntity<>(
                new ReceivingError(false, LocalDateTime.now(), detailsOfErr), new HttpHeaders(), HttpStatus.OK);
    }

}
