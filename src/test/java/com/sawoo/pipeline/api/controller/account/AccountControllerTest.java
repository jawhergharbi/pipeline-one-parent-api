package com.sawoo.pipeline.api.controller.account;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerTest;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.dto.account.AccountFieldDTO;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectTypeRequestParam;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.mock.AccountMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.common.Status;
import com.sawoo.pipeline.api.model.prospect.ProspectStatusList;
import com.sawoo.pipeline.api.model.user.UserRole;
import com.sawoo.pipeline.api.service.account.AccountService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.ArgumentCaptor;
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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
class AccountControllerTest extends BaseControllerTest<AccountDTO, Account, AccountService, AccountMockFactory> {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService service;

    @Autowired
    public AccountControllerTest(AccountMockFactory mockFactory, AccountService service, MockMvc mockMvc) {
        super(mockFactory,
                ControllerConstants.ACCOUNT_CONTROLLER_API_BASE_URI,
                DBConstants.ACCOUNT_DOCUMENT,
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

    @Override
    protected Class<AccountDTO> getDTOClass() {
        return AccountDTO.class;
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
    void updateWhenAccountFoundAndUpdatedCompanyFieldReturnsSuccess() throws Exception {
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
        mockMvc.perform(put(getResourceURI() + "/{id}", ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + ACCOUNT_ID))

                // Validate common returned fields
                .andExpect(jsonPath("$.id", is(ACCOUNT_ID)))
                .andExpect(jsonPath("$.company.name", is(ACCOUNT_NEW_COMPANY_NAME)))
                .andExpect(jsonPath("$.company.url", is(ACCOUNT_NEW_COMPANY_URL)));
    }

    @Test
    @DisplayName("PUT /api/accounts/user/{id}: update user role manager - Success")
    void updateUserWhenUserRoleManagerAndEntityFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        String USER_ID = getMockFactory().getUserMockFactory().getComponentId();
        AccountDTO postEntity = new AccountDTO();
        UserAuthDTO user = getMockFactory()
                .getUserMockFactory()
                .newDTO(USER_ID);
        user.setRoles(Set.of(new String[]{UserRole.MNG.name(), UserRole.USER.name()}));
        postEntity.getUsers().add(user);

        AccountDTO mockedEntity = getMockFactory().newDTO(ACCOUNT_ID);
        mockedEntity.getUsers().add(user);

        // setup the mocked service
        doReturn(mockedEntity).when(service).updateUser(anyString(), anyString());

        // Execute the PUT request
        mockMvc.perform(put(getResourceURI() + "/{id}/user/{userId}", ACCOUNT_ID, USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + ACCOUNT_ID))

                // Validate common returned fields
                .andExpect(jsonPath("$.id", is(ACCOUNT_ID)))
                .andExpect(jsonPath("$.users").exists())
                .andExpect(jsonPath("$.users", hasSize(1)))
                .andExpect(jsonPath("$.users[0].roles", containsInAnyOrder(UserRole.MNG.name(), UserRole.USER.name())));
    }

    @Test
    @DisplayName("PUT /api/accounts/{id}/user/{userId}: user not found - Failure")
    void updateUserWhenUserNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked entities
        String USER_ID = getMockFactory().getUserMockFactory().getComponentId();
        String ACCOUNT_ID = getMockFactory().getComponentId();

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ DBConstants.USER_DOCUMENT, USER_ID });

        // setup the mocked service
        doThrow(exception).when(service).updateUser(anyString(), anyString());

        // Execute the PUT request
        mockMvc.perform(put(getResourceURI() + "/{id}/user/{userId}", ACCOUNT_ID, USER_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(
                        "$.message", stringContainsInOrder(
                                String.format("GET operation. Component type [%s]", DBConstants.USER_DOCUMENT),
                                USER_ID)));
    }

