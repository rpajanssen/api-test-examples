package com.example.examples.rest.filters;

import com.example.examples.domain.api.ErrorResponse;
import com.example.examples.exceptions.ErrorCodes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * Custom servlet filter that will catch any unhandled runtime exception and transform it into a proper
 * response that is consistent with the our api response model.
 */
@Component
@Order(0)
public class ErrorHandlingFilter extends OncePerRequestFilter {
    private final ObjectMapper mapper;

    public ErrorHandlingFilter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            super.doFilter(request, response, filterChain);
        } catch (RuntimeException e) {
            // todo : it would be wise to log the exception information here as well

            ErrorResponse errorResponse = new ErrorResponse(ErrorCodes.UNEXPECTED_EXCEPTION.getCode());

            response.setStatus(ErrorCodes.UNEXPECTED_EXCEPTION.getHttpStatus().value());
            response.getWriter().write(convertObjectToJson(errorResponse));
        }
    }

    private  String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }

        return mapper.writeValueAsString(object);
    }
}
