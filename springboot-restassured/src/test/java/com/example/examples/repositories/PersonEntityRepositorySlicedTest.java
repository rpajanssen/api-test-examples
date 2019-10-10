package com.example.examples.repositories;

import com.example.examples.AvailableProfiles;
import com.example.examples.domain.db.PersonEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This wired unit test demonstrates the use of DataJpaTest to slice a test. With this annotation only the JPA related
 * beans will be wired, and not the whole application. This results is a faster test and it can result in less
 * test configuration to be taken care of.
 *
 * There are drawbacks as well. One is that the DataJpaTest annotation does not play nice with DBRider, it does not
 * seem to execute the data-set setup logic for each test. And this results in a more complex test setup and less control
 * over the data, so the assertions will be a bit different (see PersonEntityRepositoryFullyWiredTest as well). We
 * even had to add a method to the jpa-repository (findByLastName) to be able to write some of these unit tests! That
 * is definitely not something you want to do!
 *
 * An alternative for the initializations/verifications would be to use plain JDBC for database setup/teardown (see
 * PersonEntityRepositoryUsingJdbcSlicedTest).
 *
 * Note that normally you would not write a test like this for a plain crud/jpa repository because then you are
 * only testing the Spring repository implementations. Only if you added your own query methods to your repository
 * it would make sense to test these.
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
})
@Transactional
@ActiveProfiles(AvailableProfiles.LIVE)
class PersonEntityRepositorySlicedTest {

    @Autowired
    private PersonEntityRepository underTest;

    /**
     * Since we cannot use DBRider we need to initialize the DB in a before-method before each test.
     */
    @BeforeEach
    void setup() {
        underTest.saveAll(Arrays.asList(persons()));
    }

    /**
     * Since we cannot use DBRider we need to explicitly clean the DB ourselves after each test.
     */
    @AfterEach
    void teardown() {
        underTest.findAll().forEach(
                personEntity -> underTest.delete(personEntity)
        );
    }

    /**
     * Note that the assertions are a bit more cumbersome, non-fluent and since we can't control the full DB
     * setup, we do not control the value of the ID's!
     */
    @Test
    void shouldListAllPersons() {
        assertThat(underTest).isNotNull();

        List<PersonEntity> result = underTest.findAll();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getFirstName()).isEqualTo("Jan");
        assertThat(result.get(0).getLastName()).isEqualTo("Janssen");
        assertThat(result.get(1).getFirstName()).isEqualTo("Pieter");
        assertThat(result.get(1).getLastName()).isEqualTo("Pietersen");
        assertThat(result.get(2).getFirstName()).isEqualTo("Erik");
        assertThat(result.get(2).getLastName()).isEqualTo("Eriksen");
    }

    @Test
    void shouldAddAPerson() {
        assertThat(underTest).isNotNull();

        underTest.save(new PersonEntity(null, "Katy", "Perry"));

        List<PersonEntity> result = underTest.findAll();
        assertThat(result).hasSize(4);
        assertThat(result.get(0).getFirstName()).isEqualTo("Jan");
        assertThat(result.get(0).getLastName()).isEqualTo("Janssen");
        assertThat(result.get(1).getFirstName()).isEqualTo("Pieter");
        assertThat(result.get(1).getLastName()).isEqualTo("Pietersen");
        assertThat(result.get(2).getFirstName()).isEqualTo("Erik");
        assertThat(result.get(2).getLastName()).isEqualTo("Eriksen");
        assertThat(result.get(3).getFirstName()).isEqualTo("Katy");
        assertThat(result.get(3).getLastName()).isEqualTo("Perry");
    }

    /**
     * Not that we need to find the person instance in the DB first before we can update it since we have no control
     * and therefore do not know the value of the ID.
     */
    @Test
    void shouldUpdateAPerson() {
        assertThat(underTest).isNotNull();

        List<PersonEntity> persons = underTest.findByLastName("Eriksen");
        PersonEntity person = persons.get(0);
        person.setLastName("Erikson");

        underTest.save(person);

        List<PersonEntity> result = underTest.findAll();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getFirstName()).isEqualTo("Jan");
        assertThat(result.get(0).getLastName()).isEqualTo("Janssen");
        assertThat(result.get(1).getFirstName()).isEqualTo("Pieter");
        assertThat(result.get(1).getLastName()).isEqualTo("Pietersen");
        assertThat(result.get(2).getFirstName()).isEqualTo("Erik");
        assertThat(result.get(2).getLastName()).isEqualTo("Erikson");
    }

    @Test
    void shouldDeleteAPerson() {
        assertThat(underTest).isNotNull();

        List<PersonEntity> persons = underTest.findByLastName("Pietersen");
        PersonEntity person = persons.get(0);
        underTest.deleteById(person.getId());

        List<PersonEntity> result = underTest.findAll();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("Jan");
        assertThat(result.get(0).getLastName()).isEqualTo("Janssen");
        assertThat(result.get(1).getFirstName()).isEqualTo("Erik");
        assertThat(result.get(1).getLastName()).isEqualTo("Eriksen");
    }

    private PersonEntity[] persons() {
        return new PersonEntity[] {
                new PersonEntity(1L, "Jan", "Janssen"),
                new PersonEntity(2L, "Pieter", "Pietersen"),
                new PersonEntity(3L, "Erik", "Eriksen")
        };
    }

}
