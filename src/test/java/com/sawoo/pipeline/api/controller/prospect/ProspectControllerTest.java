package com.sawoo.pipeline.api.controller.prospect;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerTest;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.mock.ProspectMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.prospect.Prospect;
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
                DBConstants.PROSPECT_DOCUMENT,
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

    @Override
    protected Class<ProspectDTO> getDTOClass() {
        return ProspectDTO.class;
    }

    @Test
    @DisplayName("POST /api/prospects: firstName not informed - Failure")
    void createWhenFirstNameNotInformedReturnsFailure() throws Exception {
        String PROSPECT_LAST_NAME = getMockFactory().getFAKER().name().lastName();
        ProspectDTO postEntity = getMockFactory().newDTO(null, null, PROSPECT_LAST_NAME);

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
    @DisplayName("POST /api/prospects: company id and company name not informed - Failure")
    void createWhenCompanyIdAndCompanyNameNotInformedReturnsFailure() throws Exception {
        String PROSPECT_LAST_NAME = getMockFactory().getFAKER().name().lastName();
        String PROSPECT_FIRST_NAME = getMockFactory().getFAKER().name().firstName();
        ProspectDTO postEntity = getMockFactory().newDTO(null, PROSPECT_FIRST_NAME, PROSPECT_LAST_NAME);
        postEntity.getCompany().setName(null);

        // Execute the POST request
        mockMvc.perform(post(getResourceURI())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages[0]",
                        containsString("Field [company] must include either the [id] field or both [name and url] fields")));
    }

    @Test
    @DisplayName("POST /api/prospects: company id informed but company name and url not informed - Success")
    void createWhenCompanyIdInformedAndCompanyNameAndCompanyUrlNotInformedReturnsSuccess() throws Exception {
        String PROSPECT_LAST_NAME = getMockFactory().getFAKER().name().lastName();
        String PROSPECT_FIRST_NAME = getMockFactory().getFAKER().name().firstName();
        ProspectDTO postEntity = getMockFactory().newDTO(null, PROSPECT_FIRST_NAME, PROSPECT_LAST_NAME);
        postEntity.setCompany(CompanyDTO.builder().id(getMockFactory().getComponentId()).build());

        String PROSPECT_ID = getMockFactory().getComponentId();
        ProspectDTO mockedEntity = getMockFactory().newDTO(PROSPECT_ID, postEntity);

        // setup the mocked service
        doReturn(mockedEntity).when(service).create(ArgumentMatchers.any(ProspectDTO.class));

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
                .andExpect(jsonPath("$.company").exists());
    }

    @Test
    @DisplayName("PUT /api/prospects/{id}: resource exists - Success")
    void updateWhenResourceFoundAndUpdatedReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        String PROSPECT_POSITION = getMockFactory().getFAKER().company().profession();
        ProspectDTO postEntity = new ProspectDTO();
        postEntity.setPosition(PROSPECT_POSITION);
        ProspectDTO mockedEntity = getMockFactory().newDTO(PROSPECT_ID);
        mockedEntity.setPosition(PROSPECT_POSITION);

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
                .andExpect(jsonPath("$.position", is(PROSPECT_POSITION)));
    }
}
