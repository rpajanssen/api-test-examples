package com.example.examples.domain.db;

import com.example.examples.domain.api.Person;

import javax.persistence.*;
import java.util.Objects;

// todo : explain why this class exists next to Person API model
/**
 * A table-generator is specified and used for the 'id' generation so we can control the initial value of the generated
 * id's. This way we have reserved the first 1000 entries for test data sets and programmatically added persons will
 * start with an id > 1000, so we will not have any uniqueness constraint validations.
 */
@Entity
@Table(name = "PERSONS")
@TableGenerator(name = "GEN_PERSONS", table = "ID_PERSONS", initialValue = 1000)
public class PersonEntity {
    @Id
    @GeneratedValue(generator = "GEN_PERSONS")
    private Long id;

    @Column(nullable = false, name = "FIRST_NAME")
    private String firstName;
    @Column(nullable = false, name = "LAST_NAME")
    private String lastName;

    public PersonEntity() {
        // required JPA spec
    }

    public PersonEntity(final Long id, final String firstName, final String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public PersonEntity(Person person) {
        this.id = person.getId();
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
    }

    public Long getId() {
        return id;
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
        if (!(o instanceof PersonEntity)) return false;
        PersonEntity person = (PersonEntity) o;
        return Objects.equals(id, person.id) && Objects.equals(firstName, person.firstName) && Objects.equals(lastName, person.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName);
    }

    @Override
    public String toString() {
        return "PersonEntity{" + "id=" + id + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' + '}';
    }
}
