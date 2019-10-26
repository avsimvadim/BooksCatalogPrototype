package com.softserve.booksCatalogPrototype.exception.custom;

public class AuthenticationException extends RuntimeException {
	public AuthenticationException() {
		super();
	}

	public AuthenticationException(String message) {
		super(message);
	}
}
