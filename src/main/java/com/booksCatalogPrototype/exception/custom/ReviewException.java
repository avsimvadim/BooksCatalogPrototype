package com.booksCatalogPrototype.exception.custom;

public class ReviewException extends RuntimeException{
    public ReviewException() {
    }

    public ReviewException(String message) {
        super(message);
    }
}
