package com.sawoo.pipeline.api.interagtion.company;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.interagtion.MongoDataFile;
import com.sawoo.pipeline.api.interagtion.MongoSpringExtension;
import com.sawoo.pipeline.api.interagtion.base.BaseIntegrationTest;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.company.Company;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@Tags({@Tag(value = "integration")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@ExtendWith({SpringExtension.class, MongoSpringExtension.class})
public class CompanyIntegrationTest extends BaseIntegrationTest<Company> {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    public CompanyIntegrationTest(MongoTemplate mongoTemplate) {
        super(mongoTemplate,
                ControllerConstants.COMPANY_CONTROLLER_API_BASE_URI,
                DBConstants.COMPANY_DOCUMENT,
                null);
    }

    @Override
    protected Class<Company[]> getClazz() {
        return Company[].class;
    }

    @Test
    @MongoDataFile(
            value = "company-integration-test-data.json",
            classType = Company.class,
            collectionName = DBConstants.COMPANY_DOCUMENT)
    @DisplayName("POST /api/companies/{id}: find by id when entity found - Success")
    void findByIdWhenEntityFoundReturnsSuccess() throws Exception {
        // Set up
        String COMPANY_ID = "1";
        String COMPANY_NAME = "google";
        String COMPANY_URL = "http://google.com";
        // Execute the GET request
        mockMvc.perform(get(ControllerConstants.COMPANY_CONTROLLER_API_BASE_URI + "/{id}", COMPANY_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(COMPANY_ID)))
                .andExpect(jsonPath("$.name", is(COMPANY_NAME)))
                .andExpect(jsonPath("$.url", is(COMPANY_URL)));
    }
}
