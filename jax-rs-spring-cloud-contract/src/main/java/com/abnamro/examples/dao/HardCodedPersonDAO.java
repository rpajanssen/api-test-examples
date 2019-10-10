package com.abnamro.examples.dao;

import com.abnamro.examples.dao.exceptions.DataAccessException;
import com.abnamro.examples.dao.exceptions.InvalidDataException;
import com.abnamro.examples.domain.api.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of a person-finder that has no runtime dependencies and that we can control for test purposes.
 */
@SuppressWarnings("unchecked")
public class HardCodedPersonDAO implements PersonDAO<Person> {

    private final static List<Person> initialContent = Arrays.asList(
            new Person(1L, "Jan", "Janssen"),
            new Person(2L, "Pieter", "Pietersen"),
            new Person(3L, "Erik", "Eriksen"));

    private static List<Person> persons = new ArrayList<>();

    public HardCodedPersonDAO() {
        reset();
    }

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
    public void add(Person person) throws DataAccessException, InvalidDataException {
        if(exists(person)) {
            throw new InvalidDataException("person already exists");
        }

        persons.add(person);
    }

    @Override
    public void update(Person person) throws DataAccessException, InvalidDataException {
        if(!exists(person)) {
            throw new InvalidDataException("person does not exist");
        }

        persons.stream().filter(any -> any.getId() == person.getId()).forEach(
                item -> {
                    item.setFirstName(person.getFirstName());
                    item.setLastName(person.getLastName());
                }
        );
    }

    private boolean exists(Person person) {
        return persons.stream().anyMatch(any -> any.getId() == person.getId());
    }

    // todo : comment
    public static void reset() {
        persons = initialContent.stream().map(Person::new).collect(Collectors.toList());
    }
}
