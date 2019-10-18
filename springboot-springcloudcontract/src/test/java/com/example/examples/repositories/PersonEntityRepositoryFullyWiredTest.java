package com.example.examples.repositories;

import com.example.examples.AvailableProfiles;
import com.example.examples.domain.db.PersonEntity;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This wired unit test demonstrates the use of DBRider/DBUnit:
 * - to define initial data sets
 * - to verify changes are actually made to a database
 *
 * Note that to enable writing a unit test that adds a new record to a predefined data set we need to ensure that we
 * do not get uniqueness constraint violation on the generated ID of the domain object being persisted. We have solved
 * this by reserving the first 1000 numbers for ourselves and configured the ID generator to start at 1001. See the
 * Person domain class for more information.
 *
 * Note that with the data set annotations of DBRider we can write really clean unit tests and have automated assertions
 * against a predefined expected data set.
 *
 * Note how we use a regular expression in the expected result set after an insert, because we cannot know for sure the
 * generated id!
 *
 * Note that normally you would not write a test like this for a plain crud/jpa repository because then you are
 * only testing the Spring repository implementations. Only if you added your own query methods to your repository
 * it would make sense to test these.
 *
 * Note that we do not slice the test but instead use the @SpringBootTest annotation, resulting in a fully wired app.
 * This requires us to provide all the required test configurations for all beans that will be instantiated, therefore
 * we specify a specific property file to be used for this test using the TestPropertySource annotation that contains
 * all the required property settings.
 *
 * Note that the bigger the app, the slower the startup of the test will be! Slicing will then be very interesting (see
 * the other sliced tests)!
 */
@ExtendWith(SpringExtension.class)
@DBRider
@SpringBootTest
@ActiveProfiles(AvailableProfiles.LOCAL)
class PersonEntityRepositoryFullyWiredTest {

    @Autowired
    private PersonEntityRepository underTest;

    @Test
    @DataSet(value = "persons.yml", cleanBefore = true, cleanAfter = true, strategy = SeedStrategy.CLEAN_INSERT)
    void shouldListAllPersons() {
        assertThat(underTest).isNotNull();
        assertThat(underTest.findAll())
                .hasSize(3)
                .contains(
                        new PersonEntity(1L, "Jan", "Janssen"),
                        new PersonEntity(2L, "Pieter", "Pietersen"),
                        new PersonEntity(3L, "Erik", "Eriksen")
                );
    }

    @Test
    @DataSet(value = "persons.yml", cleanBefore = true, cleanAfter = true, strategy = SeedStrategy.CLEAN_INSERT)
    @ExpectedDataSet("expected_persons_after_insert.yml")
    void shouldAddAPerson() {
        assertThat(underTest).isNotNull();

        underTest.save(new PersonEntity(null, "Katy", "Perry"));
    }

    @Test
    @DataSet(value = "persons.yml", cleanBefore = true, cleanAfter = true, strategy = SeedStrategy.CLEAN_INSERT)
    @ExpectedDataSet("expected_persons_after_update.yml")
    void shouldUpdateAPerson() {
        assertThat(underTest).isNotNull();

        underTest.save(new PersonEntity(3L, "Erik", "Erikson"));
    }

    @Test
    @DataSet(value = "persons.yml", cleanBefore = true, cleanAfter = true, strategy = SeedStrategy.CLEAN_INSERT)
    @ExpectedDataSet("expected_persons_after_delete.yml")
    void shouldDeleteAPerson() {
        assertThat(underTest).isNotNull();

        underTest.deleteById(2L);
    }
}
