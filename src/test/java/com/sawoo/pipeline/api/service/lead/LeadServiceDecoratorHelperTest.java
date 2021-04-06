package com.sawoo.pipeline.api.service.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.UserCommon;
import com.sawoo.pipeline.api.mock.AccountMockFactory;
import com.sawoo.pipeline.api.mock.UserMockFactory;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.user.User;
import com.sawoo.pipeline.api.model.user.UserRole;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
class LeadServiceDecoratorHelperTest {

    @Getter
    private final UserMockFactory userMockFactory;
    @Getter
    private final AccountMockFactory accountMockFactory;
    @Getter
    private final LeadServiceDecoratorHelper leadServiceHelper;

    @MockBean
    private AccountRepository accountRepository;

    @Autowired
    public LeadServiceDecoratorHelperTest(LeadServiceDecoratorHelper leadServiceHelper, AccountMockFactory accountMockFactory, UserMockFactory userMockFactory) {
        this.userMockFactory = userMockFactory;
        this.accountMockFactory = accountMockFactory;
        this.leadServiceHelper = leadServiceHelper;
    }

    @Test
    @DisplayName("getUsers: account found and there are linked users to the account - Success")
    void getUsersWhenAccountFoundReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getAccountMockFactory().getFAKER().internet().uuid();
        String ACCOUNT_ID = getAccountMockFactory().getComponentId();
        int USERS_SIZE = 3;
        Account accountEntity = getAccountMockFactory().newEntity(ACCOUNT_ID);
        List<User> userEntities = getUserList(USERS_SIZE);
        accountEntity.getUsers().addAll(userEntities);

        // Set up the mocked repository
        doReturn(Optional.of(accountEntity)).when(accountRepository).findByLeadId(anyString());

        // Execute the call
        List<UserCommon> users = leadServiceHelper.getUsers(LEAD_ID);

