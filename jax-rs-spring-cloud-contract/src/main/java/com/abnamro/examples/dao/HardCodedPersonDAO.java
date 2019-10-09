package com.abnamro.examples.dao;

import com.abnamro.examples.dao.exceptions.DataAccessException;
import com.abnamro.examples.domain.api.Person;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of a person-finder that has no runtime dependencies and that we can control for test purposes.
 */
@SuppressWarnings("unchecked")
public class HardCodedPersonDAO implements PersonDAO<Person> {

    private List<Person> persons = Arrays.asList(
            new Person(1L, "Jan", "Janssen"),
            new Person(2L, "Pieter", "Pietersen"),
            new Person(3L, "Erik", "Eriksen"));

    @Override
    public List<Person> findAll() {
        return persons;
    }

    @Override
    public Person findById(long id) {
        Optional<Person> optional = persons.stream().filter(person -> person.getId() == id).findFirst();

        return optional.orElse(null);

    }

    @Override
    public List<Person> findWithLastName(String lastName) {
        return persons.stream().filter(person -> person.getLastName().equals(lastName)).collect(Collectors.toList());
    }

    @Override
    public void add(Person person) throws DataAccessException {
        persons.add(person);
    }
}
