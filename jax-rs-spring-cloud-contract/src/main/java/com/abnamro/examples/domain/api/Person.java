package com.abnamro.examples.domain.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * We added the validation annotations to demonstrate automatic validation and custom error handling with JAX-RS.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Person {
    private long id;

    @NotEmpty(message = "firstName is not allowed to be empty")
    @Size(min=2, message="firstName should have at least 2 characters")
    private String firstName;
    @NotEmpty(message = "firstName is not allowed to be empty")
    @Size(min=2, message="firstName should have at least 2 characters")
    private String lastName;

    public Person() {
        // only here for (de)serialization frameworks
    }

    public Person(final String firstName, final String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Person(long id, final String firstName, final String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
