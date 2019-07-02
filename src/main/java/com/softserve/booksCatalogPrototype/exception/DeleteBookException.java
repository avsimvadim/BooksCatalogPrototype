package com.softserve.booksCatalogPrototype.exception;

public class DeleteBookException extends RuntimeException {
    public DeleteBookException() {
    }

    public DeleteBookException(String message) {
        super(message);
    }
}
