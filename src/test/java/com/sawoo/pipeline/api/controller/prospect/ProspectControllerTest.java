package com.sawoo.pipeline.api.controller.prospect;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerTest;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.dto.person.PersonDTO;
import com.sawoo.pipeline.api.mock.ProspectMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.common.Status;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.model.prospect.ProspectQualification;
import com.sawoo.pipeline.api.service.prospect.ProspectService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
class ProspectControllerTest extends BaseControllerTest<ProspectDTO, Prospect, ProspectService, ProspectMockFactory> {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProspectService service;

    @Autowired
    public ProspectControllerTest(ProspectMockFactory mockFactory, ProspectService service, MockMvc mockMvc) {
        super(mockFactory,
                ControllerConstants.PROSPECT_CONTROLLER_API_BASE_URI,
                DBConstants.PROSPECT_DOCUMENT,
                service,
                mockMvc);
    }

    @Override
    protected String getExistCheckProperty() {
        return "id";
    }

    @Override
    protected List<String> getResourceFieldsToBeChecked() {
        return Arrays.asList("linkedInThread", "updated", "created");
    }

    @Override
    protected Class<ProspectDTO> getDTOClass() {
        return ProspectDTO.class;
    }

    @Test
    @DisplayName("POST /api/prospects: person not informed - Failure")
    void createWhenPersonNotInformedReturnsFailure() throws Exception {
        // Set up mock entities
        ProspectDTO postEntity = getMockFactory().newDTO(null);
        postEntity.setPerson(null);

        // Execute the POST request
        mockMvc.perform(post(getResourceURI())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(2)))
                .andExpect(jsonPath("$.messages",
                        hasItem(containsString("Field [person] must include either the [id] field or all the other fields to create a new person"))));
    }

    @Test
    @DisplayName("POST /api/prospects: person informed but only person id - Failure")
    void createWhenPersonNotProperlyInformedReturnsFailure() throws Exception {
        // Set up mock entities
        ProspectDTO postEntity = getMockFactory().newDTO(null);
        postEntity.getPerson().setFirstName(null);

        // Execute the POST request
        mockMvc.perform(post(getResourceURI())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages",
                        hasItem(containsString("Field [person] must include either the [id] field or all the other fields to create a new person"))));
    }

    @Test
    @DisplayName("POST /api/prospects: only person id informed for person entity - Success")
    void createWhenPersonIdInformedReturnsSuccess() throws Exception {
        ProspectDTO postEntity = getMockFactory().newDTO(null);
        postEntity.setPerson(PersonDTO.builder().id(getMockFactory().getComponentId()).build());
        String PERSON_ID = getMockFactory().getComponentId();
        ProspectDTO mockedEntity = getMockFactory().newDTO(PERSON_ID, postEntity);

        // setup the mocked service
        doReturn(mockedEntity).when(service).create(any(getDTOClass()));

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
                .andExpect(jsonPath("$." + getExistCheckProperty()).exists());
    }

    @Test
    @DisplayName("POST /api/prospects/{type}: qualification not informed - Success")
    void createWhenProspectQualificationNotInformedReturnsSuccess() throws Exception {
        ProspectDTO postEntity = getMockFactory().newDTO(null);
        postEntity.setPerson(PersonDTO.builder().id(getMockFactory().getComponentId()).build());
        postEntity.setQualification(Status.builder().value(ProspectQualification.TARGETABLE.getValue()).build());
        String PERSON_ID = getMockFactory().getComponentId();
        ProspectDTO mockedEntity = getMockFactory().newDTO(PERSON_ID, postEntity);

        // setup the mocked service
        doReturn(mockedEntity).when(service).create(ArgumentMatchers.any(getDTOClass()));

        // Execute the POST request
        mockMvc.perform(post(getResourceURI() + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + PERSON_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(PERSON_ID)))
                .andExpect(jsonPath("$.qualification").exists())
                .andExpect(jsonPath("$.qualification.value").exists())
                .andExpect(jsonPath("$.qualification.value", is(ProspectQualification.TARGETABLE.getValue())));
    }

    @Test
    @DisplayName("PUT /api/prospects/{id}: resource exists - Success")
    void updateWhenResourceFoundAndUpdatedReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        ProspectDTO postEntity = new ProspectDTO();
        String PROSPECT_LINKEDIN_THREAD = getMockFactory().getFAKER().company().url();
        postEntity.setLinkedInThread(PROSPECT_LINKEDIN_THREAD);
        ProspectDTO mockedEntity = getMockFactory().newDTO(PROSPECT_ID);
        mockedEntity.setLinkedInThread(PROSPECT_LINKEDIN_THREAD);

        // setup the mocked service
        doReturn(mockedEntity).when(service).update(anyString(), ArgumentMatchers.any(ProspectDTO.class));

        // Execute the PUT request
        mockMvc.perform(put(getResourceURI() + "/{id}", PROSPECT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + PROSPECT_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(PROSPECT_ID)))
                .andExpect(jsonPath("$.linkedInThread", is(PROSPECT_LINKEDIN_THREAD)));
    }

    @Test
    @DisplayName("DELETE /api/prospects/{id}/summary: resource exists - Success")
    void deleteProspectSummaryWhenResourceFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        ProspectDTO mockedEntity = getMockFactory().newDTO(PROSPECT_ID);
        mockedEntity.setProspectNotes(null);

        // setup the mocked service
        doReturn(mockedEntity).when(service).deleteProspectSummary(anyString());

        // Execute the DELETE request
        mockMvc.perform(delete(getResourceURI() + "/{id}/summary", PROSPECT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(PROSPECT_ID)))
                .andExpect(jsonPath("$.prospectNotes").doesNotExist());
    }

    @Test
    @DisplayName("DELETE /api/prospects/{id}/summary: resource does not exists - Failure")
    void deleteProspectSummaryWhenResourceNotFoundReturnsFailure() throws Exception {
        // Setup the mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();

        // setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ DBConstants.PROSPECT_DOCUMENT, PROSPECT_ID });
        doThrow(exception).when(service).deleteProspectSummary(anyString());

        // Execute the DELETE request
        mockMvc.perform(delete(getResourceURI() + "/{id}/summary", PROSPECT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", getEntityType()),
                        PROSPECT_ID)));
    }

    @Test
    @DisplayName("DELETE /api/prospects/{id}/company-summary: resource exists - Success")
    void deleteProspectCompanyCommentsWhenResourceFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        ProspectDTO mockedEntity = getMockFactory().newDTO(PROSPECT_ID);
        mockedEntity.setCompanyNotes(null);

        // setup the mocked service
        doReturn(mockedEntity).when(service).deleteProspectCompanyComments(anyString());

        // Execute the DELETE request
        mockMvc.perform(delete(getResourceURI() + "/{id}/company-summary", PROSPECT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(PROSPECT_ID)))
                .andExpect(jsonPath("$.companyNotes").doesNotExist());
    }

    @Test
    @DisplayName("DELETE /api/prospects/{id}/qualification-notes: resource exists - Success")
    void deleteProspectQualificationCommentsWhenResourceFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        ProspectDTO mockedEntity = getMockFactory().newDTO(PROSPECT_ID);
        mockedEntity.getQualification().setNotes(null);

        // setup the mocked service
        doReturn(mockedEntity).when(service).deleteProspectQualificationComments(anyString());

        // Execute the DELETE request
        mockMvc.perform(delete(getResourceURI() + "/{id}/qualification-notes", PROSPECT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(PROSPECT_ID)))
                .andExpect(jsonPath("$.status.notes").doesNotExist());
    }
}
