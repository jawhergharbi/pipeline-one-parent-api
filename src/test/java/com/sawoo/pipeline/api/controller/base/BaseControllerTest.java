package com.sawoo.pipeline.api.controller.base;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.mock.MockFactory;
import com.sawoo.pipeline.api.model.BaseEntity;
import com.sawoo.pipeline.api.service.base.BaseService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@NoArgsConstructor
@Getter
public abstract class BaseControllerTest<D, M extends BaseEntity, S extends BaseService<D>, F extends MockFactory<D, M>> {

    private MockMvc mockMvc;
    private F mockFactory;
    private String resourceURI;
    private String entityType;
    private S service;

    public BaseControllerTest(F mockFactory, String resourceURI, String entityType, S service, MockMvc mockMvc) {
        this.mockFactory = mockFactory;
        this.resourceURI = resourceURI;
        this.entityType = entityType;
        this.service = service;
        this.mockMvc = mockMvc;
    }

    protected abstract String getExistCheckProperty();
    protected abstract List<String> getResourceFieldsToBeChecked();
    protected abstract Class<D> getDTOClass();

    protected static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.disable(MapperFeature.USE_ANNOTATIONS);
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("GET /api/resource/{id}: resource found - Success")
    protected void getByIdWhenResourceFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        D mockedEntity = getMockFactory().newDTO(COMPONENT_ID);

        // Setup the mock service
        doReturn(mockedEntity).when(service).findById(COMPONENT_ID);

        // Execute the GET request
        MvcResult result = mockMvc.perform(get(getResourceURI() + "/{id}", COMPONENT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(COMPONENT_ID)))
                .andReturn();

        getResourceFieldsToBeChecked().forEach((field) -> {
            try {
                jsonPath("$." + field).exists().match(result);
            } catch (Exception exc) {
                Assertions.fail(String. format("Property [%s] must be part of the response object", field));
            }
        });

        verify(service, atMostOnce()).findById(anyString());
    }

    @Test
    @DisplayName("GET /api/resource/{id}: resource not found - Failure")
    protected void getByIdWhenResourceNotFoundReturnsResourceNoFoundException() throws Exception {
        // Setup the mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ getEntityType(), COMPONENT_ID });

        // setup the mocked service
        doThrow(exception)
                .when(service).findById(COMPONENT_ID);

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}", COMPONENT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath(
                        "$.message",
                        containsString(
                                String.format("GET operation. Component type [%s] and id [%s] was not found",
                                        getEntityType(),
                                        COMPONENT_ID))));
    }

    @Test
    @DisplayName("GET /api/resource: no resources found - Success")
    protected void findAllWhenNoResourcesFoundReturnsSuccess() throws Exception {

        // Setup the mock service
        doReturn(Collections.EMPTY_LIST).when(service).findAll();

        // Execute the GET request
        mockMvc.perform(get(getResourceURI())
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned content
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/resource: resources found - Success")
    protected void findAllWhenResourcesFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        List<String> ids = new ArrayList<>();
        int listSize = 3;
        List<D> entityList = IntStream.range(0, listSize)
                .mapToObj((entity) -> {
                    String COMPONENT_ID = getMockFactory().getComponentId();
                    ids.add(COMPONENT_ID);
                    return getMockFactory().newDTO(COMPONENT_ID);
                }).collect(Collectors.toList());

        // Setup the mock service
        doReturn(entityList).when(service).findAll();

        // Execute the GET request
        mockMvc.perform(get(getResourceURI())
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(listSize)))
                .andExpect(jsonPath("$[0].id", is(ids.get(0))));
    }

    @Test
    @DisplayName("DELETE /api/resource/{id}: resource not found - Failure")
    protected void deleteWhenResourceNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();

        // setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_DELETE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{getEntityType(), COMPONENT_ID});

        // setup the mocked helper
        doThrow(exception)
                .when(service).delete(anyString());

        // Execute the DELETE request
        mockMvc.perform(delete(getResourceURI() + "/{id}", COMPONENT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath(
                        "$.message",
                        containsString(
                                String.format("DELETE operation. Component type [%s] and id [%s] was not found",
                                        getEntityType(),
                                        COMPONENT_ID))));

        // Verify behavior
        verify(service, times(1)).delete(anyString());
    }

    @Test
    @DisplayName("DELETE /api/resource/{id}: delete resource found - Success")
    protected void deleteWhenResourceFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        D mockedEntity = getMockFactory().newDTO(COMPONENT_ID);

        // Setup the mock service
        doReturn(mockedEntity).when(service).delete(anyString());

        // Execute the GET request
        MvcResult result = mockMvc.perform(delete(getResourceURI() + "/{id}", COMPONENT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(COMPONENT_ID))).andReturn();

        getResourceFieldsToBeChecked().forEach((field) -> {
            try {
                jsonPath("$." + field).exists().match(result);
            } catch (Exception exc) {
                Assertions.fail(String. format("Property [%s] must be part of the response object", field));
            }
        });

        verify(service, atMostOnce()).delete(anyString());
    }

    @Test
    @DisplayName("POST /api/resource: resource already exists - Failure")
    protected void createWhenResourceAlreadyExistsReturnsFailure() throws Exception {
        // Setup the mocked entities
        D postEntity = getMockFactory().newDTO(null);

        CommonServiceException exception = new CommonServiceException(
                ExceptionMessageConstants.COMMON_CREATE_ENTITY_ALREADY_EXISTS_EXCEPTION,
                new String[]{ getEntityType(), getExistCheckProperty() });

        // setup the mocked service
        doThrow(exception)
                .when(service).create(ArgumentMatchers.any(getDTOClass()));

        // Execute the POST request
        mockMvc.perform(post(resourceURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath(
                        "$.message",
                        containsString(
                                String.format("Entity type [%s] with key [%s] already exits in the system",
                                        getEntityType(),
                                        getExistCheckProperty()))));
    }

    @Test
    @DisplayName("POST /api/resource: resource create - Success")
    protected void createWhenResourceCreateReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        D postEntity = getMockFactory().newDTO(null);
        D mockedEntity = getMockFactory().newDTO(COMPONENT_ID, postEntity);

        // setup the mocked service
        doReturn(mockedEntity).when(service).create(ArgumentMatchers.any(getDTOClass()));

        // Execute the POST request
        mockMvc.perform(post(getResourceURI())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + COMPONENT_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(COMPONENT_ID)))
                .andExpect(jsonPath("$." + getExistCheckProperty()).exists());
    }

    @Test
    @DisplayName("PUT /api/resource/{id}: resource not found - Failure")
    protected void updateWhenResourceNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        D postEntity = getMockFactory().newDTO(COMPONENT_ID);

        // setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_UPDATE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ getEntityType(), COMPONENT_ID });

        // setup the mocked helper
        doThrow(exception)
                .when(service).update(COMPONENT_ID, postEntity);

        // Execute the POST request
        mockMvc.perform(put(getResourceURI() + "/{id}", COMPONENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath(
                        "$.message",
                        containsString(
                                String.format("Component type [%s] and id [%s] was not found",
                                        getEntityType(),
                                        COMPONENT_ID))));
    }
}
