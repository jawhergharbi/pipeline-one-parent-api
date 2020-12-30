package com.sawoo.pipeline.api.controller.lead;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseLightControllerTest;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.mock.LeadMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.service.lead.LeadService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.web.servlet.MockMvc;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Tag(value = "controller")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class LeadInteractionControllerTest extends BaseLightControllerTest<LeadDTO, Lead, LeadService, LeadMockFactory> {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeadService service;

    @Autowired
    public LeadInteractionControllerTest(LeadMockFactory mockFactory, LeadService service) {
        super(mockFactory,
                ControllerConstants.LEAD_CONTROLLER_API_BASE_URI,
                DBConstants.LEAD_DOCUMENT,
                service);
    }

    @Test
    @DisplayName("POST /api/leads/{type}: lead type null - Success")
    void createLeadInteractionWhenLeadTypeNotInformedReturnsSuccess() throws Exception {

    }
}
