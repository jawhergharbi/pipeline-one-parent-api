package com.sawoo.pipeline.api.controller.account;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerTest;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.dto.account.AccountLeadDTO;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.lead.LeadTypeRequestParam;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.mock.AccountMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.common.Status;
import com.sawoo.pipeline.api.model.lead.LeadStatusList;
import com.sawoo.pipeline.api.model.user.UserRole;
import com.sawoo.pipeline.api.service.account.AccountService;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
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
    @DisplayName("GET /api/accounts/{id}/leads: get lead for account id returns list of leads - Success")
    void findAllLeadsWhenAccountFoundAndLeadsFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        int ID_LIST_SIZE = 3;
        List<LeadDTO> leadList = IntStream.range(0, ID_LIST_SIZE)
                .mapToObj((account) -> {
                    String COMPONENT_ID = getMockFactory().getComponentId();
                    return getMockFactory().getLeadMockFactory().newDTO(COMPONENT_ID);
                }).collect(Collectors.toList());

        // setup the mocked service
        doReturn(leadList).when(service).findAllLeads(ACCOUNT_ID);

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/leads", ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(ID_LIST_SIZE)));
    }

    @Test
    @DisplayName("GET /api/accounts/{id}/leads: get leads for account id returns an empty list - Success")
    void findAllLeadsWhenAccountFoundAndEmptyLeadListReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();

        // setup the mocked service
        doReturn(Collections.EMPTY_LIST).when(service).findAllLeads(ACCOUNT_ID);

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/leads", ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/accounts/{id}/leads: account not found - Failure")
    void findAllLeadsWhenAccountNotFoundReturnsFailure() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();

        // Setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ getEntityType(), String.valueOf(ACCOUNT_ID)});
        doThrow(exception).when(service).findAllLeads(ACCOUNT_ID);

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{id}/leads", ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", getEntityType()),
                        ACCOUNT_ID)));
    }

    @Test
    @DisplayName("GET /api/accounts/{ids}/leads/main: get lead list for a list of accounts returns an empty list - Success")
    void findAllLeadsWhenAccountsFoundAndNoLeadsFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        int ACCOUNT_LIST_SIZE = 2;
        List<String> ACCOUNT_IDS = IntStream.range(0, ACCOUNT_LIST_SIZE)
                .mapToObj( s -> getMockFactory().getComponentId() )
                .collect(Collectors.toList());
        // setup the mocked service
        doReturn(Collections.EMPTY_LIST)
                .when(service)
                .findAllLeads(ACCOUNT_IDS.toArray(String[]::new), null);

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{ids}/leads/main", String.join(",", ACCOUNT_IDS))
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/accounts/{ids}/leads/main: get lead list is found - Success")
    void findAllLeadsWhenAccountsFoundAndLeadsFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        int ACCOUNT_LIST_SIZE = 2;
        List<AccountLeadDTO> ACCOUNT_LIST = IntStream.range(0, ACCOUNT_LIST_SIZE)
                .mapToObj( (obj) -> {
                    String COMPONENT_ID = getMockFactory().getComponentId();
                    return getMockFactory().newLeadDTO(COMPONENT_ID);
                })
                .collect(Collectors.toList());
        List<String> ACCOUNT_IDS = ACCOUNT_LIST.stream().map(AccountLeadDTO::getId).collect(Collectors.toList());
        int LEAD_LIST_SIZE = 4;
        List<LeadDTO> LEAD_LIST = IntStream.range(0, LEAD_LIST_SIZE)
                .mapToObj( (obj) -> {
                    String COMPONENT_ID = getMockFactory().getComponentId();
                    LeadDTO lead = getMockFactory().getLeadMockFactory().newDTO(COMPONENT_ID);
                    int leadIdx = getMockFactory().getFAKER().number().numberBetween(0, ACCOUNT_LIST_SIZE);
                    lead.setAccount(ACCOUNT_LIST.get(leadIdx));
                    return lead;
                })
                .collect(Collectors.toList());


        // setup the mocked service
        doReturn(LEAD_LIST)
                .when(service)
                .findAllLeads(ACCOUNT_IDS.toArray(String[]::new), null);

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{ids}/leads/main", String.join(",", ACCOUNT_IDS))
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(LEAD_LIST_SIZE)))
                .andExpect(jsonPath("$.[0].account").exists())
                .andExpect(jsonPath("$.[0].id", is(LEAD_LIST.get(0).getId())));
    }

    @Test
    @DisplayName("GET /api/accounts/{ids}/leads/main: get lead list is found - Success")
    void findAllLeadsWhenAccountsFoundAndLeadStatusDeadAndLeadsFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        int ACCOUNT_LIST_SIZE = 1;
        List<AccountLeadDTO> ACCOUNT_LIST = IntStream.range(0, ACCOUNT_LIST_SIZE)
                .mapToObj( (obj) -> {
                    String COMPONENT_ID = getMockFactory().getComponentId();
                    return getMockFactory().newLeadDTO(COMPONENT_ID);
                })
                .collect(Collectors.toList());
        List<String> ACCOUNT_IDS = ACCOUNT_LIST.stream().map(AccountLeadDTO::getId).collect(Collectors.toList());
        int LEAD_LIST_SIZE = 3;
        List<LeadDTO> LEAD_LIST = IntStream.range(0, LEAD_LIST_SIZE)
                .mapToObj( (obj) -> {
                    String COMPONENT_ID = getMockFactory().getComponentId();
                    LeadDTO lead = getMockFactory().getLeadMockFactory().newDTO(COMPONENT_ID);
                    int leadIdx = getMockFactory().getFAKER().number().numberBetween(0, ACCOUNT_LIST_SIZE);
                    lead.setAccount(ACCOUNT_LIST.get(leadIdx));
                    lead.setStatus(Status.builder().value(LeadStatusList.DEAD.getStatus()).build());
                    return lead;
                })
                .collect(Collectors.toList());


        // setup the mocked serviceº
        doReturn(LEAD_LIST)
                .when(service)
                .findAllLeads(ACCOUNT_IDS.toArray(String[]::new), new Integer[] {LeadStatusList.DEAD.getStatus()});

        // Execute the GET request
        mockMvc.perform(get(getResourceURI() + "/{ids}/leads/main", String.join(",", ACCOUNT_IDS))
                .contentType(MediaType.APPLICATION_JSON)
                .param("status", String.valueOf(LeadStatusList.DEAD.getStatus())))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(LEAD_LIST_SIZE)))
                .andExpect(jsonPath("$.[0].account").exists())
                .andExpect(jsonPath("$.[0].id", is(LEAD_LIST.get(0).getId())))
                .andExpect(jsonPath("$.[0].status.value", is(LeadStatusList.DEAD.getStatus())));
    }

    @Test
    @DisplayName("DELETE /api/accounts/{id}/leads: lead found - Success")
    void removeLeadWhenAccountFoundAndLeadFoundReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        String LEAD_ID = getMockFactory().getLeadMockFactory().getComponentId();
        LeadDTO mockedLead = getMockFactory().getLeadMockFactory().newDTO(LEAD_ID);

        // setup the mocked service
        doReturn(mockedLead).when(service).removeLead(anyString(), anyString());

        // Execute the GET request
        mockMvc.perform(delete(getResourceURI() + "/{id}/leads/{leadId}", ACCOUNT_ID, LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate common returned fields
                .andExpect(jsonPath("$.id", is(LEAD_ID)))
                .andExpect(jsonPath("$.person").exists());
    }

    @Test
    @DisplayName("DELETE /api/accounts/{id}/leads: lead not found - Failure")
    void removeLeadWhenLeadNotFoundReturnsFailure() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        String LEAD_ID = getMockFactory().getLeadMockFactory().getComponentId();

        // setup the mocked service
        CommonServiceException exception = new  CommonServiceException(
                ExceptionMessageConstants.ACCOUNT_LEAD_REMOVE_LEAD_NOT_FOUND_EXCEPTION,
                new String[] {ACCOUNT_ID, LEAD_ID});
        doThrow(exception).when(service).removeLead(anyString(), anyString());

        // Execute the GET request
        mockMvc.perform(delete(getResourceURI() + "/{id}/leads/{leadId}", ACCOUNT_ID, LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and content type
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", stringContainsInOrder("Lead not found in the account lead list")));
    }

    @Test
    @DisplayName("DELETE /api/accounts/{id}/leads: account not found - Failure")
    void removeLeadWhenAccountNotFoundReturnsFailure() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        String LEAD_ID = getMockFactory().getLeadMockFactory().getComponentId();

        // setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ getEntityType(), String.valueOf(ACCOUNT_ID)});
        doThrow(exception).when(service).removeLead(anyString(), anyString());

        // Execute the GET request
        mockMvc.perform(delete(getResourceURI() + "/{id}/leads/{leadId}", ACCOUNT_ID, LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", getEntityType()),
                        ACCOUNT_ID)));
    }

    @Test
    @DisplayName("POST /api/accounts/{id}/leads: account found and lead added - Success")
    void createLeadWhenAccountFoundLeadTypePersonReturnSuccess() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        String LEAD_ID = getMockFactory().getLeadMockFactory().getComponentId();
        LeadDTO postEntity = getMockFactory().getLeadMockFactory().newDTO(null);
        LeadDTO mockedEntity = getMockFactory().getLeadMockFactory().newDTO(LEAD_ID, postEntity);

        // setup the mocked service
        doReturn(mockedEntity).when(service).createLead(anyString(), any(LeadDTO.class));

        // Execute the GET request
        mockMvc.perform(post(getResourceURI() + "/{id}/leads", ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate common returned fields
                .andExpect(jsonPath("$.id", is(LEAD_ID)))
                .andExpect(jsonPath("$.person").exists());
    }

    @Test
    @DisplayName("POST /api/accounts/{id}/leads: account found and lead added - Success")
    void createLeadWhenAccountFoundLeadTypeLeadReturnSuccess() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        String LEAD_ID = getMockFactory().getLeadMockFactory().getComponentId();
        LeadDTO postEntity = getMockFactory().getLeadMockFactory().newDTO(null);
        LeadDTO mockedEntity = getMockFactory().getLeadMockFactory().newDTO(LEAD_ID, postEntity);

        // setup the mocked service
        doReturn(mockedEntity).when(service).createLead(anyString(), any(LeadDTO.class));

        // Execute the GET request
        mockMvc.perform(post(getResourceURI() + "/{id}/leads/{type}", ACCOUNT_ID, LeadTypeRequestParam.LEAD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate common returned fields
                .andExpect(jsonPath("$.id", is(LEAD_ID)))
                .andExpect(jsonPath("$.person").exists());

        ArgumentCaptor<LeadDTO> leadArgumentCaptor = ArgumentCaptor.forClass(LeadDTO.class);
        verify(service, times(1)).createLead(anyString(), leadArgumentCaptor.capture());
        Assertions.assertEquals(
                LeadStatusList.HOT.getStatus(),
                leadArgumentCaptor.getValue().getStatus().getValue(),
                String.format("Status must be [%s]", LeadStatusList.HOT));
    }

    @Test
    @DisplayName("POST /api/accounts/{id}/leads: account not found - Failure")
    void createLeadWhenAccountNotFoundReturnsFailure() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        LeadDTO postEntity = getMockFactory().getLeadMockFactory().newDTO(null);

        // setup the mocked service
        ResourceNotFoundException exception = new ResourceNotFoundException(
                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                new String[]{ getEntityType(), String.valueOf(ACCOUNT_ID)});
        doThrow(exception).when(service).createLead(anyString(), any(LeadDTO.class));

        // Execute the GET request
        mockMvc.perform(post(getResourceURI() + "/{id}/leads", ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        String.format("GET operation. Component type [%s]", getEntityType()),
                        ACCOUNT_ID)));
    }

    @Test
    @DisplayName("POST /api/accounts/{id}/leads: account not found - Failure")
    void createLeadWhenAccountFoundAndLeadInvalidReturnsFailure() throws Exception {
        // Setup the mocked entities
        String ACCOUNT_ID = getMockFactory().getComponentId();
        LeadDTO postEntity = getMockFactory().getLeadMockFactory().newDTO(null);
        postEntity.getPerson().setLastName(null);

        // Execute the GET request
        mockMvc.perform(post(getResourceURI() + "/{id}/leads", ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }
}
