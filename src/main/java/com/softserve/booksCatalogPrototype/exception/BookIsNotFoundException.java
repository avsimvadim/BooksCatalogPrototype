package com.softserve.booksCatalogPrototype.exception;

public class BookIsNotFoundException extends RuntimeException {
    public BookIsNotFoundException() {
    }

    public BookIsNotFoundException(String message) {
        super(message);
    }
}
