package com.example.examples.rest.resources;

import com.example.examples.dao.PersonDAO;
import com.example.examples.dao.exceptions.PersonAlreadyExistsException;
import com.example.examples.dao.exceptions.PersonNotFoundException;
import com.example.examples.domain.api.Person;
import com.example.examples.domain.api.SafeList;
import com.example.examples.rest.exceptionhandlers.PersonResourceExceptionHandling;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * This rest resource is bound to a specific error handler by the PersonResourceExceptionHandling-annotation.
 *
 * The SpringBinder will trigger the bean validation activated by the @Valid annotations. A possible validation
 * failure will be handled by the designated resource exception handler.
 *
 * Note that the get persons resource (allPersons) does not return a List<Person> instance. It returns a SafeList<Person>.
 * This is because if you return a json like "[...]" - a top level json array - then you are vulnerable to a CSRF attack!
 * So it is best to always wrap a List / Array in a wrapper object, which is exactly what our SafeList implementation
 * does.
 */
@RestController
@RequestMapping("/api/person")
@PersonResourceExceptionHandling
public class PersonCrudResource {
    private final PersonDAO<Person> personDao;

    public PersonCrudResource(PersonDAO<Person> personDao) {
        this.personDao = personDao;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public SafeList<Person> allPersons() {
        return new SafeList<>(personDao.findAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Person addPerson(@Valid @RequestBody Person person) throws PersonAlreadyExistsException {
        return personDao.add(person);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void updatePerson(@Valid @RequestBody Person person) throws PersonNotFoundException {
        personDao.update(person);
    }

    @DeleteMapping(path="/{personId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePerson(@PathVariable("personId") long personId) {
        personDao.delete(personId);
    }
}
