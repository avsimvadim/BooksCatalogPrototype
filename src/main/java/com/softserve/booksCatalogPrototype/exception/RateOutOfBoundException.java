package com.softserve.booksCatalogPrototype.exception;

public class RateOutOfBoundException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public RateOutOfBoundException(String message){
        super(message);
    }
}
