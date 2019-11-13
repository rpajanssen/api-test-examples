package com.abnamro.examples.dao;

import com.abnamro.examples.dao.exceptions.DataAccessException;
import com.abnamro.examples.dao.exceptions.PersonAlreadyExistsException;
import com.abnamro.examples.dao.exceptions.PersonDoesNotExistException;
import com.abnamro.examples.domain.api.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of a person-finder that has no runtime dependencies and that we can control for test purposes.
 *
 * Note that this is NOT an implementation you would use in production :)... it is just here to support the
 * demo!
 */
@SuppressWarnings("unchecked")
public class HardCodedPersonDAO implements PersonDAO<Person> {

    private static final List<Person> initialContent = Arrays.asList(
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
    public Person add(Person person) throws DataAccessException, PersonAlreadyExistsException {
        if(exists(person)) {
            throw new PersonAlreadyExistsException("person already exists");
        }

        persons.add(person);

        return person;
    }

    @Override
    public void update(Person person) throws DataAccessException, PersonDoesNotExistException {
        if(!exists(person)) {
            throw new PersonDoesNotExistException("person does not exist");
        }

        persons.stream().filter(any -> any.getId() == person.getId()).forEach(
                item -> {
                    item.setFirstName(person.getFirstName());
                    item.setLastName(person.getLastName());
                }
        );
    }

    @Override
    public void delete(Long id) {
        persons.removeIf(person -> person.getId() == id);
    }

    private boolean exists(Person person) {
        return persons.stream().anyMatch(any -> any.getId() == person.getId());
    }

    /**
     * We need to ability to reset the state of the data-set to the initial state for test purposes.
     */
    public static void reset() {
        persons = initialContent.stream().map(Person::new).collect(Collectors.toList());
    }
}
