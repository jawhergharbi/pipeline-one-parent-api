package com.sawoo.pipeline.api.controller.account;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerTest;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.dto.client.ClientBasicDTO;
import com.sawoo.pipeline.api.mock.AccountMockFactory;
import com.sawoo.pipeline.api.model.DataStoreConstants;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.service.account.AccountService;
import org.junit.jupiter.api.*;
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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Tag(value = "controller")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class AccountControllerTest extends BaseControllerTest<AccountDTO, Account, AccountService, AccountMockFactory> {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService service;

    @Autowired
    public AccountControllerTest(AccountMockFactory mockFactory, AccountService service, MockMvc mockMvc) {
        super(mockFactory,
                ControllerConstants.ACCOUNT_CONTROLLER_API_BASE_URI,
                DataStoreConstants.ACCOUNT_DOCUMENT,
                service,
                mockMvc);
    }

    @Override
    protected String getExistCheckProperty() {
        return "email";
    }

    @Override
    protected List<String> getResourceFieldsToBeChecked() {
        return Arrays.asList("email", "fullName", "created");
    }

    @Test
    @DisplayName("POST /api/accounts: resource name not informed - Failure")
    void createWhenFullNameAndPositionNotInformedReturnsFailure() throws Exception {
        // Setup the mocked entities
        AccountDTO postEntity = getMockFactory().newDTO(null);
        postEntity.setFullName(null);
        postEntity.setPosition(null);

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
    @DisplayName("PUT /api/accounts/{id}: resource exists - Success")
    void updateWhenResourceFoundAndUpdatedReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        String ACCOUNT_POSITION = getMockFactory().getFAKER().company().profession();
        AccountDTO postEntity = new AccountDTO();
        postEntity.setPosition(ACCOUNT_POSITION);
        AccountDTO mockedDTO = getMockFactory().newDTO(ACCOUNT_ID);
        mockedDTO.setPosition(ACCOUNT_POSITION);

        // setup the mocked service
        doReturn(mockedDTO).when(service).update(anyString(), any(AccountDTO.class));

        // Execute the PUT request
        mockMvc.perform(put(getResourceURI() + "/{id}", ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + ACCOUNT_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(ACCOUNT_ID)))
                .andExpect(jsonPath("$.position", is(ACCOUNT_POSITION)));
    }
}
