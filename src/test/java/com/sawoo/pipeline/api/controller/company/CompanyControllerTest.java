package com.sawoo.pipeline.api.controller.company;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerTest;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.mock.CompanyMockFactory;
import com.sawoo.pipeline.api.model.Company;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.service.company.CompanyService;
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
public class CompanyControllerTest extends BaseControllerTest<CompanyDTO, Company, CompanyService, CompanyMockFactory> {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyService service;

    @Autowired
    public CompanyControllerTest(CompanyMockFactory mockFactory, CompanyService service, MockMvc mockMvc) {
        super(mockFactory,
                ControllerConstants.COMPANY_CONTROLLER_API_BASE_URI,
                DBConstants.COMPANY_DOCUMENT,
                service,
                mockMvc);
    }

    @Override
    protected String getExistCheckProperty() {
        return "name";
    }

    @Override
    protected List<String> getResourceFieldsToBeChecked() {
        return Arrays.asList("name", "url", "created");
    }

    @Test
    @DisplayName("POST /api/companies: resource name not informed - Failure")
    void createWhenNameAndSiteNotInformedReturnsFailure() throws Exception {
        // Setup the mocked entities
        String COMPANY_ID = getMockFactory().getComponentId();
        CompanyDTO postEntity = new CompanyDTO();
        postEntity.setId(COMPANY_ID);

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
    @DisplayName("PUT /api/companies/{id}: resource exists - Success")
    void updateWhenResourceFoundAndUpdatedReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String COMPONENT_ID = getMockFactory().getComponentId();
        String COMPANY_NAME = getMockFactory().getFAKER().company().name();
        String COMPANY_URL = getMockFactory().getFAKER().company().url();
        CompanyDTO postEntity = new CompanyDTO();
        postEntity.setUrl(COMPANY_URL);
        CompanyDTO mockedDTOEntity = getMockFactory().newDTO(COMPONENT_ID, COMPANY_NAME, COMPANY_URL);

        // setup the mocked service
        doReturn(mockedDTOEntity).when(service).update(anyString(), ArgumentMatchers.any(CompanyDTO.class));

        // Execute the PUT request
        mockMvc.perform(put(getResourceURI() + "/{id}", COMPONENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + COMPONENT_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(COMPONENT_ID)))
                .andExpect(jsonPath("$.name", is(COMPANY_NAME)))
                .andExpect(jsonPath("$.url", is(COMPANY_URL)));
    }
}
