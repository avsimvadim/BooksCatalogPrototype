package com.softserve.booksCatalogPrototype.exception;

public class RateOutOfBoundException extends RuntimeException{

    public RateOutOfBoundException() {
    }

    public RateOutOfBoundException(String message){
        super(message);
    }
}
