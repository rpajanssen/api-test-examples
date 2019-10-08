package com.abnamro.examples.domain.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;

/**
 * Can you guess why there are no setters?
 *
 * Though... keeping this up and using a (de)serialization framework can be difficult!
 *
 * Note: We added the validation annotations to demonstrate automatic validation and custom error handling with JAX-RS.
 *
 * Note: we have generified some test classes, using them for different resource implementations that implement the same
 * generic resource interface. The abstract unit test expects a less complex json then the more specific unit test for
 * a concrete resource. To prevent json incompatibilities we use the jackson-data-bind @JsonIgnoreProperties
 * annotation so that clients that call a resource that returning json with more properties then defined in this
 * class will not run into runtime deserialization exceptions.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersistablePerson {
    private long id;

    @NotNull(message = "firstName is not allowed to be null")
    private String firstName;
    @NotNull(message = "lastName is not allowed to be null")
    private String lastName;

    public PersistablePerson() {
        // only here for (de)serialization frameworks
    }

    public PersistablePerson(final String firstName, final String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public PersistablePerson(long id, final String firstName, final String lastName) {
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
