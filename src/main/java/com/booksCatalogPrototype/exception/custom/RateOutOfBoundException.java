package com.booksCatalogPrototype.exception.custom;

public class RateOutOfBoundException extends RuntimeException {
    public RateOutOfBoundException() {
    }

    public RateOutOfBoundException(String message) {
        super(message);
    }
}
