package com.example.examples.dao.exceptions;

import com.example.examples.domain.api.Person;

public class PersonNotFoundException extends Exception {
    private final Person person;

    public PersonNotFoundException(Person person, final String message) {
        super((message));
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }
}
