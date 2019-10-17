package com.example.examples.dao;

import com.example.examples.dao.exceptions.PersonAlreadyExistsException;
import com.example.examples.dao.exceptions.PersonNotFoundException;
import com.example.examples.domain.api.Person;
import com.example.examples.domain.db.PersonEntity;
import com.example.examples.repositories.PersonEntityRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DBPersonDAO implements PersonDAO<Person>{
    private final PersonEntityRepository personRepository;

    public DBPersonDAO(PersonEntityRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public List<Person> findAll() {
        return Optional.ofNullable(personRepository.findAll()).orElse(new ArrayList<>()).stream()
                .map(Person::new)
                .collect(Collectors.toList());
    }

    // todo : cleanup - never return null!
    @Override
    public Person findById(long id) {
        Optional<PersonEntity> optionalPersonEntity = personRepository.findById(id);

        if(optionalPersonEntity.isPresent()) {
            return new Person(optionalPersonEntity.get());
        }

        return null;
    }

    @Override
    public List<Person> findWithLastName(String lastName) {
        return null;
    }

    @Override
    public Person add(Person person) throws PersonAlreadyExistsException {
        if(existsById(person.getId())) {
            throw new PersonAlreadyExistsException(person, "person already exists");
        }
        return new Person(personRepository.save(new PersonEntity(person)));
    }

    @Override
    public void update(Person person) throws PersonNotFoundException {
        if(!existsById(person.getId())) {
            throw new PersonNotFoundException(person, "person does not exit");
        }

        personRepository.save(new PersonEntity(person));
    }

    @Override
    public void delete(Long id) {
        personRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return id != null && personRepository.existsById(id);
    }
}
