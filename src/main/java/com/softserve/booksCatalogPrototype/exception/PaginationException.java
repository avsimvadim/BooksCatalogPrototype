package com.softserve.booksCatalogPrototype.exception;

public class PaginationException extends RuntimeException {
    public PaginationException() {
    }

    public PaginationException(String message) {
        super(message);
    }
}
