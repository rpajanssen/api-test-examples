package com.abnamro.examples.dao;

import com.abnamro.examples.dao.exceptions.DataAccessException;
import com.abnamro.examples.domain.api.PersistablePerson;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of a person-finder that has no runtime dependencies and that we can control for test purposes.
 */
@SuppressWarnings("unchecked")
public class HardCodedPersonDAO implements PersonDAO<PersistablePerson> {

    private List<PersistablePerson> persons = Arrays.asList(
            new PersistablePerson(1L, "Roger", "Janssen"),
            new PersistablePerson(2L, "Pietje", "Puk"),
            new PersistablePerson(3L, "Jan", "Pietersen"));

    @Override
    public List<PersistablePerson> findAll() {
        return persons;
    }

    @Override
    public PersistablePerson findById(long id) {
        Optional<PersistablePerson> optional = persons.stream().filter(person -> person.getId() == id).findFirst();

        return optional.orElse(null);

    }

    @Override
    public List<PersistablePerson> findWithLastName(String lastName) {
        return persons.stream().filter(person -> person.getLastName().equals(lastName)).collect(Collectors.toList());
    }

    @Override
    public void add(PersistablePerson person) throws DataAccessException {
        persons.add(person);
    }
}
