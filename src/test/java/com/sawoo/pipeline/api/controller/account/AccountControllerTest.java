package com.sawoo.pipeline.api.controller.account;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerTest;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
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
import org.springframework.test.web.servlet.ResultActions;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
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
    @DisplayName("POST /api/accounts: resource fullName not informed - Failure")
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
    @DisplayName("POST /api/accounts: resource company not informed - Failure")
    void createWhenCompanyNotInformedReturnsFailure() throws Exception {
        // Setup the mocked entities
        AccountDTO postEntity = getMockFactory().newDTO(null);
        postEntity.setCompany(null);

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

    @Test
    @DisplayName("PUT /api/accounts: resource exists and company updated - Success")
    void updateWhenClientFoundAndUpdatedCompanyFieldReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        AccountDTO postEntity = new AccountDTO();
        String ACCOUNT_NEW_COMPANY_NAME = getMockFactory().getFAKER().company().name();
        String ACCOUNT_NEW_COMPANY_URL = getMockFactory().getFAKER().company().url();
        CompanyDTO company = CompanyDTO
                .builder()
                .name(ACCOUNT_NEW_COMPANY_NAME)
                .url(ACCOUNT_NEW_COMPANY_URL)
                .build();
        postEntity.setCompany(company);

        AccountDTO mockedEntity = getMockFactory().newDTO(ACCOUNT_ID);
        mockedEntity.setCompany(company);

        // setup the mocked service
        doReturn(mockedEntity).when(service).update(anyString(), any(AccountDTO.class));

        // Execute the PUT request
        executePutRequest(ACCOUNT_ID, postEntity)

                // Validate the returned fields
                .andExpect(jsonPath("$.company.name", is(ACCOUNT_NEW_COMPANY_NAME)))
                .andExpect(jsonPath("$.company.url", is(ACCOUNT_NEW_COMPANY_URL)));
    }

    @Test
    @DisplayName("GET /api/accounts/user/{id}: find all accounts by user id return entities found- Success")
    void findAllByUserIdWhenEntitiesFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String USER_ID = getMockFactory().getUserMockFactory().getComponentId();
        int listSize = 3;
        List<String> ids = new ArrayList<>();
        List<AccountDTO> clientList = IntStream.range(0, listSize)
                .mapToObj((account) -> {
                    String COMPONENT_ID = getMockFactory().getComponentId();
                    ids.add(COMPONENT_ID);
                    return getMockFactory().newDTO(COMPONENT_ID);
                }).collect(Collectors.toList());

        // setup the mocked service
        doReturn(clientList).when(service).findAllByUser(USER_ID);

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/user/{id}", USER_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(listSize)))
                .andExpect(jsonPath("$[0].id", is(ids.get(0))));
    }

    @Test
    @DisplayName("GET /api/accounts/user/{id}: find all accounts by user id returns empty list - Success")
    void findAllByUserIdWhenNoEntityFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String USER_ID = getMockFactory().getUserMockFactory().getComponentId();

        // setup the mocked service
        doReturn(Collections.EMPTY_LIST).when(service).findAllByUser(USER_ID);

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/user/{id}", USER_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/users/{id}/clients: get all clients when user not found - Success")
    void findAllWhenUserNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked entities
        String USER_ID = getMockFactory().getUserMockFactory().getComponentId();

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{"User", String.valueOf(USER_ID)});

        // setup the mocked service
        doThrow(exception).when(service).findAllByUser(USER_ID);

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/user/{id}", USER_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("GET operation. Component type [User]")));
    }

    private ResultActions executePutRequest(String accountId, AccountDTO postEntity) throws Exception {
        // Execute the PUT request
        return mockMvc.perform(put(getResourceURI() + "/{id}", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + accountId))

                // Validate common returned fields
                .andExpect(jsonPath("$.id", is(accountId)));
    }
}
