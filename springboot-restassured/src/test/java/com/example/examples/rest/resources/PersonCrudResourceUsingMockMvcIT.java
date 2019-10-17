package com.example.examples.rest.resources;

import com.example.examples.dao.PersonDAO;
import com.example.examples.dao.exceptions.PersonNotFoundException;
import com.example.examples.domain.api.ErrorResponse;
import com.example.examples.domain.api.Person;
import com.example.examples.domain.api.SafeList;
import com.example.examples.exceptions.ErrorCodes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This integration test demonstrates several options:
 * - how to slice a test,
 * - how to mock a dependency in your resource under test,
 * - how to test get/post/put/delete rest resources,
 * - how to process generic response types,
 * - how to use json-path for verifications,
 * - how to use fluent asserts instead of json-path,
 *
 * You can slice a test to have a more lightweight integration test. Slicing implies that you will only wire a specific
 * part of the application and not the whole application. This will result in faster startup/shutdown times of the
 * test. In this example we use WebMvcTest to slice the test. This annotation result in only wiring the MVC components
 * like resources and error-handlers (controller-advice annotated classes) and so on. The DAO the resource under
 * test depends on is not an MVC class so it will not be instantiated and thus not be injected in the resource under
 * test. Therefore we need to mock it.
 *
 * Mocking is realised by using the annotation AutoConfigureMockMvc. This will take care of injecting the mocks in the
 * resource under test and provides a fluent api for calling the mocked resource through a MockMvc instance that can be
 * injected into the class. The DAO is mocked using MockBean.
 *
 * If you want to verify the result using the plain json text response you can use json-path. You can define regular
 * expressions and matchers to define your verifications.
 *
 * The get person resource returns a list of persons. We can use a TypeReference (TypeRef in newer rest-assured versions)
 * instance to define the expected result type so that Jackson (the (de)serialization library that is used) knows what
 * to do.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(PersonCrudResource.class)
@AutoConfigureMockMvc
class PersonCrudResourceUsingMockMvcIT {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonDAO personDao;

    @AfterEach
    void cleanup() {
        Mockito.reset(personDao);
    }

    /**
     * This test uses json-path to verify the returned response.
     *
     * The andDo(document(...)) call will result in the generation of a stub for the resource under test.
     */
    @Test
    void shouldReturnAllPersonsValidatingJsonResponse() throws Exception {
        when(personDao.findAll()).thenReturn(Arrays.asList(testPersons()));

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
    void shouldReturnAllPersonsValidatingDeserializedModel() throws Exception {
        when(personDao.findAll()).thenReturn(Arrays.asList(testPersons()));

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
    void shouldAddAPerson() throws Exception {
        when(personDao.add(any(Person.class))).thenReturn(new Person(1001L, "Katy", "Perry"));

        String body = getObjectMapper().writer().writeValueAsString(new Person(null, "Katy", "Perry"));
        MvcResult mvcResult = mockMvc.perform(post("/api/person").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())

                .andReturn();

        Person person = getObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<Person>() {});

        assertThat(person).isNotNull();
        assertThat(person).isEqualTo(new Person(1001L, "Katy", "Perry"));
    }

    @Test
    void shouldUpdateAPerson() throws Exception {
        when(personDao.existsById(3L)).thenReturn(true);
        when(personDao.add(any(Person.class))).thenReturn(new Person(3L, "Erik", "Erikson"));

        String body = toJson(new Person(3L, "Erik", "Erikson"));
        mockMvc.perform(put("/api/person").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
        ;
    }

    @Test
    void shouldNotUpdateIfPersonDoesNotExist() throws Exception {
        Person person = new Person(25L, "Johnie", "Hacker");
        doThrow(new PersonNotFoundException(person, "person does not exist")).when(personDao).update(eq(person));

        String body = toJson(person);
        mockMvc.perform(put("/api/person").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound())
        ;
    }

    @Test
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
    void shouldDeleteAPerson() throws Exception {
        mockMvc.perform(delete("/api/person/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
        ;

        verify(personDao, times(1)).delete(eq(2L));
    }

    private <T> String toJson(T source) throws JsonProcessingException {
        return getObjectMapper().writer().writeValueAsString(source);
    }

    private ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
}
