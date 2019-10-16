package com.example.examples.repositories;

import com.example.examples.AvailableProfiles;
import com.example.examples.domain.db.PersonEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This wired unit test demonstrates the use of DataJpaTest to slice a test. With this annotation only the JPA related
 * beans will be wired, and not the whole application. This results is a faster test and it can result in less
 * test configuration to be taken care of.
 *
 * There are drawbacks as well. One is that the DataJpaTest annotation does not play nice with DBRider, it does not
 * seem to execute the data-set setup logic for each test. In the PersonEntityRepositorySlicedTest test we used the
 * repository under test to manage the setup/teardown of our database in between tests. In this test we will use JDBC
 * to implement the setup/teardown. We let Spring inject an instance of the JDBC template and then we can use
 * plain JDBC queries manage the database state required for each test. This has the advantage that we have complete
 * control again over all our data, including the ID's since we the circumvent JPA/Hibernate. This allows us to have the
 * clean tests again like we had in PersonEntityRepositoryFullyWiredTest, using fluent assertions replacing the DBRider
 * dataset functionality!
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
@ActiveProfiles(AvailableProfiles.LOCAL)
class PersonEntityRepositoryUsingJdbcSlicedTest {
    private static final String QUERY_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS PERSONS (ID BIGINT NOT NULL, FIRST_NAME VARCHAR(25) NOT NULL, LAST_NAME VARCHAR(50) NOT NULL)";
    private static final String QUERY_INSERT = "INSERT INTO PERSONS VALUES (%d, '%s', '%s')";
    private static final String QUERY_DROP_TABLE = "DROP TABLE PERSONS IF EXISTS";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PersonEntityRepository underTest;

    /**
     * Since we cannot use DBRider we need to initialize the DB in a before-method before each test.
     */
    @BeforeEach
    void setup() {
        initDatabase();
    }

    @Transactional
    void initDatabase()  {
        jdbcTemplate.update(QUERY_CREATE_TABLE);

        jdbcTemplate.update(String.format(QUERY_INSERT, 1L, "Jan", "Janssen"));
        jdbcTemplate.update(String.format(QUERY_INSERT, 2L, "Pieter", "Pietersen"));
        jdbcTemplate.update(String.format(QUERY_INSERT, 3L, "Erik", "Eriksen"));

    }

    /**
     * Since we cannot use DBRider we need to explicitly clean the DB ourselves after each test.
     */
    @AfterEach
    void teardown() {
        destroyDatabase();
    }

    @Transactional
    void destroyDatabase() {
        jdbcTemplate.update(QUERY_DROP_TABLE);
    }

    @Test
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
    void shouldAddAPerson() {
        assertThat(underTest).isNotNull();

        underTest.save(new PersonEntity(null, "Katy", "Perry"));

        List<PersonEntity> result = underTest.findAll();

        assertThat(result)
                .hasSize(4)
                .contains(
                        new PersonEntity(1L, "Jan", "Janssen"),
                        new PersonEntity(2L, "Pieter", "Pietersen"),
                        new PersonEntity(3L, "Erik", "Eriksen"),
                        new PersonEntity(result.get(3).getId(), "Katy", "Perry")
                );
    }

    @Test
    void shouldUpdateAPerson() {
        assertThat(underTest).isNotNull();

        underTest.save(new PersonEntity(3L, "Erik", "Erikson"));

        assertThat(underTest.findAll())
                .hasSize(3)
                .contains(
                        new PersonEntity(1L, "Jan", "Janssen"),
                        new PersonEntity(2L, "Pieter", "Pietersen"),
                        new PersonEntity(3L, "Erik", "Erikson")
                );
    }

    @Test
    void shouldDeleteAPerson() {
        assertThat(underTest).isNotNull();

        underTest.deleteById(2L);

        assertThat(underTest.findAll())
                .hasSize(2)
                .contains(
                        new PersonEntity(1L, "Jan", "Janssen"),
                        new PersonEntity(3L, "Erik", "Eriksen")
                );
    }
}
