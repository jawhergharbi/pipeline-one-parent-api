package com.sawoo.pipeline.api.controller.campaign;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.campaign.CampaignProspectDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignProspectAddDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignProspectBaseDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignProspectCreateDTO;
import com.sawoo.pipeline.api.mock.CampaignMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.campaign.CampaignProspectStatus;
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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
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
class CampaignControllerProspectTest {

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
    @DisplayName("POST /api/campaigns/{id}/prospects: campaign id and campaign prospect valid - Success")
    void createProspectWhenCampaignIdAndCampaignProspectValidReturnsSuccess() throws Exception {
        // setup the mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();
        String ACCOUNT_ID = getMockFactory().getFAKER().internet().uuid();
        String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();
        CampaignProspectCreateDTO postEntity = getMockFactory().newCampaignProspectCreateDTO(ACCOUNT_ID, SEQUENCE_ID);
        postEntity.getProspect().setId(null);
        CampaignProspectDTO mockedEntity = getMockFactory().newCampaignProspectDTO(PROSPECT_ID, SEQUENCE_ID);

        // setup the mocked service
        doReturn(mockedEntity).when(service).createProspect(anyString(), any(CampaignProspectCreateDTO.class));

        // Execute the POST request
        mockMvc.perform(post(getResourceURI() + "/{id}/" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME, COMPONENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + COMPONENT_ID + "/" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME + "/" + PROSPECT_ID))

                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.prospect").exists())
                .andExpect(jsonPath("$.prospect.id", is(PROSPECT_ID)))
                .andExpect(jsonPath("$.sequence").exists())
                .andExpect(jsonPath("$.sequence.id", is(SEQUENCE_ID)));
    }

