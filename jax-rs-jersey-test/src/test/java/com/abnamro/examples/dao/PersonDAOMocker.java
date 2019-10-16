package com.abnamro.examples.dao;

import com.abnamro.examples.dao.exceptions.DataAccessException;
import com.abnamro.examples.dao.exceptions.PersonAlreadyExistsException;
import com.abnamro.examples.domain.api.Person;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;

/**
 * This class implements the mock behavior of the person-finder used in the person-resource implementations and required
 * in unit / integration tests. All example tests test the same scenarios, using different test frameworks, and those
 * tests that mock the person-finder need the same mock. Since we have two different resource implementations returning
 * two different person implementations, we have generified this utility class so it will be able to support tests
 * for both types of resources.
 */
public final class PersonDAOMocker {
    private static final String ERROR_MESSAGE_CONSTRUCTOR = "not allowed to instantiate the person-finder-mocker utility class";

    private PersonDAOMocker() {
        throw new UnsupportedOperationException(ERROR_MESSAGE_CONSTRUCTOR);
    }

    public static PersonDAO<Person> mockPersonDAO() {
        try {
            @SuppressWarnings("unchecked") PersonDAO<Person> personDAO = (PersonDAO<Person>) Mockito.mock(PersonDAO.class);

            Mockito.when(personDAO.findAll()).thenReturn(Arrays.asList(new Person(1L, "Jan", "Janssen"), new Person(2L, "Pieter", "Pietersen"), new Person(3L, "Erik", "Eriksen")));

            Mockito.when(personDAO.findById(eq(1L))).thenReturn(new Person(1L, "Jan", "Janssen"));

            Mockito.when(personDAO.findWithLastName(eq("Janssen"))).thenReturn(Collections.singletonList(new Person(1L, "Jan", "Janssen")));

            Person instance = new Person(5L, "Despicable", "Me");
            Mockito.when(personDAO.add(eq(instance))).thenReturn(instance);

            Person anotherInstance = new Person(5L, "John", "A***e");
            Mockito.when(personDAO.add(eq(anotherInstance))).thenReturn(anotherInstance);

            return personDAO;
        } catch (DataAccessException | PersonAlreadyExistsException e) {
            throw new IllegalStateException("unable to mock person-dao", e);
        }
    }
}
