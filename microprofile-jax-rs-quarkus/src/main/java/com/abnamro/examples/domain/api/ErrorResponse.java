package com.abnamro.examples.domain.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;


@RegisterForReflection // todo : document - https://quarkus.io/guides/rest-json
@JsonIgnoreProperties(ignoreUnknown = true)
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

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
