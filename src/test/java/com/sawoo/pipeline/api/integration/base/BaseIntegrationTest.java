package com.sawoo.pipeline.api.integration.base;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sawoo.pipeline.api.integration.ExpectedResult;
import lombok.Getter;
import org.junit.jupiter.api.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Getter
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIntegrationTest<M> {

    private final MockMvc mockMvc;
    private final String resourceURI;
    private final String entityType;
    private final MongoTemplate mongoTemplate;
    private final String expectedResultsFileName;

    private List<ExpectedResult<M>> expectedResults;


    public BaseIntegrationTest(MockMvc mockMvc, MongoTemplate mongoTemplate, String resourceURI, String entityType, String expectedResultsFileName) {
        this.mockMvc = mockMvc;
        this.mongoTemplate = mongoTemplate;
        this.resourceURI = resourceURI;
        this.entityType = entityType;
        this.expectedResultsFileName = expectedResultsFileName;
    }

    protected abstract Class<M> getClazz();

    @BeforeAll
    protected void beforeAll() throws Exception {
        if (expectedResultsFileName != null) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            // Test data file
            File expectedResultsFile = Paths.get("src", "test", "resources", "test-data", "integration", expectedResultsFileName).toFile();

            JavaType type = mapper.getTypeFactory().constructParametricType(ExpectedResult.class, getClazz());
            CollectionType listType = mapper.getTypeFactory().constructCollectionType(List.class, type);

            // Deserialize our JSON file to an array of reviews
            expectedResults = mapper.readValue(expectedResultsFile, listType);
        }
    }

    protected List<M> getExpectedResults(@NotEmpty String key) {
        if (expectedResults != null) {
            return getResult(key)
                    .map(ExpectedResult::getValues)
                    .orElse(Collections.emptyList());

        }
        return Collections.emptyList();
    }

    protected Optional<M> getExpectedResult(@NotEmpty String key) {
        if (expectedResults != null) {
            return getResult(key).map(r -> r.getValues().get(0));
        }
        return Optional.empty();
    }

    protected List<String> getResourceFieldsToBeChecked(@NotEmpty String key) {
        if (expectedResults != null) {
            return getResult(key)
                    .map(ExpectedResult::getFieldsToBeChecked)
                    .orElse(Collections.emptyList());
        }
        return Collections.emptyList();
    }

    @Test
    @DisplayName("POST /api/entities/{id}: find by id when entity found - Success")
    void findByIdWhenEntityFoundReturnsSuccess() throws Exception {
        // Set up
        String EXPECTED_RESULTS_KEY = "findByIdWhenEntityFound";
        String COMPONENT_ID = "1";
        Optional<M> expectedResults = getExpectedResult(EXPECTED_RESULTS_KEY);
        Assertions.assertTrue(
                expectedResults.isPresent(),
                String.format("Expected results with key [%s] must be in file [%s]",
                        EXPECTED_RESULTS_KEY,
                        expectedResultsFileName));
        M result = expectedResults.get();
        // Execute the GET request
        MvcResult mvcResult = mockMvc.perform(get(getResourceURI() + "/{id}", COMPONENT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        getResourceFieldsToBeChecked(EXPECTED_RESULTS_KEY).forEach((field) -> {
            try {
                jsonPath("$." + field).exists().match(mvcResult);
                Field classField = getField(getClazz(), field);
                classField.setAccessible(true);
                Object fieldValue = classField.get(result);
                jsonPath("$." + field).value(fieldValue.toString()).match(mvcResult);
            } catch (Exception exc) {
                Assertions.fail(String. format("Property [%s] must be part of the response object", field));
            }
        });
    }

    private Field getField(Class clazz, String fieldName) {
        List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
        return fields
                .stream()
                .filter(f -> fieldName.equals(f.getName()))
                .findAny()
                .orElseGet(() -> getField(clazz.getSuperclass(), fieldName));
    }

    private Optional<ExpectedResult<M>> getResult(String key) {
        return expectedResults.stream().filter(r -> r.getKey().equals(key)).findFirst();
    }
}
