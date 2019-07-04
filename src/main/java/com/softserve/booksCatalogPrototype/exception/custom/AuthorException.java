package com.softserve.booksCatalogPrototype.exception.custom;

public class AuthorException extends RuntimeException {
    public AuthorException() {
    }

    public AuthorException(String message) {
        super(message);
    }
}
