package com.softserve.bookscatalogpprototype.exception;

public class RateOutOfBoundException extends Throwable{
    public RateOutOfBoundException(){
        super();
    }

    public RateOutOfBoundException(String message){
        super(message);
    }
}
