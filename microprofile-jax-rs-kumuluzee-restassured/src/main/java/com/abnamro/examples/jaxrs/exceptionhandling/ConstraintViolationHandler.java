package com.abnamro.examples.jaxrs.exceptionhandling;

import com.abnamro.examples.domain.api.ErrorResponse;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
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
public class ConstraintViolationHandler implements ExceptionMapper<ConstraintViolationException> {
    private static final String CONSTRAINT_TEMPLATE = "%s %s\n";

    /**
     * Example default exception handler that will return a proper error-response in the response body and a
     * HTTP 500 status code since we encounter some unexpected exception.
     */
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(Response.Status.BAD_REQUEST.name(), gatherConstraintViolations(exception)))
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

    private String gatherConstraintViolations(ConstraintViolationException exception) {
        StringBuilder message = new StringBuilder();

        for (ConstraintViolation<?> cv : exception.getConstraintViolations()) {
            message.append(String.format(CONSTRAINT_TEMPLATE, cv.getPropertyPath(), cv.getMessage()));
        }

        return message.toString();
    }
}
