package com.softserve.booksCatalogPrototype.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleAnyException(Exception exception){

        String errorMessageDescription = exception.getLocalizedMessage();
        if (errorMessageDescription == null){
            errorMessageDescription = exception.toString();
        }
        ErrorMessage errorMessage = new ErrorMessage(new Date(), errorMessageDescription);
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {RateOutOfBoundException.class})
    public ResponseEntity<Object> handleRateOutOfBoundExceptionException(RateOutOfBoundException exception){

        String errorMessageDescription = exception.getLocalizedMessage();
        if (errorMessageDescription == null){
            errorMessageDescription = exception.toString();
        }
        ErrorMessage errorMessage = new ErrorMessage(new Date(), errorMessageDescription);
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(value = {EntityException.class, PaginationException.class})
    public ResponseEntity<Object> handleSpecificException(Exception exception){

        String errorMessageDescription = exception.getLocalizedMessage();
        if (errorMessageDescription == null){
            errorMessageDescription = exception.toString();
        }
        ErrorMessage errorMessage = new ErrorMessage(new Date(), errorMessageDescription);
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {BookIsNotFoundException.class, AuthorIsNotFoundException.class})
    public ResponseEntity<Object> handleBookIsNotFoundExceptionException(Exception exception){

        String errorMessageDescription = exception.getLocalizedMessage();
        if (errorMessageDescription == null){
            errorMessageDescription = exception.toString();
        }
        ErrorMessage errorMessage = new ErrorMessage(new Date(), errorMessageDescription);
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {UploadCoverException.class, UploadContentException.class})
    public ResponseEntity<Object> handleUploadCoverExceptionException(Exception exception){

        String errorMessageDescription = exception.getLocalizedMessage();
        if (errorMessageDescription == null){
            errorMessageDescription = exception.toString();
        }
        ErrorMessage errorMessage = new ErrorMessage(new Date(), errorMessageDescription);
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE);
    }
}
