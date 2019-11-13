package com.abnamro.examples.domain.api;

public class ErrorResponse {
    private String code;
    private String message;

    public ErrorResponse() {
        // only here for (de)serialization frameworks
    }

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
