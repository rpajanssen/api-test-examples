package com.example.examples.domain.api;

import com.example.examples.domain.db.PersonEntity;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Objects;

public class Person {
    private Long id;

    @NotEmpty(message = "firstName is not allowed to be empty")
    @Size(min=2, message="firstName should have at least 2 characters")
    private String firstName;
    @NotEmpty(message = "lastName is not allowed to be empty")
    @Size(min=2, message="lastName should have at least 2 characters")
    private String lastName;

    public Person() {
        // required by (de)serialization framework
    }

    public Person(final Long id, final String firstName, final String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Person(PersonEntity personEntity) {
        this.id = personEntity.getId();
        this.firstName = personEntity.getFirstName();
        this.lastName = personEntity.getLastName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    /*
        The methods below are required to perform comparisons (in unit tests) and to get some readable output.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id) && Objects.equals(firstName, person.firstName) && Objects.equals(lastName, person.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName);
    }

    @Override
    public String toString() {
        return "Person{" + "id=" + id + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' + '}';
    }
}
