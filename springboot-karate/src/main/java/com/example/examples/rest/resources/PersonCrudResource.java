package com.example.examples.rest.resources;

import com.example.examples.dao.PersonDao;
import com.example.examples.domain.api.Person;
import com.example.examples.domain.api.SafeList;
import com.example.examples.exceptions.PersonNotFoundException;
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
    private final PersonDao personDao;

    public PersonCrudResource(PersonDao personDao) {
        this.personDao = personDao;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public SafeList<Person> allPersons() {
        return new SafeList<>(personDao.findAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Person addPerson(@Valid @RequestBody Person person) {
        return personDao.save(person);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Person updatePerson(@Valid @RequestBody Person person) throws PersonNotFoundException {
        if(!personDao.existsById(person.getId())) {
            throw new PersonNotFoundException(person);
        }
        return personDao.save(person);
    }

    @DeleteMapping(path="/{personId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deletePerson(@PathVariable("personId") long personId) {
        personDao.delete(personId);
    }
}
