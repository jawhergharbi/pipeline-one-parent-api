package com.sawoo.pipeline.api.repository.sequence;

import com.sawoo.pipeline.api.mock.SequenceMockFactory;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.model.sequence.SequenceStep;
import com.sawoo.pipeline.api.model.sequence.SequenceUserType;
import com.sawoo.pipeline.api.repository.base.BaseRepositoryTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tags(value = {@Tag(value = "data")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class SequenceRepositoryTest extends BaseRepositoryTest<Sequence, SequenceRepository, SequenceMockFactory> {

    private static final String SEQUENCE_JSON_DATA_FILE_NAME = "sequence-test-data.json";
    private static final String SEQUENCE_ID = "60278c364334846b8d167131";

    private final SequenceStepRepository sequenceStepRepository;


    @Autowired
    public SequenceRepositoryTest(
            SequenceRepository repository,
            SequenceMockFactory mockFactory,
            SequenceStepRepository sequenceStepRepository) {
        super(repository, SEQUENCE_JSON_DATA_FILE_NAME, SEQUENCE_ID, Sequence.class.getSimpleName(), mockFactory);
        this.sequenceStepRepository = sequenceStepRepository;
    }

    @Override
    protected Class<Sequence[]> getClazz() {
        return Sequence[].class;
    }

    @Override
    protected String getComponentId(Sequence component) {
        return component.getId();
    }

    @Override
    protected Sequence getNewEntity() {
        String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();
        return getMockFactory().newEntity(SEQUENCE_ID);
    }

    @AfterEach
    protected void afterEach() {
        super.afterEach();
        sequenceStepRepository.deleteAll();
    }

    @Test
    @DisplayName("findByUserId: sequences found - Success")
    void findByUserWhenUserIsPartOfSequencesReturnsSuccess() {
        // Arrange
        String USER_ID = "6027a3436fb12b99f63b0e23";
        int SEQUENCES_FOUND = 1;

        // Act
        List<Sequence> sequences = getRepository().findByUser(USER_ID);

        // Assert
        Assertions.assertFalse(sequences.isEmpty(), "List of sequences can not be empty");
        Assertions.assertEquals(
                SEQUENCES_FOUND,
                sequences.size(),
                String.format("[%d] sequence/s must be found for user with id [%s]", SEQUENCES_FOUND, USER_ID));
    }

    @Test
    @DisplayName("findByUser: sequences not found - Success")
    void findByUserWhenUserIsNotPartOfSequencesReturnsSuccess() {
        // Arrange
        String USER_ID = "wrong_id";

        // Act
        List<Sequence> sequences = getRepository().findByUser(USER_ID);

        // Assert
        Assertions.assertTrue(sequences.isEmpty(), "List of sequences must be empty");
    }

    @Test
    @DisplayName("findByUsers: sequences found - Success")
    void findByUsersWhenUsersArePartOfSequencesReturnsSuccess() {
        // Arrange
        String USER_ID_1 = "6027a3436fb12b99f63b0e23";
        String USER_ID_2 = "6027a2ff4542c0de858d2936";
        int SEQUENCES_FOUND = 3;

        // Act
        List<Sequence> sequences = getRepository().findByUsers(new HashSet<>(Arrays.asList(USER_ID_1, USER_ID_2)));

        // Assert
        Assertions.assertFalse(sequences.isEmpty(), "List of sequences can not be empty");
        Assertions.assertEquals(
                SEQUENCES_FOUND,
                sequences.size(),
                String.format("[%d] sequence/s must be found for users with ids [%s]", SEQUENCES_FOUND, Arrays.asList(USER_ID_1, USER_ID_2)));
    }

    @Test
    @DisplayName("findByUsers: sequences found and only one userId provided- Success")
    void findByUsersWhenUserIsPartOfSequencesReturnsSuccess() {
        // Arrange
        String USER_ID = "6027a3436fb12b99f63b0e23";
        int SEQUENCES_FOUND = 1;

        // Act
        List<Sequence> sequences = getRepository().findByUsers(new HashSet<>(Collections.singletonList(USER_ID)));

        // Assert
        Assertions.assertFalse(sequences.isEmpty(), "List of sequences can not be empty");
        Assertions.assertEquals(
                SEQUENCES_FOUND,
                sequences.size(),
                String.format(
                        "[%d] sequence/s must be found for users with ids [%s]",
                        SEQUENCES_FOUND,
                        Collections.singletonList(USER_ID)));
    }

    @Test
    @DisplayName("findByUserIdAndUserType: sequences found - Success")
    void findByUserIdAndUserTypeWhenUserIsPartOfSequencesReturnsSuccess() {
        // Arrange
        String USER_ID = "6027a2ff4542c0de858d2936";
        int SEQUENCES_FOUND = 2;

        // Act
        List<Sequence> sequences = getRepository().findByUserIdAndUserType(USER_ID, SequenceUserType.OWNER);

        Assertions.assertFalse(sequences.isEmpty(), "List of sequences can not be empty");
        Assertions.assertEquals(
                SEQUENCES_FOUND,
                sequences.size(),
                String.format(
                        "[%d] sequence/s must be found for user with id [%s] and user type [%s]",
                        SEQUENCES_FOUND,
                        USER_ID,
                        SequenceUserType.OWNER));
    }

    @Test
    @DisplayName("findByUserIdsAndUserType: sequences found - Success")
    void findByUserIdsAndUserTypeWhenUserIsPartOfSequencesReturnsSuccess() {
        // Arrange
        String USER_ID = "6027a2ff4542c0de858d2936";
        int SEQUENCES_FOUND = 1;

        // Act
        List<Sequence> sequences = getRepository()
                .findByUserIdsAndUserType(
                        new HashSet<>(Collections.singletonList(USER_ID)),
                        SequenceUserType.EDITOR);

        // Assert
        Assertions.assertFalse(sequences.isEmpty(), "List of sequences can not be empty");
        Assertions.assertEquals(
                SEQUENCES_FOUND,
                sequences.size(),
                String.format(
                        "[%d] sequence/s must be found for user with id [%s] and user type [%s]",
                        SEQUENCES_FOUND,
                        USER_ID,
                        SequenceUserType.EDITOR));
    }

    @Test
    @DisplayName("insert: sequence with one step - Success")
    void insertWhenSequenceContainsOneStepReturnsSuccess() {
        // Arrange
        String SEQUENCE_ID = getMockFactory().getComponentId();
        Sequence sequence = getMockFactory().newEntity(SEQUENCE_ID);
        SequenceStep step = getMockFactory()
                .getSequenceStepMockFactory()
                .newSequenceStepEntity(null, 0);
        sequence.getSteps().add(step);
        getRepository().insert(sequence);

        // Act
        Optional<Sequence> sequenceFound = getRepository().findById(SEQUENCE_ID);

        // Assert
        Assertions.assertAll(String.format("Sequence with id [%s] is correctly stored", SEQUENCE_ID),
                () -> Assertions.assertTrue(sequenceFound.isPresent(), "Sequence can not be null"),
                () -> sequenceFound.ifPresent((s) -> Assertions.assertFalse(s.getSteps().isEmpty(), "Steps can not be empty")),
                () -> sequenceFound.ifPresent((s) -> Assertions.assertNotNull(s.getSteps().get(0), "Steps[0] can not null")));


    }
}
