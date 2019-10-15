package com.abnamro.examples.dao.exceptions;

public class PersonAlreadyExistsException extends Exception {

    public PersonAlreadyExistsException(final String message) {
        super(message);
    }

    public PersonAlreadyExistsException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
