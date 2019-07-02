package com.softserve.booksCatalogPrototype.exception;

public class ReviewDeleteException extends RuntimeException{
    public ReviewDeleteException() {
    }

    public ReviewDeleteException(String message) {
        super(message);
    }
}
