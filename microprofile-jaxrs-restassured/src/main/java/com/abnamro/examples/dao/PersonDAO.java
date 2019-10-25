package com.abnamro.examples.dao;

import com.abnamro.examples.dao.exceptions.DataAccessException;
import com.abnamro.examples.dao.exceptions.PersonAlreadyExistsException;
import com.abnamro.examples.dao.exceptions.PersonDoesNotExistException;

import java.io.Serializable;
import java.util.List;

public interface PersonDAO<T> extends Serializable {
    List<T> findAll() throws DataAccessException;
    T findById(long id) throws DataAccessException;
    List<T> findWithLastName(String lastName) throws DataAccessException;

    T add(T person) throws DataAccessException, PersonAlreadyExistsException;
    void update(T person) throws DataAccessException, PersonDoesNotExistException;
    void delete(Long id);
}
