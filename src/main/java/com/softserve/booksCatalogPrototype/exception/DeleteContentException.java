package com.softserve.booksCatalogPrototype.exception;

public class DeleteContentException extends RuntimeException {
    public DeleteContentException() {
    }

    public DeleteContentException(String message) {
        super(message);
    }
}
