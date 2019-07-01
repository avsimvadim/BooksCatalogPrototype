package com.softserve.booksCatalogPrototype.exception;

public class EntityException extends RuntimeException {
    public EntityException() {
    }

    public EntityException(String message) {
        super(message);
    }
}
