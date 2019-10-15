package com.example.examples.rest.exceptionhandlers;

import com.example.examples.domain.api.ErrorResponse;
import com.example.examples.domain.api.Person;
import com.example.examples.exceptions.ErrorCodes;
import com.example.examples.exceptions.PersonNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This error-handler will only handle exceptions from the person-resource since it is bound to that resource using
 * the PersonResourceExceptionHandling-annotation.
 *
 * Note that this error handler does not return meaningful exceptions in the sense that if you do not have a list of
 * possible error codes explaining what they stand for, you do not know what went wrong! This is helpful in making
 * life a bit harder for hackers by not giving away information (information leakage).
 *
 * Note that with JAX-RS we can use ExceptionMappers as equivalent.
 */
@ControllerAdvice (annotations = PersonResourceExceptionHandling.class )
public class PersonResourceExceptionHandler {
    @ResponseBody
    @ExceptionHandler(PersonNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse<Person> personNotFoundHandler(PersonNotFoundException exception) {
        return new ErrorResponse<>(exception.getPerson(), ErrorCodes.PERSON_NOT_FOUND.getCode());
    }

    /**
     * This handler handles the bean validations defined for the bean arguments of the person rest-resource. The spring-
     * binder will bind the json to an instance of a person an perform all validations. If any validation fails a
     * MethodArgumentNotValidException will be throw.
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse<Person> validationExceptionHandler(MethodArgumentNotValidException exception) {
        return new ErrorResponse<>((Person)exception.getBindingResult().getTarget(), ErrorCodes.PERSON_INVALID.getCode());
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorResponse unexpectedExceptionHandler(Exception exception) {
        // todo : it would be wise to log the exception information here as well

        return new ErrorResponse(ErrorCodes.UNEXPECTED_EXCEPTION.getCode());
    }
}
