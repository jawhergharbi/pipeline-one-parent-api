package com.sawoo.pipeline.api.controller.lead;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerTest;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.lead.LeadTypeRequestParam;
import com.sawoo.pipeline.api.dto.person.PersonDTO;
import com.sawoo.pipeline.api.mock.LeadMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.common.Status;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.lead.LeadStatusList;
import com.sawoo.pipeline.api.service.lead.LeadService;
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
@AutoConfigureMockMvc(addFilters = false)
@Tag(value = "controller")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class LeadControllerTest extends BaseControllerTest<LeadDTO, Lead, LeadService, LeadMockFactory> {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeadService service;

    @Autowired
    public LeadControllerTest(LeadMockFactory mockFactory, LeadService service, MockMvc mockMvc) {
        super(mockFactory,
                ControllerConstants.LEAD_CONTROLLER_API_BASE_URI,
                DBConstants.LEAD_DOCUMENT,
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
    protected Class<LeadDTO> getDTOClass() {
        return LeadDTO.class;
    }

    @Test
    @DisplayName("POST /api/leads: person not informed - Failure")
    void createWhenPersonNotInformedReturnsFailure() throws Exception {
        LeadDTO postEntity = getMockFactory().newDTO(null);
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
    @DisplayName("POST /api/leads: person informed but only person id - Failure")
    void createWhenPersonNotProperlyInformedReturnsFailure() throws Exception {
        LeadDTO postEntity = getMockFactory().newDTO(null);
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
    @DisplayName("POST /api/leads: only person id informed for person entity - Success")
    void createWhenPersonIdInformedReturnsSuccess() throws Exception {
        LeadDTO postEntity = getMockFactory().newDTO(null);
        postEntity.setPerson(PersonDTO.builder().id(getMockFactory().getComponentId()).build());
        String PERSON_ID = getMockFactory().getComponentId();
        LeadDTO mockedEntity = getMockFactory().newDTO(PERSON_ID, postEntity);

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
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + PERSON_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(PERSON_ID)))
                .andExpect(jsonPath("$." + getExistCheckProperty()).exists());
    }

    @Test
    @DisplayName("POST /api/leads/{type}: type Lead - Success")
    void createWhenLeadTypeLeadReturnsSuccess() throws Exception {
        LeadDTO postEntity = getMockFactory().newDTO(null);
        postEntity.setPerson(PersonDTO.builder().id(getMockFactory().getComponentId()).build());
        postEntity.setStatus(Status.builder().value(LeadStatusList.HOT.getStatus()).build());
        String PERSON_ID = getMockFactory().getComponentId();
        LeadDTO mockedEntity = getMockFactory().newDTO(PERSON_ID, postEntity);

        // setup the mocked service
        doReturn(mockedEntity).when(service).create(ArgumentMatchers.any(getDTOClass()));

        // Execute the POST request
        mockMvc.perform(post(getResourceURI() + "/{type}", LeadTypeRequestParam.LEAD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + PERSON_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(PERSON_ID)))
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status.value").exists())
                .andExpect(jsonPath("$.status.value", is(LeadStatusList.HOT.getStatus())));
    }

    @Test
    @DisplayName("POST /api/leads/{type}: lead type null - Success")
    void createWhenLeadTypeNotInformedReturnsSuccess() throws Exception {
        LeadDTO postEntity = getMockFactory().newDTO(null);
        postEntity.setPerson(PersonDTO.builder().id(getMockFactory().getComponentId()).build());
        postEntity.setStatus(Status.builder().value(LeadStatusList.FUNNEL_ON_GOING.getStatus()).build());
        String PERSON_ID = getMockFactory().getComponentId();
        LeadDTO mockedEntity = getMockFactory().newDTO(PERSON_ID, postEntity);

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
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status.value").exists())
                .andExpect(jsonPath("$.status.value", is(LeadStatusList.FUNNEL_ON_GOING.getStatus())));
    }

    @Test
    @DisplayName("PUT /api/leads/{id}: resource exists - Success")
    void updateWhenResourceFoundAndUpdatedReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String LEAD_ID = getMockFactory().getComponentId();
        LeadDTO postEntity = new LeadDTO();
        String LEAD_LINKEDIN_THREAD = getMockFactory().getFAKER().company().url();
        postEntity.setLinkedInThread(LEAD_LINKEDIN_THREAD);
        LeadDTO mockedEntity = getMockFactory().newDTO(LEAD_ID);
        mockedEntity.setLinkedInThread(LEAD_LINKEDIN_THREAD);

        // setup the mocked service
        doReturn(mockedEntity).when(service).update(anyString(), ArgumentMatchers.any(LeadDTO.class));

        // Execute the PUT request
        mockMvc.perform(put(getResourceURI() + "/{id}", LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + LEAD_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(LEAD_ID)))
                .andExpect(jsonPath("$.linkedInThread", is(LEAD_LINKEDIN_THREAD)));
    }
}
