package com.sawoo.pipeline.api.dummy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@Tag(value = "controller")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
class DummyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DummyService service;

    protected static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("GET /api/common/hello")
    void helloWhenOKReturnsOkAndContainsHereIAmText() throws Exception {
        mockMvc
                .perform(get("/api/common/hello"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(containsString("Hello")));
    }

    @Test
    @DisplayName("GET /api/common/message/dummy.message.test - no locale")
    void messageWhenNoLocaleReturnsMessage() throws Exception {
        mockMvc
                .perform(get("/api/common/message/{1}", "dummy.message.test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(containsString("This is only a dumb message")));
    }

    @Test
    @DisplayName("GET /api/common/message/dummy.message.test - locale ES")
    void messageWhenLocaleEsReturnsMessage() throws Exception {
        mockMvc
                .perform(get("/api/common/message/{1}", "dummy.message.test")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "es")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(containsString("Este es un mensaje muy tonto")));
    }

    @Test
    @DisplayName("GET /api/common/message/dummy.message.test - locale EN")
    void messageWhenLocaleENReturnsMessage() throws Exception {
        mockMvc
                .perform(get("/api/common/message/{1}", "dummy.message.test")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "en")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(containsString("This is only a dumb message")))
                .andExpect(content().string(containsString(" with EN locale defined")));
    }

    @Test
    @DisplayName("GET /api/common/dummies - Add dummy")
    void saveWhenEntityIsCorrectReturnsEntityWithId() throws Exception {
        // setup mock Dummy Entity
        DummyEntity postDummy = new DummyEntity();
        postDummy.setName("my name");
        postDummy.setNumber(13);

        DummyEntity mockDummy = new DummyEntity();
        mockDummy.setName("my name");
        mockDummy.setNumber(13);
        mockDummy.setVersion(1);
        mockDummy.setId("my_dummy_id");

        // setup the mocked service
        doReturn(mockDummy).when(service).save(any());

        // execute the POST request
        mockMvc
                .perform(post("/api/common/dummies/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDummy)))
                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/common/dummies/my_dummy_id"))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is("my_dummy_id")))
                .andExpect(jsonPath("$.name", is("my name")))
                .andExpect(jsonPath("$.number", is(13)))
                .andExpect(jsonPath("$.version", is(1)));
    }

    @Test
    @DisplayName("GET /api/common/dummies/my_dummy_id - Success")
    void getWhenDummyFoundReturnsSuccess() throws Exception {
        // Setup the mocked Dummy entities
        DummyEntity mockDummy = new DummyEntity();
        mockDummy.setVersion(1);
        mockDummy.setId("my_dummy_id");
        mockDummy.setName("hello my dear");
        mockDummy.setNumber(14);

        // Setup the mock service
        doReturn(Optional.of(mockDummy)).when(service).findById("my_dummy_id");

        // Execute the GET request
        mockMvc.perform(get("/api/common/dummies/{id}", "my_dummy_id")
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/common/dummies/my_dummy_id"))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is("my_dummy_id")))
                .andExpect(jsonPath("$.name", is("hello my dear")))
                .andExpect(jsonPath("$.number", is(14)))
                .andExpect(jsonPath("$.version", is(1)));
    }

    @Test
    @DisplayName("GET /api/common/dummies/my_dummy_id - Failure")
    void getWhenDummyNotFoundReturnsFailure() throws Exception {
        // Setup the mock service
        doReturn(Optional.empty()).when(service).findById("my_dummy_id");

        // Execute the Get request
        mockMvc.perform(get("/api/common/dummies/{id}", "my_dummy_id")
                .contentType(MediaType.APPLICATION_JSON))
                // Validate that we get a 404 Not Found Response
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/common/dummies/my_dummy_id - Success")
    void updateWhenDummyFoundReturnsSuccess() throws Exception {
        // Setup the mocked Dummy entities
        DummyEntity putDummy = new DummyEntity();
        putDummy.setName("hello my dear friend");
        putDummy.setNumber(124);
        DummyEntity mockEntity = new DummyEntity("my_dummy_id", "hello my dear", 14, 1, LocalDateTime.now(ZoneOffset.UTC));
        DummyEntity mockUpdatedEntity = new DummyEntity("my_dummy_id", "hello my dear friend", 124, 2, LocalDateTime.now(ZoneOffset.UTC));

        // Setup the mocked service
        doReturn(Optional.of(mockEntity)).when(service).findById("my_dummy_id");
        doReturn(Optional.of(mockUpdatedEntity)).when(service).update(eq("my_dummy_id"), any());

        // Execute the Put request
        mockMvc.perform(put("/api/common/dummies/{id}", "my_dummy_id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(putDummy)))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.ETAG, "\"2\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/common/dummies/my_dummy_id"))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is("my_dummy_id")))
                .andExpect(jsonPath("$.name", is("hello my dear friend")))
                .andExpect(jsonPath("$.number", is(124)))
                .andExpect(jsonPath("$.version", is(2)));
    }

    @Test
    @DisplayName("PUT /api/common/dummies/my_dummy_id - Not found")
    void updateWhenDummyNotFoundReturnsFailure() throws Exception {
        // Setup the mocked Dummy entities
        DummyEntity putDummy = new DummyEntity();
        putDummy.setName("hello my dear friend");
        putDummy.setNumber(124);

        // Setup the mocked service
        doReturn(Optional.empty()).when(service).findById("my_dummy_id_not_found");

        // Execute the Put request
        mockMvc.perform(put("/api/common/dummies/{id}", "my_dummy_id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(putDummy)))

                // Validate the response code and content type
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/common/dummies/my_dummy_id - Success")
    void deleteWhenDummyFoundReturnsSuccess() throws Exception {
        // Setup the mocked Dummy entity
        DummyEntity mockDummy = new DummyEntity();
        mockDummy.setVersion(1);
        mockDummy.setId("my_dummy_id");
        mockDummy.setName("hello my dear");
        mockDummy.setNumber(14);

        // Setup the mock service
        doReturn(Optional.of(mockDummy)).when(service).findById("my_dummy_id");
        doReturn(true).when(service).delete("my_dummy_id");

        // Execute the DELETE request
        mockMvc.perform(delete("/api/common/dummies/{id}", "my_dummy_id"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/common/dummies/my_dummy_id - Failure")
    void deleteWhenDummyNotFoundReturnsFailure() throws Exception {
        // Setup the mock service
        doReturn(Optional.empty()).when(service).findById("my_dummy_id");

        // Execute DELETE request
        mockMvc.perform(delete("/api/common/dummies/{id}", "my_dummy_id"))
                .andExpect(status().isNotFound());
    }
}
