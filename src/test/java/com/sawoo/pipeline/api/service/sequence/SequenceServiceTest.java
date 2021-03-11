package com.sawoo.pipeline.api.service.sequence;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.dto.sequence.SequenceUserDTO;
import com.sawoo.pipeline.api.mock.SequenceMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.model.sequence.SequenceUser;
import com.sawoo.pipeline.api.model.sequence.SequenceUserType;
import com.sawoo.pipeline.api.repository.sequence.SequenceRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SequenceServiceTest extends BaseServiceTest<SequenceDTO, Sequence, SequenceRepository, SequenceService, SequenceMockFactory> {

    @MockBean
    private SequenceRepository repository;

    @Autowired
    public SequenceServiceTest(SequenceMockFactory mockFactory, SequenceService service) {
        super(mockFactory, DBConstants.SEQUENCE_DOCUMENT, service);
    }

    @Override
    protected String getEntityId(Sequence component) {
        return component.getId();
    }

    @Override
    protected String getDTOId(SequenceDTO component) {
        return component.getId();
    }

    @Override
    protected void mockedEntityExists(Sequence entity) {
        doReturn(Optional.of(entity)).when(repository).findById(anyString());
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }

    @Test
    @DisplayName("create: user id not informed - Failure")
    void createWhenUserOwnerNotInformedReturnsFailure() {
        // Set up mock entities
        String USER_ID = getMockFactory().getFAKER().internet().uuid();
        SequenceDTO mockedDTO = getMockFactory().newDTO(null);
        mockedDTO.setUsers(new HashSet<>(Collections.singleton(
                        SequenceUserDTO
                                .builder()
                                .userId(USER_ID)
                                .type(SequenceUserType.EDITOR)
                                .build())));

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute the service call
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () ->  getService().create(mockedDTO),
                "create must throw a CommonServiceException");
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.SEQUENCE_CREATE_USER_OWNER_NOT_SPECIFIED_EXCEPTION)
                        .matches(exception.getMessage()));

        verify(repository, never()).insert(any(Sequence.class));
    }

    @Test
    @DisplayName("update: user id not informed - Failure")
    void updateWhenUserIdNotInformedReturnsFailure() {
        // Set up mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();
        Sequence mockedEntity = getMockFactory().newEntity(SEQUENCE_ID);
        SequenceDTO mockedDTO = SequenceDTO.builder()
                .users(new HashSet<>(Collections.singleton(
                        SequenceUserDTO
                                .builder()
                                .type(SequenceUserType.OWNER)
                                .build())))
                .id(SEQUENCE_ID)
                .build();

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(anyString());

        // Execute the service call
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () ->  getService().update(SEQUENCE_ID, mockedDTO),
                "update must throw a CommonServiceException");
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.SEQUENCE_UPDATE_USER_ID_NOT_INFORMED_EXCEPTION)
                        .matches(exception.getMessage()));

        verify(repository, times(1)).findById(anyString());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("deleteUser: user id is present - Success")
    void deleteUserWhenUserIsPresentReturnsSuccess() {
        // Set up mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();
        Sequence mockedEntity = getMockFactory().newEntity(SEQUENCE_ID);
        String USER_ID = getMockFactory().getFAKER().internet().uuid();
        String USER_OWNER_ID = getMockFactory().getFAKER().internet().uuid();
        addUserToSequence(mockedEntity, USER_ID, SequenceUserType.VIEWER);
        addUserToSequence(mockedEntity, USER_OWNER_ID, SequenceUserType.OWNER);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(anyString());

        // Execute the service call
        SequenceDTO sequenceDTO = getService().deleteUser(SEQUENCE_ID, USER_ID);

        // Assertions
        Assertions.assertAll(String.format("Sequence with id [%s] is correctly validated", SEQUENCE_ID),
                () -> Assertions.assertEquals(
                        1,
                        sequenceDTO.getUsers().size(),
                        String. format("Sequence user list must have [%d] element", 1)),
                () -> Assertions.assertNotNull(
                        sequenceDTO.getUsers().iterator().next(),
                        "Fist element of the list of user can not be null"),
                () -> {
                    SequenceUserType userType = sequenceDTO.getUsers().iterator().next().getType();
                    Assertions.assertEquals(
                            SequenceUserType.OWNER,
                            userType,
                            String.format("Remaining user must have type [%s]", SequenceUserType.OWNER));
                },
                () -> {
                    String userId = sequenceDTO.getUsers().iterator().next().getUserId();
                    Assertions.assertEquals(
                            USER_OWNER_ID,
                            userId,
                            String.format("Remaining user id must be [%s]", USER_OWNER_ID));
                });
    }

    @Test
    @DisplayName("deleteUser: user id is present - Failure")
    void deleteUserWhenUserIsTheOwnerPresentReturnsFailure() {
        // Set up mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();
        Sequence mockedEntity = getMockFactory().newEntity(SEQUENCE_ID);
        String USER_ID = getMockFactory().getFAKER().internet().uuid();
        String USER_OWNER_ID = getMockFactory().getFAKER().internet().uuid();
        addUserToSequence(mockedEntity, USER_ID, SequenceUserType.VIEWER);
        addUserToSequence(mockedEntity, USER_OWNER_ID, SequenceUserType.OWNER);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(anyString());

        // Execute the service call
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> getService().deleteUser(SEQUENCE_ID, USER_OWNER_ID),
                "deleteUser must throw CommonServiceException"
                );

        Assertions.assertEquals(
                exception.getMessage(),
                ExceptionMessageConstants.SEQUENCE_UPDATE_DELETE_USER_OWNER_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, times(2)).findById(anyString());
        verify(repository, never()).save(any());
    }

    private void addUserToSequence(Sequence sequence, String userId, SequenceUserType userType) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        SequenceUser user = SequenceUser.builder()
                .userId(userId)
                .type(userType)
                .updated(now.minusDays(10))
                .created(now.minusDays(1))
                .build();
        sequence.getUsers().add(user);
    }
}
