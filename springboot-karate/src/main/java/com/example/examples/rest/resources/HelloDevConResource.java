package com.example.examples.rest.resources;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hello")
public class HelloDevConResource {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String hello() {
        return "Hi DevCon";
    }
}
