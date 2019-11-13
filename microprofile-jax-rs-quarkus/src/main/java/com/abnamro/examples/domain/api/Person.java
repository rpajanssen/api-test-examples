package com.abnamro.examples.domain.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * Note: We added the validation annotations to demonstrate automatic validation and custom error handling with JAX-RS.
 *
 * Note: we have generified some test classes, using them for different resource implementations that implement the same
 * generic resource interface. The abstract unit test expects a less complex json then the more specific unit test for
 * a concrete resource. To prevent json incompatibilities we use the jackson-data-bind @JsonIgnoreProperties
 * annotation so that clients that call a resource that returning json with more properties then defined in this
 * class will not run into runtime deserialization exceptions.
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return id == person.id && Objects.equals(firstName, person.firstName) && Objects.equals(lastName, person.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName);
    }
}
