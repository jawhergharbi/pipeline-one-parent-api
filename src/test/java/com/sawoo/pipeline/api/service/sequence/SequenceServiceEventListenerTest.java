package com.sawoo.pipeline.api.service.sequence;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.dto.sequence.SequenceUserDTO;
import com.sawoo.pipeline.api.mock.SequenceMockFactory;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.model.sequence.SequenceUser;
import com.sawoo.pipeline.api.model.sequence.SequenceUserType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class SequenceServiceEventListenerTest {

    @Autowired
    private SequenceServiceEventListener listener;

    @Autowired
    private SequenceMockFactory mockFactory;

    @Autowired
    private SequenceMapper mapper;

    @Test
    @DisplayName("onBeforeInsert: sequence contains user - Success")
    void onBeforeInsertWhenSequenceContainsUsersWithoutTimestampsReturnsSuccess() {
        // Set up mocked entities
        String SEQUENCE_ID = mockFactory.getComponentId();
        Sequence entity = mockFactory.newEntity(SEQUENCE_ID);

        String USER_ID = mockFactory.getFAKER().internet().uuid();
        SequenceUserDTO user = SequenceUserDTO.builder().userId(USER_ID).type(SequenceUserType.OWNER).build();
        SequenceDTO postDTO = mockFactory.newDTO(null);
        postDTO.setUsers(new HashSet<>(Collections.singleton(user)));

        // Execute the service call
        listener.onBeforeInsert(postDTO, entity);

        // Assertions
        SequenceUserDTO u = postDTO.getUsers().iterator().next();
        Assertions.assertEquals(
                SequenceUserType.OWNER,
                u.getType(),
                String.format("User id: [%s] default type have to be [%s]", u.getUserId(), SequenceUserType.OWNER));
        Assertions.assertNotNull(u.getCreated(), "'created' timestamp can not be null");
        Assertions.assertNotNull(u.getUpdated(), "'updated' timestamp can not be null");
    }

    @Test
    @DisplayName("onBeforeInsert: sequence contains user but user type owner is not present - Failure")
    void onBeforeInsertWhenSequenceContainsUsersAndUserIsNotOwnerTypeReturnsFailure() {
        // Set up mocked entities
        String USER_ID = mockFactory.getFAKER().internet().uuid();
        SequenceUserDTO user = SequenceUserDTO.builder().userId(USER_ID).type(SequenceUserType.EDITOR).build();
        SequenceDTO postDTO = mockFactory.newDTO(null);
        postDTO.setUsers(new HashSet<>(Collections.singleton(user)));
        Sequence entity = mapper.getMapperIn().getDestination(postDTO);

        // Execute the service call
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () ->  listener.onBeforeInsert(postDTO, entity),
                "create must throw a CommonServiceException");

        // Assertions
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.SEQUENCE_CREATE_USER_OWNER_NOT_SPECIFIED_EXCEPTION)
                        .matches(exception.getMessage()));
    }

    @Test
    @DisplayName("onBeforeSave: sequence contains user, update timestamp should be updated - Success")
    void onBeforeSaveWhenSequenceContainsUsersReturnsSuccess() {
        // Set up mocked entities
        String SEQUENCE_ID = mockFactory.getComponentId();
        Sequence entity = mockFactory.newEntity(SEQUENCE_ID);
        String USER_ID = mockFactory.getFAKER().internet().uuid();
        SequenceUser user = SequenceUser.builder()
                .userId(USER_ID)
                .type(SequenceUserType.OWNER)
                .created(LocalDateTime.now(ZoneOffset.UTC).minusDays(10))
                .updated(LocalDateTime.now(ZoneOffset.UTC).minusDays(2))
                .build();
        entity.getUsers().add(user);

        SequenceUserDTO userDTO = SequenceUserDTO.builder()
                .userId(USER_ID)
                .type(SequenceUserType.EDITOR)
                .build();
        SequenceDTO postDTO = mockFactory.newDTO(null);
        postDTO.setUsers(new HashSet<>(Collections.singleton(userDTO)));

        // Execute the service call
        listener.onBeforeSave(postDTO, entity);

        // Assertions
        SequenceUserDTO u = postDTO.getUsers().iterator().next();
        Assertions.assertNotNull(u.getCreated(), "'created' timestamp can not be null");
        Assertions.assertNotNull(u.getUpdated(), "'updated' timestamp can not be null");
        Assertions.assertEquals(
                0,
                u.getUpdated().toLocalDate().compareTo(LocalDate.now(ZoneOffset.UTC)),
                "Updated timestamp should be changed");
    }

    @Test
    @DisplayName("onBeforeUpdate: sequence does not have any user - Success")
    void onBeforeUpdateWhenSequenceDoesNotHaveAnyUserReturnsSuccess() {
        // Set up mocked entities
        int USERS_SIZE = 1;
        String SEQUENCE_ID = mockFactory.getComponentId();
        Sequence entity = mockFactory.newEntity(SEQUENCE_ID);

        String USER_ID = mockFactory.getFAKER().internet().uuid();
        SequenceUserDTO user = SequenceUserDTO.builder().userId(USER_ID).type(SequenceUserType.OWNER).build();
        SequenceDTO postDTO = new SequenceDTO();
        postDTO.setId(SEQUENCE_ID);
        postDTO.setUsers(new HashSet<>(Collections.singleton(user)));

        // Execute the service call
        listener.onBeforeUpdate(postDTO, entity);

        // Assertions
        Assertions.assertAll(
                String.format("User list can not be empty, size must be [%d] and the type by the user must be [%s]",
                        USERS_SIZE, SequenceUserType.OWNER),
                () -> Assertions.assertFalse(entity.getUsers().isEmpty(), "List of users can not be empty"),
                () -> Assertions.assertEquals(USERS_SIZE, entity.getUsers().size(), String.format("User list size must be [%s]", USERS_SIZE)),
                () -> {
                    SequenceUser u = entity.getUsers().iterator().next();
                    Assertions.assertNotNull(u.getCreated(), "'created' timestamp can not be null");
                    Assertions.assertNotNull(u.getUpdated(), "'updated' timestamp can not be null");
                });
    }

    @Test
    @DisplayName("onBeforeUpdate: add user without id - Failure")
    void onBeforeUpdateWhenAddUserWhenUserIdNotInformedReturnsFailure() {
        // Set up mocked entities
        String SEQUENCE_ID = mockFactory.getComponentId();
        Sequence entity = mockFactory.newEntity(SEQUENCE_ID);

        SequenceDTO postDTO = SequenceDTO.builder().id(SEQUENCE_ID).build();
        SequenceUserDTO newUser = SequenceUserDTO.builder()
                .type(SequenceUserType.VIEWER)
                .toBeDeleted(true)
                .build();
        postDTO.setUsers(new HashSet<>(Collections.singleton(newUser)));

        // Execute the service call
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> listener.onBeforeUpdate(postDTO, entity),
                "onBeforeUpdate must throw an update must throw an CommonServiceException");
        Assertions.assertEquals(
                exception.getMessage(),
                ExceptionMessageConstants.SEQUENCE_UPDATE_USER_ID_NOT_INFORMED_EXCEPTION);
        Assertions.assertEquals(1, exception.getArgs().length);
    }

    @Test
    @DisplayName("onBeforeUpdate: sequence does have an owner - Success")
    void onBeforeUpdateWhenSequenceDoesHaveAnUserOwnerReturnsSuccess() {
        // Set up mocked entities
        int USERS_SIZE = 2;
        String SEQUENCE_ID = mockFactory.getComponentId();
        String USER_ID = mockFactory.getFAKER().internet().uuid();
        Sequence entity = createSequenceEntityWithOwner(SEQUENCE_ID, USER_ID);

        SequenceDTO postDTO = new SequenceDTO();
        String NEW_USER_ID = mockFactory.getFAKER().internet().uuid();
        SequenceUserDTO newUser = SequenceUserDTO.builder()
                .userId(NEW_USER_ID)
                .type(SequenceUserType.OWNER)
                .build();
        postDTO.setId(SEQUENCE_ID);
        postDTO.setUsers(new HashSet<>(Collections.singleton(newUser)));

        // Execute the service call
        listener.onBeforeUpdate(postDTO, entity);

        // Assertions
        Set<SequenceUser> users = entity.getUsers();
        Assertions.assertFalse(users.isEmpty(), "User list can not be empty");
        Assertions.assertEquals(USERS_SIZE, users.size(), String.format("Users size must be [%d]", USERS_SIZE));
        Assertions.assertTrue(users.stream().anyMatch(
                u -> u.getType().equals(SequenceUserType.EDITOR)),
                String.format("There must be a user of type [%s]", SequenceUserType.EDITOR));
        Optional<SequenceUser> owner = users.stream().filter(u -> u.getType().equals(SequenceUserType.OWNER)).findFirst();
        Assertions.assertTrue(owner.isPresent(), "");
        owner.ifPresent(u -> Assertions.assertEquals(
                NEW_USER_ID,
                u.getUserId(),
                String.format("Owner user must be user with id [%s]", NEW_USER_ID)));
    }

    @Test
    @DisplayName("onBeforeUpdate: delete user but the sequence's owner - Failure")
    void onBeforeUpdateWhenDeleteUserAndSequenceDoesHaveAnUserOwnerReturnsFailure() {
        String SEQUENCE_ID = mockFactory.getComponentId();
        String USER_ID = mockFactory.getFAKER().internet().uuid();
        Sequence entity = createSequenceEntityWithOwner(SEQUENCE_ID, USER_ID);

        SequenceDTO postDTO = SequenceDTO.builder().id(SEQUENCE_ID).build();
        SequenceUserDTO newUser = SequenceUserDTO.builder()
                .userId(USER_ID)
                .toBeDeleted(true)
                .build();
        postDTO.setUsers(new HashSet<>(Collections.singleton(newUser)));

        // Execute the service call
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> listener.onBeforeUpdate(postDTO, entity),
                "onBeforeUpdate must throw an update must throw an CommonServiceException");
        Assertions.assertEquals(
                exception.getMessage(),
                ExceptionMessageConstants.SEQUENCE_UPDATE_DELETE_USER_OWNER_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);
    }

    @Test
    @DisplayName("onBeforeUpdate: delete user and the sequence does not contain the user id - Failure")
    void onBeforeUpdateWhenDeleteUserAndSequenceDoesNotContainUserReturnsFailure() {
        String SEQUENCE_ID = mockFactory.getComponentId();
        String USER_ID = mockFactory.getFAKER().internet().uuid();
        Sequence entity = createSequenceEntityWithOwner(SEQUENCE_ID, USER_ID);

        SequenceDTO postDTO = SequenceDTO.builder().id(SEQUENCE_ID).build();
        String ANOTHER_USER_ID = mockFactory.getFAKER().internet().uuid();
        SequenceUserDTO newUser = SequenceUserDTO.builder()
                .userId(ANOTHER_USER_ID)
                .toBeDeleted(true)
                .build();
        postDTO.setUsers(new HashSet<>(Collections.singleton(newUser)));

        // Execute the service call
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> listener.onBeforeUpdate(postDTO, entity),
                "onBeforeUpdate must throw an update must throw an CommonServiceException");
        Assertions.assertEquals(
                exception.getMessage(),
                ExceptionMessageConstants.SEQUENCE_UPDATE_DELETE_USER_NOT_FOUND_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);
    }

    @Test
    @DisplayName("onBeforeUpdate: delete user and the sequence does have users - Failure")
    void onBeforeUpdateWhenDeleteUserAndSequenceDoesNotHaveUsersReturnsFailure() {
        String SEQUENCE_ID = mockFactory.getComponentId();
        Sequence entity = mockFactory.newEntity(SEQUENCE_ID);

        String USER_ID = mockFactory.getFAKER().internet().uuid();
        SequenceDTO postDTO = SequenceDTO.builder().id(SEQUENCE_ID).build();
        SequenceUserDTO newUser = SequenceUserDTO.builder()
                .userId(USER_ID)
                .toBeDeleted(true)
                .build();
        postDTO.setUsers(new HashSet<>(Collections.singleton(newUser)));

        // Execute the service call
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> listener.onBeforeUpdate(postDTO, entity),
                "onBeforeUpdate must throw an update must throw an CommonServiceException");
        Assertions.assertEquals(
                exception.getMessage(),
                ExceptionMessageConstants.SEQUENCE_UPDATE_DELETE_USER_LIST_EMPTY_EXCEPTION);
        Assertions.assertEquals(1, exception.getArgs().length);
    }

    private Sequence createSequenceEntityWithOwner(String sequenceId, String userId) {
        Sequence entity = mockFactory.newEntity(sequenceId);
        SequenceUser user = SequenceUser.builder()
                .userId(userId)
                .type(SequenceUserType.OWNER)
                .updated(LocalDateTime.now(ZoneOffset.UTC).minusDays(10))
                .created(LocalDateTime.now(ZoneOffset.UTC).minusDays(10))
                .build();
        entity.getUsers().add(user);
        return entity;
    }
}
