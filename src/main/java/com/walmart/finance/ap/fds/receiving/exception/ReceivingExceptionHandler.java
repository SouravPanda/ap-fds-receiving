package com.walmart.finance.ap.fds.receiving.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
                new ReceivingError(false, LocalDateTime.now(), detailsOfErr), new HttpHeaders(), HttpStatus.NO_CONTENT);
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

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> constraintViolationExceptionHandler(
            Exception ex, ConstraintViolationException e, WebRequest request) {
        List<String> details = new ArrayList<>();
        ErrorDetails detailsOfErr = new ErrorDetails(104, ex.getMessage(), details);
        return new ResponseEntity<>(
                new ReceivingError(false, LocalDateTime.now(), detailsOfErr), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> details = ex
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        int length=details.size();
        String addOn = "as it(they) is(are) a mandatory field/fields.";
        details.add(addOn);
        ErrorDetails detailsOfErr = new ErrorDetails(103, details.subList(0,length).toString(), details);
        return new ResponseEntity<Object>(
                new ReceivingError(false, LocalDateTime.now(), detailsOfErr), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<Object> BadRequestExceptionHandler(
            Exception ex, BadRequestException e, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage() + " " + e.getErrorMessage());
        ErrorDetails errorDetails = new ErrorDetails(105, ex.getMessage(), details);
        return new ResponseEntity<>(
                new ReceivingError(false, LocalDateTime.now(), errorDetails), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MandatoryPatameterMissingException.class})
    public ResponseEntity<Object> MandatoryPatameterMissingExceptionHandler(
            Exception ex, MandatoryPatameterMissingException e, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage() + " " + e.getErrorMessage());
        ErrorDetails errorDetails = new ErrorDetails(103, ex.getMessage(), details);
        return new ResponseEntity<>(
                new ReceivingError(false, LocalDateTime.now(), errorDetails), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}

