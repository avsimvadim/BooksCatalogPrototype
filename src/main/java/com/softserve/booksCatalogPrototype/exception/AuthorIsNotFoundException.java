package com.softserve.booksCatalogPrototype.exception;

public class AuthorIsNotFoundException extends RuntimeException {
    public AuthorIsNotFoundException() {
    }

    public AuthorIsNotFoundException(String message) {
        super(message);
    }
}
