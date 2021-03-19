package com.sawoo.pipeline.api.controller.campaign;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.campaign.CampaignLeadAddDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignLeadDTO;
import com.sawoo.pipeline.api.mock.CampaignMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.service.campaign.CampaignService;
import com.sawoo.pipeline.api.utils.JacksonObjectMapperUtils;
import lombok.Getter;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@Tag(value = "controller")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class CampaignControllerLeadTest {

    @Autowired
    private MockMvc mockMvc;

    @Getter
    @Autowired
    private CampaignMockFactory mockFactory;

    @MockBean
    private CampaignService service;

    protected static String asJsonString(final Object obj) {
        return JacksonObjectMapperUtils.asJsonString(obj);
    }

    private String getResourceURI() {
        return ControllerConstants.CAMPAIGN_CONTROLLER_API_BASE_URI;
    }

    @Test
    @DisplayName("POST /api/campaigns/{id}/leads: campaign id and campaign lead valid - Success")
    void addCampaignLeadWhenCampaignIdAndCampaignLeadValidReturnsSuccess() throws Exception {
        // setup the mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        String LEAD_ID = getMockFactory().getFAKER().internet().uuid();
        String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();
        CampaignLeadAddDTO postEntity = getMockFactory().newCampaignLeadAddDTO();
        CampaignLeadDTO mockedEntity = getMockFactory().newCampaignLeadDTO(LEAD_ID, SEQUENCE_ID);

        // setup the mocked service
        doReturn(mockedEntity).when(service).addCampaignLead(anyString(), any(CampaignLeadAddDTO.class));

        // Execute the POST request
        mockMvc.perform(post(getResourceURI() + "/{id}/leads", COMPONENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + COMPONENT_ID + "/leads/" + LEAD_ID))

                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.lead").exists())
                .andExpect(jsonPath("$.lead.id", is(LEAD_ID)))
                .andExpect(jsonPath("$.sequence").exists())
                .andExpect(jsonPath("$.sequence.id", is(SEQUENCE_ID)));
    }

    @Test
    @DisplayName("POST /api/campaigns/{id}/leads: lead id not informed - Failure")
    void addCampaignLeadWhenCampaignLeadNotValidLeadIdNotInformedReturnsFailure() throws Exception {
        // setup the mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        CampaignLeadAddDTO postEntity = getMockFactory().newCampaignLeadAddDTO();
        postEntity.setLeadId(null);

        // Execute the POST request
        mockMvc.perform(post(getResourceURI() + "/{id}/leads", COMPONENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages",
                        hasItem(stringContainsInOrder("Field or param", "in component", "can not be empty or null"))));
    }

    @Test
    @DisplayName("POST /api/campaigns/{id}/leads: lead id and sequence id not informed - Failure")
    void addCampaignLeadWhenCampaignLeadNotValidLeadIdAndSequenceIdNotInformedReturnsFailure() throws Exception {
        // setup the mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        CampaignLeadAddDTO postEntity = getMockFactory().newCampaignLeadAddDTO();
        postEntity.setLeadId(null);
        postEntity.setSequenceId(null);

        // Execute the POST request
        mockMvc.perform(post(getResourceURI() + "/{id}/leads", COMPONENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(2)))
                .andExpect(jsonPath("$.messages",
                        hasItem(stringContainsInOrder("Field or param", "in component", "can not be empty or null"))));
    }

    @Test
    @DisplayName("POST /api/campaigns/{id}/leads: campaign not found - Failure")
    void addCampaignLeadWhenCampaignNotFoundReturnsFailure() throws Exception {
        // setup the mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        CampaignLeadAddDTO postEntity = getMockFactory().newCampaignLeadAddDTO();

        // setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ DBConstants.CAMPAIGN_DOCUMENT, COMPONENT_ID });
        doThrow(exception).when(service).addCampaignLead(anyString(), any(CampaignLeadAddDTO.class));

        // Execute the POST request
        mockMvc.perform(post(getResourceURI() + "/{id}/leads", COMPONENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", DBConstants.CAMPAIGN_DOCUMENT),
                        COMPONENT_ID)));
    }

    @Test
    @DisplayName("DELETE /api/campaigns/{id}/leads/{leadId}: campaign id and lead id valid - Success")
    void removeCampaignLeadWhenCampaignIdAndCampaignLeadValidReturnsSuccess() throws Exception {
        // setup the mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        String LEAD_ID = getMockFactory().getFAKER().internet().uuid();
        String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();
        CampaignLeadDTO mockedEntity = getMockFactory().newCampaignLeadDTO(LEAD_ID, SEQUENCE_ID);

        // setup the mocked service
        doReturn(mockedEntity).when(service).removeCampaignLead(anyString(), anyString());

        // Execute the POST request
        mockMvc.perform(delete(getResourceURI() + "/{id}/leads/{leadId}", COMPONENT_ID, LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.lead").exists())
                .andExpect(jsonPath("$.lead.id", is(LEAD_ID)))
                .andExpect(jsonPath("$.sequence").exists())
                .andExpect(jsonPath("$.sequence.id", is(SEQUENCE_ID)));
    }

    @Test
    @DisplayName("DELETE/api/campaigns/{id}/leads/{leadId}: campaign not found - Failure")
    void removeCampaignLeadWhenCampaignNotFoundReturnsFailure() throws Exception {
        // setup the mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        String LEAD_ID = getMockFactory().getFAKER().internet().uuid();

        // setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ DBConstants.CAMPAIGN_DOCUMENT, COMPONENT_ID });
        doThrow(exception).when(service).removeCampaignLead(anyString(), anyString());

        // Execute the POST request
        mockMvc.perform(delete(getResourceURI() + "/{id}/leads/{leadId}", COMPONENT_ID, LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", DBConstants.CAMPAIGN_DOCUMENT),
                        COMPONENT_ID)));
    }
}
