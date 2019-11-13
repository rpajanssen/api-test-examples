package com.abnamro.examples.jaxrs.exceptionhandling;

import com.abnamro.examples.domain.api.ErrorResponse;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * If you want more control over the responses in case of exceptions you can control this by implementing custom
 * JAX-RS exception-mappers. You can specify which exception triggers the handler.
 *
 * A nice option is that you can return a nice error-response object instance that will then be transformed into
 * proper json. This way you do not have to create json replies yourself.
 *
 * The hard work needs to be done in the client! In this case, our resource could return a Person object and the client
 * may have client code expecting a Person object as result, strongly typed! So... what about the scenario of an
 * exception? If the call returns an ErrorResponse but the client expects a Person... things go south very quickly!
 *
 * Roughly, you have three scenarios:
 *
 * 1) You use a client that automatically unmarshalls http responses that do not have a 20x status code into exceptions.
 * This implies that you will loose the specifics of the returned ErrorResponse but you will not runtime not
 * encounter any class-cast exceptions troubles alike! You just need to implement proper error handling in your client
 * code, specifics depend on your client. As example Jersey will throw BadRequestExceptions on 40x responses like can
 * be seen in our test example.
 *
 * 2) You use the client to receive the raw response without automatic unmarshalling and write your own
 * de-serialization that will handle success and error responses. Normally you verify the http response code and
 * depending on that you select a (self written) handler for the response, either transforming the response into
 * the Person object or an ErrorResponse. This will complicate your clients, and you have to repeat this for all
 * your clients! So you will probably implement your own http client class that implements all this boilerplate - the
 * Spring successor is born :)
 *
 * 3) You use a client that can be configured with an interceptor that can trigger the required unmarshalling
 * handler. Here you shift the problem a bit more towards the location where it needs to be handled. It depends
 * on the http client implementation if this option is supported. As example Springs RestTemplate has such an option
 * where you can intercept the response and replace the standard deserialization. But... we then still have not resolved
 * the Java client code problem, because that needs to be strongly typed. What you normally will end up with is some
 * kind of ResponseWrapper class that will have a Person and ErrorResponse property - of course use generics if you need
 * to handle many different api responses - and your custom response interceptor then will return an instance of the
 * wrapper, and you client code will look like : ResponseWrapper result = client.get(....);
 * Then in the client you can verify if a Person or ErrorResponse is present in the wrapper and handle the result
 * accordingly.
 *
 * Example default exception handler that will return a proper error-response in the response body and a
 * HTTP 500 status code since we encounter some unexpected exception.
 *
 * NOTE: we return the message of the exception and root-cause but this may expose internals (information leak). So
 * normally you need to sanitize these messages or construct a new message that you completely control and does
 * not leak information.
 */
@Provider
public class NotFoundExceptionHandler implements ExceptionMapper<NotFoundException> {
    private static final String MESSAGE_TEMPLATE = "%s %s";

    @Override
    public Response toResponse(NotFoundException exception) {
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
