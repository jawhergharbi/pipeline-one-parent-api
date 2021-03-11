package com.sawoo.pipeline.api.controller.campaign;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerTest;
import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.mock.CampaignMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.campaign.Campaign;
import com.sawoo.pipeline.api.service.campaign.CampaignService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

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
}
