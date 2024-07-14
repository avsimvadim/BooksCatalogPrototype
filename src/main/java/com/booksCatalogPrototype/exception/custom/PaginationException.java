package com.booksCatalogPrototype.exception.custom;

public class PaginationException extends RuntimeException {
    public PaginationException() {
    }

    public PaginationException(String message) {
        super(message);
    }
}
