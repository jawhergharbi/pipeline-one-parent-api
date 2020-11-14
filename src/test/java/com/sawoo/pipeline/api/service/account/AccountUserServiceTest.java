package com.sawoo.pipeline.api.service.account;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.mock.AccountMockFactory;
import com.sawoo.pipeline.api.model.DataStoreConstants;
import com.sawoo.pipeline.api.model.User;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.repository.UserRepository;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
import com.sawoo.pipeline.api.service.base.BaseLightServiceTest;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountUserServiceTest extends BaseLightServiceTest<AccountDTO, Account, AccountRepository, AccountService, AccountMockFactory> {

    @MockBean
    private AccountRepository repository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    public AccountUserServiceTest(AccountMockFactory mockFactory, AccountService service) {
        super(mockFactory, DataStoreConstants.ACCOUNT_DOCUMENT, service);
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }

    @Test
    @DisplayName("findAllByUserId: entities found - Success")
    void findAllByUserIdWhenEntitiesFoundReturnsSuccess() {
        // Set up mock entities
        String USER_ID = getMockFactory().getFAKER().internet().uuid();
        String USER_EMAIL = getMockFactory().getFAKER().internet().emailAddress();
        User mockedUser = getMockFactory()
                .getUserMockFactory().newEntity(
                        USER_ID,
                        USER_EMAIL,
                        null,
                        new String[] {Role.MNG.name()});
        int listSize = 3;
        List<Account> entityList = IntStream.range(0, listSize)
                .mapToObj((entity) -> {
                    String COMPONENT_ID = getMockFactory().getFAKER().internet().uuid();
                    return getMockFactory().newEntity(COMPONENT_ID);
                }).collect(Collectors.toList());
        entityList.forEach((acc) -> acc.getUsers().add(mockedUser));

        // Set up the mocked repository
        doReturn(Optional.of(mockedUser)).when(userRepository).findById(anyString());
        doReturn(entityList).when(getRepository()).findByUserId(USER_ID);

        // Execute the service call
        List<AccountDTO> returnedList = getService().findAllByUser(USER_ID);

        Assertions.assertFalse(returnedList.isEmpty(), "Returned list can not be empty");
        Assertions.assertEquals(
                listSize,
                returnedList.size(),
                String.format("Returned list size must be %d", listSize));

        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(userRepository, atMostOnce()).findById(userIdCaptor.capture());

        Assertions.assertEquals(
                userIdCaptor.getValue(),
                USER_ID,
                String.format("User id to be verified must be: [%s]", USER_ID));
    }

    @Test
    @DisplayName("findAllByUserId: no entities found - Success")
    void findAlByUserIdWhenNoEntitiesFoundReturnsSuccess() {
        // Set up mock entities
        String USER_ID = getMockFactory().getFAKER().name().username();
        User mockedUser = getMockFactory().getUserMockFactory().newEntity(USER_ID);

        // Set up the mocked repository
        doReturn(Optional.of(mockedUser)).when(userRepository).findById(anyString());
        doReturn(Collections.emptyList()).when(getRepository()).findByUserId(anyString());

        // Execute the service call
        List<AccountDTO> returnedList = getService().findAllByUser(USER_ID);

        // Assertions
        Assertions.assertTrue(returnedList.isEmpty(), "Returned list must be empty");
    }

    @Test
    @DisplayName("findAllByUserId: user not found - Failure")
    void findAllByUserIdWhenUserNotFoundReturnsResourceNotFoundException() {
        // Set up mock entities
        String USER_ID = getMockFactory().getFAKER().name().username();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(userRepository).findById(anyString());

        // Execute the service call
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> getService().findAllByUser(USER_ID),
                "findAll must throw a ResourceNotFoundException");

        // Assertions
        Assertions.assertEquals(exception.getMessage(), ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);

        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(userRepository, atMostOnce()).findById(userIdCaptor.capture());

        Assertions.assertEquals(userIdCaptor.getValue(), USER_ID, String.format("User id to be verified must be: [%s]", USER_ID));
    }

    @Test
    @DisplayName("findAllByUserId: entities found - Success")
    void findAllByUSerIdWhenUserRoleADMINReturnsSuccess() {
        // Set up mock entities
        String USER_MGN_ID = getMockFactory().getFAKER().internet().uuid();
        User userMNG = getMockFactory()
                .getUserMockFactory()
                .newEntity(USER_MGN_ID, new String[]{Role.MNG.name(), Role.USER.name()});
        String USER_ADM_ID = getMockFactory().getFAKER().internet().uuid();
        User userADM = getMockFactory()
                .getUserMockFactory()
                .newEntity(USER_MGN_ID, new String[]{Role.ADMIN.name(), Role.USER.name()});
        int listSize = 3;
        List<Account> entityList = IntStream.range(0, listSize)
                .mapToObj((entity) -> {
                    String COMPONENT_ID = getMockFactory().getFAKER().internet().uuid();
                    return getMockFactory().newEntity(COMPONENT_ID);
                }).collect(Collectors.toList());
        entityList.get(0).getUsers().add(userADM);

        // Set up the mocked repository
        doReturn(Optional.of(userMNG))
                .doReturn(Optional.of(userADM))
                .when(userRepository).findById(anyString());
        doReturn(Collections.singletonList(entityList.get(0)))
                .when(getRepository()).findByUserId(anyString());
        doReturn(entityList)
                .when(getRepository()).findAll();

        // Execute the service call
        List<AccountDTO> returnedListCSM = getService().findAllByUser(USER_MGN_ID);
        List<AccountDTO> returnedListADMIN = getService().findAllByUser(USER_ADM_ID);

        Assertions.assertFalse(returnedListCSM.isEmpty(), "Returned list can not be empty");
        Assertions.assertEquals(1, returnedListCSM.size(), String.format("Returned list size must be %d", 1));

        Assertions.assertFalse(returnedListADMIN.isEmpty(), "Returned list can not be empty");
        Assertions.assertEquals(listSize, returnedListADMIN.size(), String.format("Returned list size must be %d", listSize));

        verify(userRepository, Mockito.times(2)).findById(any());
        verify(getRepository(), Mockito.atMostOnce()).findAll();
        verify(getRepository(), Mockito.atMostOnce()).findByUserId(anyString());
    }
}
