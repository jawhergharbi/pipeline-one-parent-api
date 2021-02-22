package com.sawoo.pipeline.api.service.sequence;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.mock.SequenceMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.model.sequence.SequenceUser;
import com.sawoo.pipeline.api.model.sequence.SequenceUserType;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
import com.sawoo.pipeline.api.repository.sequence.SequenceRepository;
import com.sawoo.pipeline.api.service.base.BaseLightServiceTest;
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

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doReturn;
import static org.hamcrest.Matchers.containsString;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServiceAccountServiceTest extends BaseLightServiceTest<SequenceDTO, Sequence, SequenceRepository, SequenceService, SequenceMockFactory> {

    @MockBean
    private SequenceRepository repository;

    @MockBean
    private AccountRepository accountRepository;

    @Autowired
    public ServiceAccountServiceTest(SequenceMockFactory mockFactory, SequenceService service) {
        super(mockFactory, DBConstants.SEQUENCE_DOCUMENT, service);
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }

    @Test
    @DisplayName("findByAccountIds: sequence is present - Success")
    void findByAccountIdsWhenSequenceFoundPresentReturnsSuccess() {
        // Set up mocked entities
        String SEQUENCE_ID = getMockFactory().getComponentId();
        Sequence mockedEntity = getMockFactory().newEntity(SEQUENCE_ID);
        String COMPONENT_ID = mockedEntity.getComponentId();
        String USER_OWNER_ID = getMockFactory().getFAKER().internet().uuid();
        addUserToSequence(mockedEntity, USER_OWNER_ID, SequenceUserType.OWNER);
        Account mockedAccount = getMockFactory().getAccountMockFactory().newEntity(COMPONENT_ID);

        // Set up the mocked repository
        doReturn(Collections.singletonList(mockedEntity)).when(repository).findByComponentIdIn(anySet());
        doReturn(Collections.singletonList(mockedAccount)).when(accountRepository).findAllById(anySet());

        // Execute the service call
        List<SequenceDTO> sequences = getService().findByAccountIds(new HashSet<>(Collections.singleton(USER_OWNER_ID)));

        // Assertions
        Assertions.assertAll(String.format("Sequence list must contain 1 sequence with id [%s]", SEQUENCE_ID),
                () -> Assertions.assertFalse(sequences.isEmpty(), "sequence list can not be empty"),
                () -> {
                    SequenceDTO sequence = sequences.get(0);
                    Assertions.assertEquals(SEQUENCE_ID, sequence.getId(), String.format("Sequence id must be [%s]", SEQUENCE_ID));
                    Assertions.assertNotNull(sequence.getOwnerId(), "Owner id can not be null");
                    Assertions.assertEquals(USER_OWNER_ID, sequence.getOwnerId(), String.format("Sequence owner id must be [%s]", USER_OWNER_ID));
                    Assertions.assertNotNull(sequence.getAccount(), "Account can not be null");
                });
    }

    @Test
    @DisplayName("findByAccountIds: account ids is empty - Failure")
    void findByAccountIdsWhenAccountIdsListIsEmptyReturnsFailure() {

        Exception exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> getService().findByAccountIds(new HashSet<>()),
                "findByAccountIds must throw ConstraintViolationException");

        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_LIST_FIELD_CAN_NOT_BE_EMPTY_ERROR).matches(exception.getMessage()));
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
