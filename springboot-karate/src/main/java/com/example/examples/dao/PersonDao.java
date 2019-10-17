package com.example.examples.dao;

import com.example.examples.domain.api.Person;
import com.example.examples.domain.db.PersonEntity;
import com.example.examples.repositories.PersonEntityRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PersonDao {
    private final PersonEntityRepository personRepository;

    public PersonDao(PersonEntityRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> findAll() {
        return Optional.ofNullable(personRepository.findAll()).orElse(new ArrayList<>()).stream()
                .map(Person::new)
                .collect(Collectors.toList());
    }

    public Person save(Person person) {
        return new Person(personRepository.save(new PersonEntity(person)));
    }

    public void delete(Long id) {
        personRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return personRepository.existsById(id);
    }
}