    @Test
    @DisplayName("POST /api/campaigns/{id}/prospects: account id and sequence id not informed - Failure")
    void createProspectWhenCampaignProspectNotValidProspectIdAndSequenceIdNotInformedReturnsFailure() throws Exception {
        // setup the mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        CampaignProspectCreateDTO postEntity = getMockFactory().newCampaignProspectCreateDTO(null, null);

        // Execute the POST request
        mockMvc.perform(post(getResourceURI() + "/{id}/" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME, COMPONENT_ID)
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
    @DisplayName("POST /api/campaigns/{id}/prospects/{prospectId}: campaign id and campaign prospect valid - Success")
    void addProspectWhenCampaignIdAndCampaignProspectValidReturnsSuccess() throws Exception {
        // setup the mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();
        String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();
        CampaignProspectAddDTO postEntity = getMockFactory().newCampaignProspectAddDTO(SEQUENCE_ID);
        CampaignProspectDTO mockedEntity = getMockFactory().newCampaignProspectDTO(PROSPECT_ID, SEQUENCE_ID);

        // setup the mocked service
        doReturn(mockedEntity).when(service).addProspect(anyString(), any(CampaignProspectAddDTO.class));

        // Execute the POST request
        mockMvc.perform(post(
                getResourceURI() +
                        "/{id}/" +
                        ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME +
                        "/{" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_PATH_VARIABLE_NAME + "}",
                COMPONENT_ID, PROSPECT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + COMPONENT_ID + "/" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME + "/" + PROSPECT_ID))

                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.prospect").exists())
                .andExpect(jsonPath("$.prospect.id", is(PROSPECT_ID)))
                .andExpect(jsonPath("$.sequence").exists())
                .andExpect(jsonPath("$.sequence.id", is(SEQUENCE_ID)));
    }

    @Test
    @DisplayName("POST /api/campaigns/{id}/prospects/{prospectId}: prospect id and sequence id not informed - Failure")
    void addProspectWhenCampaignProspectNotValidProspectIdAndSequenceIdNotInformedReturnsFailure() throws Exception {
        // setup the mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();
        CampaignProspectAddDTO postEntity = getMockFactory().newCampaignProspectAddDTO();
        postEntity.setSequenceId(null);

        // Execute the POST request
        mockMvc.perform(post(
                getResourceURI() +
                        "/{id}/" +
                        ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME +
                        "/{" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_PATH_VARIABLE_NAME + "}",
                COMPONENT_ID, PROSPECT_ID)
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
    @DisplayName("POST /api/campaigns/{id}/prospects/{prospectId}: campaign not found - Failure")
    void addProspectWhenCampaignNotFoundReturnsFailure() throws Exception {
        // setup the mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();
        CampaignProspectAddDTO postEntity = getMockFactory().newCampaignProspectAddDTO(PROSPECT_ID);

        // setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ DBConstants.CAMPAIGN_DOCUMENT, COMPONENT_ID });
        doThrow(exception).when(service).addProspect(anyString(), any(CampaignProspectAddDTO.class));

        // Execute the POST request
        mockMvc.perform(post(
                getResourceURI() +
                        "/{id}/" +
                        ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME +
                        "/{" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_PATH_VARIABLE_NAME + "}",
                COMPONENT_ID, PROSPECT_ID)
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
    @DisplayName("DELETE /api/campaigns/{id}/prospects/{prospectId}: campaign id and prospect id valid - Success")
    void removeProspectWhenCampaignIdAndCampaignProspectValidReturnsSuccess() throws Exception {
        // setup the mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();
        String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();
        CampaignProspectDTO mockedEntity = getMockFactory().newCampaignProspectDTO(PROSPECT_ID, SEQUENCE_ID);

        // setup the mocked service
        doReturn(mockedEntity).when(service).removeProspect(anyString(), anyString(), eq(null));

        // Execute the DELETE request
        mockMvc.perform(delete(
                getResourceURI() +
                        "/{id}/" +
                        ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME +
                        "/{" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_PATH_VARIABLE_NAME + "}",
                COMPONENT_ID, PROSPECT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.prospect").exists())
                .andExpect(jsonPath("$.prospect.id", is(PROSPECT_ID)))
                .andExpect(jsonPath("$.sequence").exists())
                .andExpect(jsonPath("$.sequence.id", is(SEQUENCE_ID)));
    }

    @Test
    @DisplayName("DELETE /api/campaigns/{id}/prospects/{prospectId}: campaign not found - Failure")
    void removeProspectWhenCampaignNotFoundReturnsFailure() throws Exception {
        // setup the mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();

        // setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ DBConstants.CAMPAIGN_DOCUMENT, COMPONENT_ID });
        doThrow(exception).when(service).removeProspect(anyString(), anyString(), eq(null));

        // Execute the DELETE request
        mockMvc.perform(delete(
                getResourceURI() +
                        "/{id}/" +
                        ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME +
                        "/{" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_PATH_VARIABLE_NAME + "}",
                COMPONENT_ID, PROSPECT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", DBConstants.CAMPAIGN_DOCUMENT),
                        COMPONENT_ID)));
    }

    @Test
    @DisplayName("PUT /api/campaigns/{id}/prospects/{prospectId}: campaign not found - Failure")
    void updateProspectWhenCampaignNotFoundReturnsFailure() throws Exception {
        // setup the mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();
        CampaignProspectBaseDTO postEntity = CampaignProspectBaseDTO
                .builder()
                .prospectId(PROSPECT_ID)
                .status(CampaignProspectStatus.ARCHIVED.getValue()).build();

        // setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ DBConstants.CAMPAIGN_DOCUMENT, COMPONENT_ID });
        doThrow(exception).when(service).updateProspect(anyString(), anyString(), any(CampaignProspectBaseDTO.class));

        // Execute the PUT request
        mockMvc.perform(put(
                getResourceURI() +
                        "/{id}/" +
                        ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME +
                        "/{" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_PATH_VARIABLE_NAME + "}",
                COMPONENT_ID, PROSPECT_ID)
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
    @DisplayName("PUT /api/campaigns/{id}/prospects/{prospectId}: campaign prospect valid - Failure")
    void updateProspectWhenCampaignLeadNotValidReturnsSuccess() throws Exception {
        // setup the mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();
        String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();
        CampaignProspectBaseDTO postEntity = CampaignProspectBaseDTO
                .builder()
                .startDate(LocalDateTime.now(ZoneOffset.UTC).plusDays(20))
                .status(CampaignProspectStatus.ARCHIVED.getValue())
                .build();
        CampaignProspectDTO mockedEntity = getMockFactory().newCampaignProspectDTO(PROSPECT_ID, SEQUENCE_ID);

        // setup the mocked service
        doReturn(mockedEntity).when(service).updateProspect(anyString(), anyString(), any(CampaignProspectBaseDTO.class));

        // Execute the PUT request
        mockMvc.perform(put(
                getResourceURI() +
                        "/{id}/" +
                        ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME +
                        "/{" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_PATH_VARIABLE_NAME + "}",
                COMPONENT_ID, PROSPECT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.prospect").exists())
                .andExpect(jsonPath("$.prospect.id", is(PROSPECT_ID)));
    }

    @Test
    @DisplayName("GET /api/campaigns/{id}/prospects: campaign not found - Failure")
    void findAllProspectsWhenCampaignNotFoundReturnsFailure() throws Exception {
        // setup the mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();

        // setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ DBConstants.CAMPAIGN_DOCUMENT, COMPONENT_ID });
        doThrow(exception).when(service).findAllProspects(anyString());

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME, COMPONENT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", DBConstants.CAMPAIGN_DOCUMENT),
                        COMPONENT_ID)));
    }

    @Test
    @DisplayName("GET /api/campaigns/{id}/prospects: campaign found - Success")
    void findAllProspectsWhenCampaignFoundReturnsSuccess() throws Exception {
        // setup the mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        int LIST_SIZE = 3;
        List<CampaignProspectDTO> prospects = IntStream.range(0, LIST_SIZE)
                .mapToObj((p) -> {
                    String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();
                    String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();
                    return getMockFactory().newCampaignProspectDTO(PROSPECT_ID, SEQUENCE_ID);
                }).collect(Collectors.toList());

        // setup the mocked service
        doReturn(prospects).when(service).findAllProspects(anyString());

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME, COMPONENT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(LIST_SIZE)))
                .andExpect(jsonPath("$[0].prospect").exists());
    }
}
