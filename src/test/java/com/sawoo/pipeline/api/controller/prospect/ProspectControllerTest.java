package com.sawoo.pipeline.api.controller.prospect;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerTest;
import com.sawoo.pipeline.api.controller.base.BaseControllerTestOld;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.mock.CompanyMockFactory;
import com.sawoo.pipeline.api.mock.ProspectMockFactory;
import com.sawoo.pipeline.api.model.Company;
import com.sawoo.pipeline.api.model.DataStoreConstants;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.service.company.CompanyService;
import com.sawoo.pipeline.api.service.prospect.ProspectService;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Tag(value = "controller")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class ProspectControllerTest extends BaseControllerTest<ProspectDTO, Prospect, ProspectService, ProspectMockFactory> {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProspectService service;

    @Autowired
    public ProspectControllerTest(ProspectMockFactory mockFactory, ProspectService service, MockMvc mockMvc) {
        super(mockFactory,
                ControllerConstants.PROSPECT_CONTROLLER_API_BASE_URI,
                DataStoreConstants.PROSPECT_DOCUMENT,
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

    /*@Test
    @DisplayName("POST /api/prospect: resource firstName not informed - Failure")
    void createWhenFirstNameNotInformedReturnsFailure() throws Exception {
        String PROSPECT_ID = FAKER.internet().uuid();
        String PROSPECT_LAST_NAME = FAKER.name().lastName();
        ProspectDTO postEntity = getMockFactory().newProspectBaseDTO(PROSPECT_ID, null, PROSPECT_LAST_NAME);

        // Execute the POST request
        mockMvc.perform(post("/api/prospects/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    @Test
    @DisplayName("PUT /api/prospects/{id}: resource exists - Success")
    void updateWhenResourceFoundAndUpdatedReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String PROSPECT_ID = FAKER.internet().uuid();
        String PROSPECT_FIRST_NAME = FAKER.name().firstName();
        String PROSPECT_LAST_NAME = FAKER.name().lastName();
        String PROSPECT_POSITION = FAKER.company().profession();
        ProspectDTO postEntity = new ProspectDTO();
        postEntity.setPosition(PROSPECT_POSITION);
        ProspectDTO mockedDTOEntity = getMockFactory().newProspectDTO(PROSPECT_ID, PROSPECT_FIRST_NAME, PROSPECT_LAST_NAME);

        // setup the mocked service
        doReturn(mockedDTOEntity).when(service).update(anyString(), ArgumentMatchers.any(ProspectDTO.class));

        // Execute the PUT request
        mockMvc.perform(put("/api/prospects/{id}", PROSPECT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/prospects/" + PROSPECT_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(PROSPECT_ID)))
                .andExpect(jsonPath("$.firstName", is(PROSPECT_FIRST_NAME)))
                .andExpect(jsonPath("$.lastName", is(PROSPECT_LAST_NAME)));
    }*/
}
