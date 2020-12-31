package com.sawoo.pipeline.api.service.account;

import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.mock.AccountMockFactory;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.user.User;
import com.sawoo.pipeline.api.model.user.UserRole;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;
import java.util.HashSet;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class AccountServiceEventListenerTest {

    @Autowired
    private AccountServiceEventListener listener;

    @Autowired
    private AccountMockFactory mockFactory;

    @Test
    @DisplayName("onBeforeUpdate: account does not have any user - Success")
    void onBeforeUpdateWhenAccountDoesNotHaveAnyUserReturnsSuccess() {
        // Set up mocked entities
        String ACCOUNT_ID = mockFactory.getComponentId();
        Account entity = mockFactory.newEntity(ACCOUNT_ID);
        String USER_ID = mockFactory.getUserMockFactory().getComponentId();
        UserAuthDTO user = mockFactory.getUserMockFactory().newDTO(USER_ID);
        user.setRoles(new HashSet<>(Arrays.asList(UserRole.MNG.name(), UserRole.USER.name())));
        AccountDTO postDTO = new AccountDTO();
        postDTO.setId(ACCOUNT_ID);
        postDTO.getUsers().add(user);

        // Execute the service call
        listener.onBeforeUpdate(postDTO, entity);

        Assertions.assertAll(
                String.format("User list can not be empty, size must be [%d] and the default role by the user must be [%s]",
                        1, UserRole.MNG.name()),
                () -> Assertions.assertFalse(entity.getUsers().isEmpty(), "List of user can not be empty"),
                () -> Assertions.assertEquals(1, entity.getUsers().size(), String.format("User list size must be [%s]", 1)),
                () -> {
                    User u = entity.getUsers().iterator().next();
                    Assertions.assertEquals(
                            UserRole.MNG,
                            UserRole.getDefaultRole(u.getRoles()),
                            String.format("User id: [%s] default role have to be [%s]" , UserRole.MNG.name(), u.getId()));
                });
    }

    @Test
    @DisplayName("onBeforeUpdate: account does have already the user to be added - Success")
    void onBeforeUpdateWhenAccountIncludesAlreadyTheUserReturnsSuccess() {
        // Set up mocked entities
        String ACCOUNT_ID = mockFactory.getComponentId();
        Account entity = mockFactory.newEntity(ACCOUNT_ID);
        String USER_ID = mockFactory.getUserMockFactory().getComponentId();
        User user = mockFactory.getUserMockFactory().newEntity(USER_ID);
        entity.getUsers().add(user);

        UserAuthDTO newUser = mockFactory.getUserMockFactory().newDTO(USER_ID);
        AccountDTO postDTO = new AccountDTO();
        postDTO.setId(ACCOUNT_ID);
        postDTO.getUsers().add(newUser);

        // Execute the service call
        listener.onBeforeUpdate(postDTO, entity);

        Assertions.assertAll(
                String.format("User list can not be empty, size must be [%d] and the default role by the user must be [%s]",
                        1, UserRole.MNG.name()),
                () -> Assertions.assertFalse(entity.getUsers().isEmpty(), "List of user can not be empty"),
                () -> Assertions.assertEquals(1, entity.getUsers().size(), String.format("User list size must be [%s]", 1)),
                () -> {
                    User u = entity.getUsers().iterator().next();
                    Assertions.assertEquals(
                            USER_ID,
                            u.getId(),
                            String.format("User id must be [%s]", u.getId()));
                });
    }

    @Test
    @DisplayName("onBeforeUpdate: account does have one user and we add another one with a different default role - Success")
    void onBeforeUpdateWhenAddUserWithNewRoleReturnsSuccess() {
        // Set up mocked entities
        String ACCOUNT_ID = mockFactory.getComponentId();
        Account entity = mockFactory.newEntity(ACCOUNT_ID);
        String USER_ID = mockFactory.getUserMockFactory().getComponentId();
        User user = mockFactory.getUserMockFactory().newEntity(USER_ID);
        user.setRoles(new HashSet<>(Arrays.asList(UserRole.MNG.name(), UserRole.USER.name())));
        entity.getUsers().add(user);

        UserAuthDTO newUser = mockFactory.getUserMockFactory().newDTO(mockFactory.getFAKER().internet().uuid());
        newUser.setRoles(new HashSet<>(Arrays.asList(UserRole.AST.name(), UserRole.USER.name())));
        AccountDTO postDTO = new AccountDTO();
        postDTO.setId(ACCOUNT_ID);
        postDTO.getUsers().add(newUser);

        // Execute the service call
        listener.onBeforeUpdate(postDTO, entity);

        Assertions.assertAll(
                String.format("User list can not be empty, size must be [%d] and the default role by the user must be [%s]",
                        1, UserRole.MNG.name()),
                () -> Assertions.assertFalse(entity.getUsers().isEmpty(), "List of user can not be empty"),
                () -> Assertions.assertEquals(2, entity.getUsers().size(), String.format("User list size must be [%s]", 2))
        );
    }

    @Test
    @DisplayName("onBeforeUpdate: replace an existing user because they both hold the same default role - Success")
    void onBeforeUpdateWhenUserReplacedReturnsSuccess() {
        // Set up mocked entities
        String ACCOUNT_ID = mockFactory.getComponentId();
        Account entity = mockFactory.newEntity(ACCOUNT_ID);
        String USER_ID = mockFactory.getUserMockFactory().getComponentId();
        User user = mockFactory.getUserMockFactory().newEntity(USER_ID);
        user.setRoles(new HashSet<>(Arrays.asList(UserRole.MNG.name(), UserRole.USER.name())));
        entity.getUsers().add(user);

        String NEW_USER_ID = mockFactory.getFAKER().internet().uuid();
        UserAuthDTO newUser = mockFactory.getUserMockFactory().newDTO(NEW_USER_ID);
        newUser.setRoles(new HashSet<>(Arrays.asList(UserRole.MNG.name(), UserRole.USER.name())));
        AccountDTO postDTO = new AccountDTO();
        postDTO.setId(ACCOUNT_ID);
        postDTO.getUsers().add(newUser);

        // Execute the service call
        listener.onBeforeUpdate(postDTO, entity);

        Assertions.assertAll(
                String.format("User list can not be empty, size must be [%d] and the default role by the user must be [%s]",
                        1, UserRole.MNG.name()),
                () -> Assertions.assertFalse(entity.getUsers().isEmpty(), "List of user can not be empty"),
                () -> Assertions.assertEquals(1, entity.getUsers().size(), String.format("User list size must be [%s]", 1)),
                () -> {
                    User u = entity.getUsers().iterator().next();
                    Assertions.assertEquals(
                            NEW_USER_ID,
                            u.getId(),
                            String.format("User id must be [%s]", u.getId()));
                });
    }
}
