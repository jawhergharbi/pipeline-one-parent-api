package com.sawoo.pipeline.api.controller.lead;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerTest;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.mock.LeadMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.lead.Lead;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
    @DisplayName("POST /api/leads: prospect not informed - Failure")
    void createWhenProspectNotInformedReturnsFailure() throws Exception {
        LeadDTO postEntity = getMockFactory().newDTO(null);
        postEntity.setProspect(null);

        // Execute the POST request
        mockMvc.perform(post(getResourceURI())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(2)));
    }

    @Test
    @DisplayName("POST /api/leads: only prospect id informed for prospect entity - Success")
    void createWhenProspectIdInformedReturnsSuccess() throws Exception {
        LeadDTO postEntity = getMockFactory().newDTO(null);
        postEntity.setProspect(ProspectDTO.builder().id(getMockFactory().getComponentId()).build());
        String PROSPECT_ID = getMockFactory().getComponentId();
        LeadDTO mockedEntity = getMockFactory().newDTO(PROSPECT_ID, postEntity);

        // setup the mocked service
        doReturn(mockedEntity).when(service).create(ArgumentMatchers.any(LeadDTO.class));

        // Execute the POST request
        mockMvc.perform(post(getResourceURI())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + PROSPECT_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(PROSPECT_ID)))
                .andExpect(jsonPath("$." + getExistCheckProperty()).exists());
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
