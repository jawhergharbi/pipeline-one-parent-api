package com.sawoo.pipeline.api.controller.person;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerTest;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.dto.person.PersonDTO;
import com.sawoo.pipeline.api.mock.PersonMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.person.Person;
import com.sawoo.pipeline.api.service.person.PersonService;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Tag(value = "controller")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class PersonControllerTest extends BaseControllerTest<PersonDTO, Person, PersonService, PersonMockFactory> {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService service;

    @Autowired
    public PersonControllerTest(PersonMockFactory mockFactory, PersonService service, MockMvc mockMvc) {
        super(mockFactory,
                ControllerConstants.PERSON_CONTROLLER_API_BASE_URI,
                DBConstants.PERSON_DOCUMENT,
                service,
                mockMvc);
    }

    @Override
    protected String getExistCheckProperty() {
        return "linkedInUrl";
    }

    @Override
    protected List<String> getResourceFieldsToBeChecked() {
        return Arrays.asList("linkedInUrl", "firstName", "lastName", "created");
    }

    @Override
    protected Class<PersonDTO> getDTOClass() {
        return PersonDTO.class;
    }

    @Test
    @DisplayName("POST /api/persons: firstName not informed - Failure")
    void createWhenFirstNameNotInformedReturnsFailure() throws Exception {
        String PERSON_LAST_NAME = getMockFactory().getFAKER().name().lastName();
        PersonDTO postEntity = getMockFactory().newDTO(null, null, PERSON_LAST_NAME);

        // Execute the POST request
        mockMvc.perform(post(getResourceURI())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    @Test
    @DisplayName("POST /api/persons: company id and company name not informed - Failure")
    void createWhenCompanyIdAndCompanyNameNotInformedReturnsFailure() throws Exception {
        String PERSON_LAST_NAME = getMockFactory().getFAKER().name().lastName();
        String PERSON_FIRST_NAME = getMockFactory().getFAKER().name().firstName();
        PersonDTO postEntity = getMockFactory().newDTO(null, PERSON_FIRST_NAME, PERSON_LAST_NAME);
        postEntity.getCompany().setName(null);

        // Execute the POST request
        mockMvc.perform(post(getResourceURI())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages[0]",
                        containsString("Field [company] must include either the [id] field or both [name and url] fields")));
    }

    @Test
    @DisplayName("POST /api/persons: company id informed but company name and url not informed - Success")
    void createWhenCompanyIdInformedAndCompanyNameAndCompanyUrlNotInformedReturnsSuccess() throws Exception {
        String PERSON_LAST_NAME = getMockFactory().getFAKER().name().lastName();
        String PERSON_FIRST_NAME = getMockFactory().getFAKER().name().firstName();
        PersonDTO postEntity = getMockFactory().newDTO(null, PERSON_FIRST_NAME, PERSON_LAST_NAME);
        postEntity.setCompany(CompanyDTO.builder().id(getMockFactory().getComponentId()).build());

        String PERSON_ID = getMockFactory().getComponentId();
        PersonDTO mockedEntity = getMockFactory().newDTO(PERSON_ID, postEntity);

        // setup the mocked service
        doReturn(mockedEntity).when(service).create(ArgumentMatchers.any(PersonDTO.class));

        // Execute the POST request
        mockMvc.perform(post(getResourceURI())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + PERSON_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(PERSON_ID)))
                .andExpect(jsonPath("$.company").exists());
    }

    @Test
    @DisplayName("PUT /api/persons/{id}: resource exists - Success")
    void updateWhenResourceFoundAndUpdatedReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String PERSON_ID = getMockFactory().getComponentId();
        String PERSON_POSITION = getMockFactory().getFAKER().company().profession();
        PersonDTO postEntity = new PersonDTO();
        postEntity.setPosition(PERSON_POSITION);
        PersonDTO mockedEntity = getMockFactory().newDTO(PERSON_ID);
        mockedEntity.setPosition(PERSON_POSITION);

        // setup the mocked service
        doReturn(mockedEntity).when(service).update(anyString(), ArgumentMatchers.any(PersonDTO.class));

        // Execute the PUT request
        mockMvc.perform(put(getResourceURI() + "/{id}", PERSON_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + PERSON_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(PERSON_ID)))
                .andExpect(jsonPath("$.position", is(PERSON_POSITION)));
    }
}
