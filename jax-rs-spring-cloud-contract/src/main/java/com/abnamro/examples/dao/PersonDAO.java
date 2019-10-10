package com.abnamro.examples.dao;

import com.abnamro.examples.dao.exceptions.DataAccessException;
import com.abnamro.examples.dao.exceptions.InvalidDataException;

import java.io.Serializable;
import java.util.List;

public interface PersonDAO<T> extends Serializable {
    List<T> findAll() throws DataAccessException;
    T findById(long id) throws DataAccessException;
    List<T> findWithLastName(String lastName) throws DataAccessException;
    
    void add(T person) throws DataAccessException, InvalidDataException;
    void update(T person) throws DataAccessException, InvalidDataException;
}
