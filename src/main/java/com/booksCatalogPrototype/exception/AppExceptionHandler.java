package com.booksCatalogPrototype.exception;

import java.util.Date;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.booksCatalogPrototype.exception.custom.AuthenticationException;
import com.booksCatalogPrototype.exception.custom.AuthorException;
import com.booksCatalogPrototype.exception.custom.BookException;
import com.booksCatalogPrototype.exception.custom.ContentException;
import com.booksCatalogPrototype.exception.custom.CoverException;
import com.booksCatalogPrototype.exception.custom.PaginationException;
import com.booksCatalogPrototype.exception.custom.RateOutOfBoundException;
import com.booksCatalogPrototype.exception.custom.ReviewException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @ExceptionHandler(value = {PaginationException.class, AuthenticationException.class})
    public ResponseEntity<Object> handleSpecificException(Exception exception){

        String errorMessageDescription = exception.getLocalizedMessage();
        if (errorMessageDescription == null){
            errorMessageDescription = exception.toString();
        }
        ErrorMessage errorMessage = new ErrorMessage(new Date(), errorMessageDescription);
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {BookException.class, AuthorException.class,
            ReviewException.class, CoverException.class, ContentException.class})
    public ResponseEntity<Object> handleNotFoundExceptions(Exception exception){

        String errorMessageDescription = exception.getLocalizedMessage();
        if (errorMessageDescription == null){
            errorMessageDescription = exception.toString();
        }
        ErrorMessage errorMessage = new ErrorMessage(new Date(), errorMessageDescription);
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value ={MaxUploadSizeExceededException.class})
    public ModelAndView handleMaxSizeException(
            MaxUploadSizeExceededException exc,
            HttpServletRequest request,
            HttpServletResponse response) {

        ModelAndView modelAndView = new ModelAndView("file");
        modelAndView.getModel().put("message", "File too large.");
        return modelAndView;
    }

}
