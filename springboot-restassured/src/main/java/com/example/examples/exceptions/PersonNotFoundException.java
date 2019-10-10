package com.example.examples.exceptions;

import com.example.examples.domain.api.Person;

public class PersonNotFoundException extends Exception {
    private final Person person;

    public PersonNotFoundException(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }
}
