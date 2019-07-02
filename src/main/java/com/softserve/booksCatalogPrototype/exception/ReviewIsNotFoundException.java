package com.softserve.booksCatalogPrototype.exception;

public class ReviewIsNotFoundException extends RuntimeException {
    public ReviewIsNotFoundException() {
    }

    public ReviewIsNotFoundException(String message) {
        super(message);
    }
}
