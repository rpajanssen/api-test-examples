package com.abnamro.examples.dao;

import com.abnamro.examples.dao.exceptions.DataAccessException;
import com.abnamro.examples.domain.api.PersistablePerson;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;

/**
 * This class implements the mock behavior of the person-finder used in the person-resource implementations and required
 * in unit / integration tests. All example tests test the same scenarios, using different test frameworks, and those
 * tests that mock the person-finder need the same mock. Since we have two different resource implementations returning
 * two different person implementations, we have generified this utility class so it will be able to support tests
 * for both types of resources.
 */
public final class PersonFinderMocker {
    private static final String ERROR_MESSAGE_CONSTRUCTOR = "not allowed to instantiate the person-finder-mocker utility class";

    private PersonFinderMocker() {
        throw new UnsupportedOperationException(ERROR_MESSAGE_CONSTRUCTOR);
    }

    public static <T extends PersistablePerson> void mockPersonFinder(
            final PersonDAO<T> personDAO, final Constructor<T> constructor
    ) throws DataAccessException {
        Mockito.when(personDAO.findAll()).thenAnswer((Answer<List<T>>) invocation -> Arrays.asList(
                constructor.newInstance(1L, "Roger", "Janssen"),
                constructor.newInstance(2L, "Pietje", "Puk"),
                constructor.newInstance(3L, "Jan", "Pietersen")
        ));

        Mockito.when(personDAO.findById(eq(1L))).thenAnswer(
                (Answer<T>) invocation -> constructor.newInstance(1L, "Roger", "Janssen")
        );

        Mockito.when(personDAO.findWithLastName(eq("Janssen"))).thenAnswer(
                (Answer<List<T>>) invocation -> Collections.singletonList(constructor.newInstance(1L, "Roger", "Janssen"))
        );
    }
}
