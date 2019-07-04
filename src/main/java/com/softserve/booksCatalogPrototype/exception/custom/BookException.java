package com.softserve.booksCatalogPrototype.exception.custom;

public class BookException extends RuntimeException {

    public BookException() {
    }

    public BookException(String message) {
        super(message);
    }
}
