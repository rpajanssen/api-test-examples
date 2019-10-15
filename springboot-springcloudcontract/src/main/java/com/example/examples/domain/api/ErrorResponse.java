package com.example.examples.domain.api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse<T> {
    private T data;
    private String code;

    public ErrorResponse() {
        // required by (de)serialization framework
    }

    public ErrorResponse(String code) {
        this.code = code;
    }

    public ErrorResponse(T data, String code) {
        this.data = data;
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
