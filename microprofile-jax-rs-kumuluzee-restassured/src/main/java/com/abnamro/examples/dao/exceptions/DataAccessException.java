package com.abnamro.examples.dao.exceptions;

public class DataAccessException extends Exception {

    public DataAccessException(final String message) {
        super(message);
    }

    public DataAccessException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
