package com.example.examples.rest.resources;

import com.example.examples.domain.api.ErrorResponse;
import com.example.examples.exceptions.ErrorCodes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Error controller that transforms all rerouted errors into a response using the required rest-api domain model.
 * This error controller will typically handle errors that have risen before execution of the REST Resource was or after
 * execution flow has returned from the REST Resource. Exceptions will then NOT be handled by a (global) exception
 * handler.
 *
 * As example when Zuul blocks the calls to a resource by use of the RateLimitPreFilter these blocked calls will result
 * in a redirect to this error path.
 */
@RestController
public class ErrorHandlerResource implements ErrorController {
    private static final String PROPERTY_STATUS_CODE = "javax.servlet.error.status_code";
    private static final String PROPERTY_EXCEPTION  = "javax.servlet.error.exception";

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping(value = "/error", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ErrorResponse> jsonError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(PROPERTY_STATUS_CODE);
        Exception exception = (Exception) request.getAttribute(PROPERTY_EXCEPTION);
        //exception.printStackTrace();

        // todo : it would be wise to log the exception information here as well

        ErrorCodes errorCode = ErrorCodes.fromHttpStatusCode(statusCode);
        ErrorResponse<?> errorResponse = new ErrorResponse<>(errorCode.getCode());

        return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
    }
}
