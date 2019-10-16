package com.abnamro.examples.dao.exceptions;

public class PersonDoesNotExistException extends Exception {

    public PersonDoesNotExistException(final String message) {
        super(message);
    }

    public PersonDoesNotExistException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
