package com.example.examples.domain.db;

import com.example.examples.domain.api.Person;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.util.Objects;

/**
 * We implemented a custom id generator to enable setting the id's ourselves by simply setting it! If it is not null,
 * it won't be overwritten. This is to enable DB state manipulation for test purposes from Karate using the API under
 * test.
 */
@Entity
@Table(name = "PERSONS")
public class PersonEntity {
    @Id
    @GeneratedValue(generator="idGenerator")
    @GenericGenerator(
            name="idGenerator", strategy="com.example.examples.hibernate.IdGenerator",
            parameters = @Parameter(name = "initial_value", value = "1001")
    )
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
