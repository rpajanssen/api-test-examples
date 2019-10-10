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
    @NotEmpty(message = "lastName is not allowed to be empty")
    @Size(min=2, message="lastName should have at least 2 characters")
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

    public Person(Person original) {
        this.id = original.getId();
        this.firstName = original.getFirstName();
        this.lastName = original.getLastName();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