    @Test
    @DisplayName("PUT /api/accounts/{id}/user/{userId}: user not found - Failure")
    void updateUserWhenAccountNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked entities
        String USER_ID = getMockFactory().getUserMockFactory().getComponentId();
        String ACCOUNT_ID = getMockFactory().getComponentId();

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ DBConstants.ACCOUNT_DOCUMENT, ACCOUNT_ID });

        // setup the mocked service
        doThrow(exception).when(service).updateUser(anyString(), anyString());

        // Execute the PUT request
        mockMvc.perform(put(getResourceURI() + "/{id}/user/{userId}", ACCOUNT_ID, USER_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", DBConstants.ACCOUNT_DOCUMENT),
                        ACCOUNT_ID)));
    }

    @Test
    @DisplayName("GET /api/accounts/user/{id}: get accounts by user returns entities found- Success")
    void findAllByUserWhenEntitiesFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String USER_ID = getMockFactory().getUserMockFactory().getComponentId();
        int ID_LIST_SIZE = 3;
        List<String> ids = new ArrayList<>();
        List<AccountDTO> accountList = IntStream.range(0, ID_LIST_SIZE)
                .mapToObj((account) -> {
                    String COMPONENT_ID = getMockFactory().getComponentId();
                    ids.add(COMPONENT_ID);
                    return getMockFactory().newDTO(COMPONENT_ID);
                }).collect(Collectors.toList());

        // setup the mocked service
        doReturn(accountList).when(service).findAllByUser(USER_ID);

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/user/{id}", USER_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(ID_LIST_SIZE)))
                .andExpect(jsonPath("$[0].id", is(ids.get(0))));
    }

    @Test
    @DisplayName("GET /api/accounts/user/{id}: get accounts by user returns empty list - Success")
    void findAllByUserWhenNoUserFoundReturnsSuccess() throws Exception {
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
    @DisplayName("GET /api/accounts/user/{id}: get accounts by user when user not found - Success")
    void findAllByUserWhenUserNotFoundReturnsResourceNotFoundException() throws Exception {
        // Setup the mocked entities
        String USER_ID = getMockFactory().getUserMockFactory().getComponentId();

        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ DBConstants.USER_DOCUMENT, String.valueOf(USER_ID)});

        // setup the mocked service
        doThrow(exception).when(service).findAllByUser(USER_ID);

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/user/{id}", USER_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", DBConstants.USER_DOCUMENT),
                        USER_ID)));
    }

    @Test
    @DisplayName("GET /api/accounts/{id}/prospects: get prospect for account id returns list of prospects - Success")
    void findAllProspectsWhenAccountFoundAndProspectsFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        int ID_LIST_SIZE = 3;
        List<ProspectDTO> prospectList = IntStream.range(0, ID_LIST_SIZE)
                .mapToObj((account) -> {
                    String COMPONENT_ID = getMockFactory().getComponentId();
                    return getMockFactory().getProspectMockFactory().newDTO(COMPONENT_ID);
                }).collect(Collectors.toList());

        // setup the mocked service
        doReturn(prospectList).when(service).findAllProspects(ACCOUNT_ID);

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME, ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(ID_LIST_SIZE)));
    }

    @Test
    @DisplayName("GET /api/accounts/{id}/prospects: get prospects for account id returns an empty list - Success")
    void findAllProspectsWhenAccountFoundAndEmptyProspectListReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();

        // setup the mocked service
        doReturn(Collections.EMPTY_LIST).when(service).findAllProspects(ACCOUNT_ID);

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME, ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/accounts/{id}/prospects: account not found - Failure")
    void findAllProspectsWhenAccountNotFoundReturnsFailure() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();

        // Setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ getEntityType(), String.valueOf(ACCOUNT_ID)});
        doThrow(exception).when(service).findAllProspects(ACCOUNT_ID);

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME, ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", getEntityType()),
                        ACCOUNT_ID)));
    }

    @Test
    @DisplayName("GET /api/accounts/{ids}/prospects/main: get prospect list for a list of accounts returns an empty list - Success")
    void findAllProspectsWhenAccountsFoundAndNoProspectsFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        int ACCOUNT_LIST_SIZE = 2;
        List<String> ACCOUNT_IDS = IntStream.range(0, ACCOUNT_LIST_SIZE)
                .mapToObj( s -> getMockFactory().getComponentId() )
                .collect(Collectors.toList());
        // setup the mocked service
        doReturn(Collections.EMPTY_LIST)
                .when(service)
                .findAllProspects(ACCOUNT_IDS.toArray(String[]::new), null);

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{ids}/" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME + "/main", String.join(",", ACCOUNT_IDS))
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/accounts/{ids}/prospects/main: get prospect list is found - Success")
    void findAllProspectsWhenAccountsFoundAndProspectsFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        int ACCOUNT_LIST_SIZE = 2;
        List<AccountFieldDTO> ACCOUNT_LIST = IntStream.range(0, ACCOUNT_LIST_SIZE)
                .mapToObj( (obj) -> {
                    String COMPONENT_ID = getMockFactory().getComponentId();
                    return getMockFactory().newAccountDTO(COMPONENT_ID);
                })
                .collect(Collectors.toList());
        List<String> ACCOUNT_IDS = ACCOUNT_LIST.stream().map(AccountFieldDTO::getId).collect(Collectors.toList());
        int PROSPECT_LIST_SIZE = 4;
        List<ProspectDTO> PROSPECT_LIST = createProspectList(PROSPECT_LIST_SIZE, ACCOUNT_LIST_SIZE, ACCOUNT_LIST);


        // setup the mocked service
        doReturn(PROSPECT_LIST)
                .when(service)
                .findAllProspects(ACCOUNT_IDS.toArray(String[]::new), null);

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{ids}/" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME + "/main", String.join(",", ACCOUNT_IDS))
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(PROSPECT_LIST_SIZE)))
                .andExpect(jsonPath("$.[0].account").exists())
                .andExpect(jsonPath("$.[0].id", is(PROSPECT_LIST.get(0).getId())));
    }

    @Test
    @DisplayName("GET /api/accounts/{ids}/prospects/main: get prospect list is found - Success")
    void findAllProspectsWhenAccountsFoundAndProspectStatusDeadAndProspectsFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        int ACCOUNT_LIST_SIZE = 1;
        List<AccountFieldDTO> ACCOUNT_LIST = IntStream.range(0, ACCOUNT_LIST_SIZE)
                .mapToObj( (obj) -> {
                    String COMPONENT_ID = getMockFactory().getComponentId();
                    return getMockFactory().newAccountDTO(COMPONENT_ID);
                })
                .collect(Collectors.toList());
        List<String> ACCOUNT_IDS = ACCOUNT_LIST.stream().map(AccountFieldDTO::getId).collect(Collectors.toList());
        int PROSPECT_LIST_SIZE = 3;
        List<ProspectDTO> PROSPECT_LIST = createProspectList(PROSPECT_LIST_SIZE, ACCOUNT_LIST_SIZE, ACCOUNT_LIST);

        // setup the mocked service
        doReturn(PROSPECT_LIST)
                .when(service)
                .findAllProspects(ACCOUNT_IDS.toArray(String[]::new), new Integer[] {ProspectStatusList.DEAD.getStatus()});

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{ids}/" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME + "/main", String.join(",", ACCOUNT_IDS))
                .contentType(MediaType.APPLICATION_JSON)
                .param("status", String.valueOf(ProspectStatusList.DEAD.getStatus())))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(PROSPECT_LIST_SIZE)))
                .andExpect(jsonPath("$.[0].account").exists())
                .andExpect(jsonPath("$.[0].id", is(PROSPECT_LIST.get(0).getId())))
                .andExpect(jsonPath("$.[0].status.value", is(ProspectStatusList.DEAD.getStatus())));
    }

    @Test
    @DisplayName("DELETE /api/accounts/{id}/prospects: prospect found - Success")
    void removeProspectWhenAccountFoundAndProspectFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        String PROSPECT_ID = getMockFactory().getProspectMockFactory().getComponentId();
        ProspectDTO mockedProspect = getMockFactory().getProspectMockFactory().newDTO(PROSPECT_ID);

        // setup the mocked service
        doReturn(mockedProspect).when(service).removeProspect(anyString(), anyString());

        // Execute the GET request
        mockMvc.perform(delete(
                getResourceURI() +
                        "/{id}/" +
                        ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME +
                        "/" +
                        ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_PATH_VARIABLE_NAME,
                ACCOUNT_ID, PROSPECT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate common returned fields
                .andExpect(jsonPath("$.id", is(PROSPECT_ID)))
                .andExpect(jsonPath("$.person").exists());
    }

    @Test
    @DisplayName("DELETE /api/accounts/{id}/prospects: prospect not found - Failure")
    void removeProspectWhenProspectNotFoundReturnsFailure() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        String PROSPECT_ID = getMockFactory().getProspectMockFactory().getComponentId();

        // setup the mocked service
        CommonServiceException exception = new  CommonServiceException(
                ExceptionMessageConstants.ACCOUNT_PROSPECT_REMOVE_PROSPECT_NOT_FOUND_EXCEPTION,
                new String[] {ACCOUNT_ID, PROSPECT_ID});
        doThrow(exception).when(service).removeProspect(anyString(), anyString());

        // Execute the GET request
        mockMvc.perform(delete(
                getResourceURI() +
                        "/{id}/" +
                        ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME +
                        "/" +
                        ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_PATH_VARIABLE_NAME,
                ACCOUNT_ID, PROSPECT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and content type
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", stringContainsInOrder("Prospect not found in the account prospect list")));
    }

    @Test
    @DisplayName("DELETE /api/accounts/{id}/prospects: account not found - Failure")
    void removeProspectWhenAccountNotFoundReturnsFailure() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        String PROSPECT_ID = getMockFactory().getProspectMockFactory().getComponentId();

        // setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ getEntityType(), String.valueOf(ACCOUNT_ID)});
        doThrow(exception).when(service).removeProspect(anyString(), anyString());

        // Execute the GET request
        mockMvc.perform(delete(
                getResourceURI() +
                        "/{id}/" +
                        ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME +
                        "/" +
                        ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_PATH_VARIABLE_NAME,
                ACCOUNT_ID, PROSPECT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", getEntityType()),
                        ACCOUNT_ID)));
    }

    @Test
    @DisplayName("POST /api/accounts/{id}/prospects: account found and prospect added - Success")
    void createProspectWhenAccountFoundProspectTypePersonReturnSuccess() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        String PROSPECT_ID = getMockFactory().getProspectMockFactory().getComponentId();
        ProspectDTO postEntity = getMockFactory().getProspectMockFactory().newDTO(null);
        ProspectDTO mockedEntity = getMockFactory().getProspectMockFactory().newDTO(PROSPECT_ID, postEntity);

        // setup the mocked service
        doReturn(mockedEntity).when(service).createProspect(anyString(), any(ProspectDTO.class));

        // Execute the GET request
        mockMvc.perform(post(getResourceURI() + "/{id}/" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME, ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate common returned fields
                .andExpect(jsonPath("$.id", is(PROSPECT_ID)))
                .andExpect(jsonPath("$.person").exists());
    }

    @Test
    @DisplayName("POST /api/accounts/{id}/prospects: account found and prospect added - Success")
    void createProspectWhenAccountFoundProspectTypeProspectReturnSuccess() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        String PROSPECT_ID = getMockFactory().getProspectMockFactory().getComponentId();
        ProspectDTO postEntity = getMockFactory().getProspectMockFactory().newDTO(null);
        ProspectDTO mockedEntity = getMockFactory().getProspectMockFactory().newDTO(PROSPECT_ID, postEntity);

        // setup the mocked service
        doReturn(mockedEntity).when(service).createProspect(anyString(), any(ProspectDTO.class));

        // Execute the GET request
        mockMvc.perform(post(getResourceURI() + "/{id}/" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME + "/{type}", ACCOUNT_ID, ProspectTypeRequestParam.LEAD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate common returned fields
                .andExpect(jsonPath("$.id", is(PROSPECT_ID)))
                .andExpect(jsonPath("$.person").exists());

        ArgumentCaptor<ProspectDTO> prospectArgumentCaptor = ArgumentCaptor.forClass(ProspectDTO.class);
        verify(service, times(1)).createProspect(anyString(), prospectArgumentCaptor.capture());
        Assertions.assertEquals(
                ProspectStatusList.HOT.getStatus(),
                prospectArgumentCaptor.getValue().getStatus().getValue(),
                String.format("Status must be [%s]", ProspectStatusList.HOT));
    }

    @Test
    @DisplayName("POST /api/accounts/{id}/prospects: account not found - Failure")
    void createProspectWhenAccountNotFoundReturnsFailure() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        ProspectDTO postEntity = getMockFactory().getProspectMockFactory().newDTO(null);

        // setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ getEntityType(), String.valueOf(ACCOUNT_ID)});
        doThrow(exception).when(service).createProspect(anyString(), any(ProspectDTO.class));

        // Execute the GET request
        mockMvc.perform(post(getResourceURI() + "/{id}/" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME, ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", getEntityType()),
                        ACCOUNT_ID)));
    }

    @Test
    @DisplayName("POST /api/accounts/{id}/prospects: account not found - Failure")
    void createProspectWhenAccountFoundAndProspectInvalidReturnsFailure() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        ProspectDTO postEntity = getMockFactory().getProspectMockFactory().newDTO(null);
        postEntity.getPerson().setLastName(null);

        // Execute the GET request
        mockMvc.perform(post(getResourceURI() + "/{id}/" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME, ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    @Test
    @DisplayName("DELETE /api/accounts/{id}/notes: resource exists - Success")
    void deleteAccountNotesWhenResourceFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        AccountDTO mockedEntity = getMockFactory().newDTO(ACCOUNT_ID);
        mockedEntity.setNotes(null);

        // setup the mocked service
        doReturn(mockedEntity).when(service).deleteAccountNotes(anyString());

        // Execute the DELETE request
        mockMvc.perform(delete(getResourceURI() + "/{id}/notes", ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(ACCOUNT_ID)))
                .andExpect(jsonPath("$.notes").doesNotExist());
    }

    @Test
    @DisplayName("DELETE /api/accounts/{id}/summary: resource does not exists - Failure")
    void deleteAccountNotesWhenResourceNotFoundReturnsFailure() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();

        // setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ DBConstants.ACCOUNT_DOCUMENT, ACCOUNT_ID });

        // setup the mocked service
        doThrow(exception).when(service).deleteAccountNotes(anyString());

        // Execute the DELETE request
        mockMvc.perform(delete(getResourceURI() + "/{id}/notes", ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", getEntityType()),
                        ACCOUNT_ID)));
    }

    @Test
    @DisplayName("DELETE /api/accounts/{id}/company-notes: resource exists - Success")
    void deleteProspectCompanyCommentsWhenResourceFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        AccountDTO mockedEntity = getMockFactory().newDTO(ACCOUNT_ID);
        mockedEntity.setCompanyNotes(null);

        // setup the mocked service
        doReturn(mockedEntity).when(service).deleteAccountCompanyNotes(anyString());

        // Execute the DELETE request
        mockMvc.perform(delete(getResourceURI() + "/{id}/company-notes", ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(ACCOUNT_ID)))
                .andExpect(jsonPath("$.companyNotes").doesNotExist());
    }

    private List<ProspectDTO> createProspectList(int prospectListSize, int accountListSize, List<AccountFieldDTO> accountList) {
        return IntStream.range(0, prospectListSize)
                .mapToObj( (obj) -> {
                    String COMPONENT_ID = getMockFactory().getComponentId();
                    ProspectDTO prospect = getMockFactory().getProspectMockFactory().newDTO(COMPONENT_ID);
                    int prospectIdx = getMockFactory().getFAKER().number().numberBetween(0, accountListSize);
                    prospect.setAccount(accountList.get(prospectIdx));
                    prospect.setStatus(Status.builder().value(ProspectStatusList.DEAD.getStatus()).build());
                    return prospect;
                })
                .collect(Collectors.toList());
    }

}
