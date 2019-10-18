package com.example.examples.rest.resources;

import com.example.examples.AvailableProfiles;
import com.example.examples.domain.api.ErrorResponse;
import com.example.examples.domain.api.Person;
import com.example.examples.domain.api.SafeList;
import com.example.examples.exceptions.ErrorCodes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This integration test demonstrates several options is more a less a demo on how to use MockMVC but fully wiring
 * your app.
 *
 * We then use MockMVC as a replacement for RestAssured to call the REST API and verify the results. So... if you
 * have a Spring Boot application and you have written an automated REST API test... well that is great! But if you
 * used RestAssured for it... why have you? What is the added value to you of RestAssured over MockMVC?
 *
 * For more info on MockMVC or DBRider... see the other examples.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DBRider
@ActiveProfiles(AvailableProfiles.LOCAL)
class PersonCrudResourceUsingMockMvcWithoutMockingAnythingIT {
    @Autowired
    private MockMvc mockMvc;

    /**
     * This test uses json-path to verify the returned response.
     *
     * The andDo(document(...)) call will result in the generation of a stub for the resource under test.
     */
    @Test
    @DataSet(value = "persons.yml", cleanBefore = true, cleanAfter = true, strategy = SeedStrategy.CLEAN_INSERT)
    void shouldReturnAllPersonsValidatingJsonResponse() throws Exception {
        mockMvc.perform(get("/api/person")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].firstName", is("Jan")))
                .andExpect(jsonPath("$.items[0].lastName", is("Janssen")))
                .andExpect(jsonPath("$.items[1].firstName", is("Pieter")))
                .andExpect(jsonPath("$.items[1].lastName", is("Pietersen")))
                .andExpect(jsonPath("$.items[2].firstName", is("Erik")))
                .andExpect(jsonPath("$.items[2].lastName", is("Eriksen")))
                ;
    }

    /**
     * This test uses a bean model to verify the response. It demonstrates the use of type-references to enable
     * processing generic responses.
     */
    @Test
    @DataSet(value = "persons.yml", cleanBefore = true, cleanAfter = true, strategy = SeedStrategy.CLEAN_INSERT)
    void shouldReturnAllPersonsValidatingDeserializedModel() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/person"))
                .andExpect(status().isOk())
                .andReturn();

        // note: we use a TypeReference to inform Jackson about the List's generic type
        SafeList<Person> persons = getObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<SafeList<Person>>() {});

        assertThat(persons.getItems()).isNotNull();
        assertThat(persons.getItems())
                .hasSize(3)
                .contains(testPersons());
    }

    private Person[] testPersons() {
        return new Person[] {
                new Person(1L, "Jan", "Janssen"),
                new Person(2L, "Pieter", "Pietersen"),
                new Person(3L, "Erik", "Eriksen")
        };
    }

    @Test
    @DataSet(value = "persons.yml", cleanBefore = true, cleanAfter = true, strategy = SeedStrategy.CLEAN_INSERT)
    @ExpectedDataSet("expected_persons_after_insert.yml")
    void shouldAddAPerson() throws Exception {
        String body = getObjectMapper().writer().writeValueAsString(new Person(null, "Katy", "Perry"));
        MvcResult mvcResult = mockMvc.perform(post("/api/person").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())

                .andReturn();

        Person person = getObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<Person>() {});

        assertThat(person).isNotNull();
        assertThat(person.getId()).isNotNull();
        assertThat(person.getFirstName()).isEqualTo("Katy");
        assertThat(person.getLastName()).isEqualTo("Perry");
    }

    @Test
    @DataSet(value = "persons.yml", cleanBefore = true, cleanAfter = true, strategy = SeedStrategy.CLEAN_INSERT)
    @ExpectedDataSet("expected_persons_after_update.yml")
    void shouldUpdateAPerson() throws Exception {
        String body = toJson(new Person(3L, "Erik", "Erikson"));
        mockMvc.perform(put("/api/person").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
        ;
    }

    @Test
    @DataSet(value = "persons.yml", cleanBefore = true, cleanAfter = true, strategy = SeedStrategy.CLEAN_INSERT)
    @ExpectedDataSet("persons.yml")
    void shouldNotUpdateIfPersonDoesNotExist() throws Exception {
        Person person = new Person(25L, "Johnie", "Hacker");
        String body = toJson(person);
        mockMvc.perform(put("/api/person").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @DataSet(value = "persons.yml", cleanBefore = true, cleanAfter = true, strategy = SeedStrategy.CLEAN_INSERT)
    @ExpectedDataSet("persons.yml")
    void shouldNotUpdateIfPersonIsInvalid() throws Exception {
        String body = toJson(new Person(3L, null, "Erikson"));
        MvcResult mvcResult = mockMvc.perform(put("/api/person").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse<Person> errorResponse = getObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<ErrorResponse<Person>>() {});

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getCode()).isEqualTo(ErrorCodes.PERSON_INVALID.getCode());
        assertThat(errorResponse.getData()).isEqualTo(new Person(3L, null, "Erikson"));
    }

    @Test
    @DataSet(value = "persons.yml", cleanBefore = true, cleanAfter = true, strategy = SeedStrategy.CLEAN_INSERT)
    @ExpectedDataSet("expected_persons_after_delete.yml")
    void shouldDeleteAPerson() throws Exception {
        mockMvc.perform(delete("/api/person/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
        ;
    }

    private <T> String toJson(T source) throws JsonProcessingException {
        return getObjectMapper().writer().writeValueAsString(source);
    }

    private ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
}
