package com.abnamro.examples.jaxrs.exceptionhandling;

import com.abnamro.examples.dao.exceptions.PersonDoesNotExistException;
import com.abnamro.examples.domain.api.ErrorResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Example default exception handler that will return a proper error-response in the response body and a
 * HTTP 400 status code since we encountered an exception during input validation.
 *
 * NOTE: we return the message of the exception and root-cause but this may expose internals (information leak). So
 * normally you need to sanitize these messages or construct a new message that you completely control and does
 * not leak information.
 *
 * See DefaultExceptionHandler class for a lot more explanation about the patterns and examples!
 */
@Provider
public class PersonDoesNotExistExceptionHandler implements ExceptionMapper<PersonDoesNotExistException> {
    private static final String MESSAGE_TEMPLATE = "%s %s";

    @Override
    public Response toResponse(PersonDoesNotExistException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(Response.Status.NOT_FOUND.name(), buildErrorMessage(exception)))
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

    private String buildErrorMessage(Exception exception) {
        Throwable rootCause = findRootCause(exception);
        if(rootCause != null) {
            return String.format(MESSAGE_TEMPLATE, exception.getMessage(), rootCause.getMessage());
        }

        return exception.getMessage();
    }

    private Throwable findRootCause(Exception exception) {
        Throwable rootCause = exception.getCause();
        while(rootCause != null && rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }
}