        // Assertions
        Assertions.assertAll("List of users is correct",
                () -> Assertions.assertFalse(users.isEmpty(), "List of user can not be empty"),
                () -> Assertions.assertEquals(USERS_SIZE, users.size(), String.format("User list size must be [%d]", USERS_SIZE)));
    }

    @Test
    @DisplayName("getUsers: account found and there are no linked users to the account - Success")
    void getUsersWhenAccountFoundAndNoUsersReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getAccountMockFactory().getFAKER().internet().uuid();
        String ACCOUNT_ID = getAccountMockFactory().getComponentId();
        Account accountEntity = getAccountMockFactory().newEntity(ACCOUNT_ID);

        // Set up the mocked repository
        doReturn(Optional.of(accountEntity)).when(accountRepository).findByLeadId(anyString());

        // Execute the call
        List<UserCommon> users = leadServiceHelper.getUsers(LEAD_ID);

        // Assertions
        Assertions.assertAll("List of users is correct",
                () -> Assertions.assertTrue(users.isEmpty(), "List of user must be empty"));
    }

    @Test
    @DisplayName("getUsers: account not found - Failure")
    void getUsersWhenAccountNotFoundReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = getAccountMockFactory().getFAKER().internet().uuid();

        // Set up the mocked repository
        doReturn(Optional.empty()).when(accountRepository).findByLeadId(anyString());

        // Execute the call
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> leadServiceHelper.getUsers(LEAD_ID),
                "getUsers must throw CommonServiceException");

        // Assertions
        Assertions.assertEquals(
                ExceptionMessageConstants.LEAD_LEAD_ACCOUNT_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(1, exception.getArgs().length);

        verify(accountRepository, times(1)).findByLeadId(anyString());
    }

    @Test
    @DisplayName("getAssignee: account found, assigneeId not null and assignee found - Success")
    void getAssigneeWhenAccountFoundAssigneeIdNotNullAndAssigneeFoundReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getAccountMockFactory().getFAKER().internet().uuid();
        String ASSIGNEE_ID = getAccountMockFactory().getFAKER().internet().uuid();
        String ACCOUNT_ID = getAccountMockFactory().getComponentId();
        Account accountEntity = getAccountMockFactory().newEntity(ACCOUNT_ID);
        User userAssignee = newUser(ASSIGNEE_ID, UserRole.AST);
        accountEntity.getUsers().add(userAssignee);

        // Set up the mocked repository
        doReturn(Optional.of(accountEntity)).when(accountRepository).findByLeadId(anyString());

        // Execute the call
        UserCommon user = leadServiceHelper.getAssignee(LEAD_ID, ASSIGNEE_ID);

        // Assertions
        Assertions.assertNotNull(user, "User can not be null");
        Assertions.assertEquals(ASSIGNEE_ID, user.getId(), String.format("User id must be [%s]", ASSIGNEE_ID));
        verify(accountRepository, times(1)).findByLeadId(anyString());
    }

    @Test
    @DisplayName("getAssignee: account found, assigneeId not null and assignee not found - Failure")
    void getAssigneeWhenAccountFoundAssigneeIdNotNullAndAssigneeNotFoundReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = getAccountMockFactory().getFAKER().internet().uuid();
        String ASSIGNEE_ID = getAccountMockFactory().getFAKER().internet().uuid();
        String ANOTHER_ASSIGNEE_ID = getAccountMockFactory().getFAKER().internet().uuid();
        String ACCOUNT_ID = getAccountMockFactory().getComponentId();
        Account accountEntity = getAccountMockFactory().newEntity(ACCOUNT_ID);
        User userAssignee = newUser(ASSIGNEE_ID, UserRole.AST);
        accountEntity.getUsers().add(userAssignee);

        // Set up the mocked repository
        doReturn(Optional.of(accountEntity)).when(accountRepository).findByLeadId(anyString());

        // Execute the call
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> leadServiceHelper.getAssignee(LEAD_ID, ANOTHER_ASSIGNEE_ID),
                "getAssignee must throw CommonServiceException");

        // Assertions
        Assertions.assertEquals(
                ExceptionMessageConstants.LEAD_LEAD_ACCOUNT_ASSIGNEE_NOT_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(accountRepository, times(1)).findByLeadId(anyString());
    }

    @Test
    @DisplayName("getAssignee: account found, assigneeId null and assignee assistant found - Success")
    void getAssigneeWhenAccountFoundAssigneeIdNullAndAssigneeAssistantFoundReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getAccountMockFactory().getFAKER().internet().uuid();
        String ASSIGNEE_AST_ID = getAccountMockFactory().getFAKER().internet().uuid();
        String ASSIGNEE_USER_SALES_ID = getAccountMockFactory().getFAKER().internet().uuid();
        String ACCOUNT_ID = getAccountMockFactory().getComponentId();
        Account accountEntity = getAccountMockFactory().newEntity(ACCOUNT_ID);
        User userAssignee = newUser(ASSIGNEE_AST_ID, UserRole.AST);
        accountEntity.getUsers().add(userAssignee);
        User userSalesAssignee = newUser(ASSIGNEE_USER_SALES_ID, UserRole.CLIENT);
        accountEntity.getUsers().add(userSalesAssignee);

        // Set up the mocked repository
        doReturn(Optional.of(accountEntity)).when(accountRepository).findByLeadId(anyString());

        // Execute the call
        UserCommon user = leadServiceHelper.getAssignee(LEAD_ID, null);

        // Assertions
        Assertions.assertNotNull(user, "User can not be null");
        Assertions.assertEquals(ASSIGNEE_AST_ID, user.getId(), String.format("User id must be [%s]", ASSIGNEE_AST_ID));
        verify(accountRepository, times(1)).findByLeadId(anyString());
    }

    @Test
    @DisplayName("getAssignee: account found, assigneeId null and  sales user found - Success")
    void getAssigneeWhenAccountFoundAssigneeIdNullAndSalesUserFoundReturnsSuccess() {
        // Set up mocked entities
        String LEAD_ID = getAccountMockFactory().getFAKER().internet().uuid();
        String ASSIGNEE_USER_SALES_ID = getAccountMockFactory().getFAKER().internet().uuid();
        String ACCOUNT_ID = getAccountMockFactory().getComponentId();
        Account accountEntity = getAccountMockFactory().newEntity(ACCOUNT_ID);
        User userSalesAssignee = newUser(ASSIGNEE_USER_SALES_ID, UserRole.CLIENT);
        accountEntity.getUsers().add(userSalesAssignee);

        // Set up the mocked repository
        doReturn(Optional.of(accountEntity)).when(accountRepository).findByLeadId(anyString());

        // Execute the call
        UserCommon user = leadServiceHelper.getAssignee(LEAD_ID, null);

        // Assertions
        Assertions.assertNotNull(user, "User can not be null");
        Assertions.assertEquals(ASSIGNEE_USER_SALES_ID, user.getId(), String.format("User id must be [%s]", ASSIGNEE_USER_SALES_ID));
        verify(accountRepository, times(1)).findByLeadId(anyString());
    }

    @Test
    @DisplayName("getAssignee: account found, assigneeId null and  sales user found - Failure")
    void getAssigneeWhenAccountFoundAssigneeIdNullAndNoAssigneeFoundReturnsFailure() {
        // Set up mocked entities
        String LEAD_ID = getAccountMockFactory().getFAKER().internet().uuid();
        String ACCOUNT_ID = getAccountMockFactory().getComponentId();
        int USERS_SIZE = 3;
        Account accountEntity = getAccountMockFactory().newEntity(ACCOUNT_ID);
        List<User> userEntities = getUserList(USERS_SIZE);
        accountEntity.getUsers().addAll(userEntities);

        // Set up the mocked repository
        doReturn(Optional.of(accountEntity)).when(accountRepository).findByLeadId(anyString());

        // Execute the call
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> leadServiceHelper.getAssignee(LEAD_ID, null),
                "getAssignee must throw CommonServiceException");

        // Assertions
        Assertions.assertEquals(
                ExceptionMessageConstants.LEAD_LEAD_ACCOUNT_NO_ASSIGNEE_FOUND_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(1, exception.getArgs().length);

        verify(accountRepository, times(1)).findByLeadId(anyString());
    }

    private User newUser(String id, UserRole role) {
        User userAssignee = getUserMockFactory().newEntity(id);
        userAssignee.getRoles().add(role.name());
        return userAssignee;
    }

    private List<User> getUserList(int userSize) {
        return IntStream.range(0, userSize)
                .mapToObj(u -> {
                    String COMPONENT_ID = getUserMockFactory().getComponentId();
                    return getUserMockFactory().newEntity(COMPONENT_ID);
                }).collect(Collectors.toList());
    }
}
