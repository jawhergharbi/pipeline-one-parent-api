package com.sawoo.pipeline.api.controller.campaign;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerTest;
import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.mock.CampaignMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.campaign.Campaign;
import com.sawoo.pipeline.api.service.campaign.CampaignService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@Tag(value = "controller")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class CampaignControllerTest extends BaseControllerTest<CampaignDTO, Campaign, CampaignService, CampaignMockFactory> {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CampaignService service;

    @Autowired
    public CampaignControllerTest(CampaignMockFactory mockFactory, CampaignService service, MockMvc mockMvc) {
        super(mockFactory,
                ControllerConstants.CAMPAIGN_CONTROLLER_API_BASE_URI,
                DBConstants.CAMPAIGN_DOCUMENT,
                service,
                mockMvc);
    }

    @Override
    protected String getExistCheckProperty() {
        return "name";
    }

    @Override
    protected List<String> getResourceFieldsToBeChecked() {
        return Arrays.asList("name", "componentId", "created");
    }

    @Override
    protected Class<CampaignDTO> getDTOClass() {
        return CampaignDTO.class;
    }

    @Test
    @DisplayName("POST /api/campaigns: resource componentId - Failure")
    void createWhenComponentIdNotInformedReturnsFailure() throws Exception {
        // Setup the mocked entities
        CampaignDTO postEntity = getMockFactory().newDTO(null);
        postEntity.setComponentId(null);

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
    @DisplayName("POST /api/campaigns: name and endDate are not informed - Failure")
    void createWhenNameAndEndDateNotInformedReturnsFailure() throws Exception {
        // Setup the mocked entities
        CampaignDTO postEntity = getMockFactory().newDTO(null);
        postEntity.setName(null);
        postEntity.setEndDate(null);

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
    @DisplayName("GET /api/campaigns/accounts/{accountIds}/main: campaign found - Success")
    void findByAccountsWhenSequenceEntitiesFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String COMPONENT_ID = getMockFactory().getAccountMockFactory().getComponentId();
        String ENTITY_ID = getMockFactory().getComponentId();
        CampaignDTO mockedDTO = getMockFactory().newDTO(ENTITY_ID);

        // setup the mocked service
        doReturn(Collections.singletonList(mockedDTO)).when(service).findByAccountIds(anySet());

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/accounts/{accountIds}/main", new HashSet<>(Collections.singletonList(COMPONENT_ID))))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
