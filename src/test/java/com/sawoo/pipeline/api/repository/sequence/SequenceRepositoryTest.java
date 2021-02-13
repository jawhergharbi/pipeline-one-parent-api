package com.sawoo.pipeline.api.repository.sequence;

import com.sawoo.pipeline.api.mock.SequenceMockFactory;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.model.sequence.SequenceUserType;
import com.sawoo.pipeline.api.repository.base.BaseRepositoryTest;
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

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tags(value = {@Tag(value = "data")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class SequenceRepositoryTest extends BaseRepositoryTest<Sequence, SequenceRepository, SequenceMockFactory> {

    private static final String SEQUENCE_JSON_DATA_FILE_NAME = "sequence-test-data.json";
    private static final String SEQUENCE_ID = "60278c364334846b8d167131";


    @Autowired
    public SequenceRepositoryTest(
            SequenceRepository repository,
            SequenceMockFactory mockFactory) {
        super(repository, SEQUENCE_JSON_DATA_FILE_NAME, SEQUENCE_ID, Sequence.class.getSimpleName(), mockFactory);
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

    @Test
    @DisplayName("findByUserId: sequences found - Success")
    void findByUserWhenUserIsPartOfSequencesReturnsSuccess() {
        String USER_ID = "6027a3436fb12b99f63b0e23";
        int SEQUENCES_FOUND = 1;

        List<Sequence> sequences = getRepository().findByUser(USER_ID);

        Assertions.assertFalse(sequences.isEmpty(), "List of sequences can not be empty");
        Assertions.assertEquals(
                SEQUENCES_FOUND,
                sequences.size(),
                String.format("[%d] sequence/s must be found for user with id [%s]", SEQUENCES_FOUND, USER_ID));
    }

    @Test
    @DisplayName("findByUser: sequences not found - Success")
    void findByUserWhenUserIsNotPartOfSequencesReturnsSuccess() {
        String USER_ID = "wrong_id";

        List<Sequence> sequences = getRepository().findByUser(USER_ID);

        Assertions.assertTrue(sequences.isEmpty(), "List of sequences must be empty");
    }

    @Test
    @DisplayName("findByUsers: sequences found - Success")
    void findByUsersWhenUsersArePartOfSequencesReturnsSuccess() {
        String USER_ID_1 = "6027a3436fb12b99f63b0e23";
        String USER_ID_2 = "6027a2ff4542c0de858d2936";
        int SEQUENCES_FOUND = 3;

        List<Sequence> sequences = getRepository().findByUsers(new HashSet<>(Arrays.asList(USER_ID_1, USER_ID_2)));

        Assertions.assertFalse(sequences.isEmpty(), "List of sequences can not be empty");
        Assertions.assertEquals(
                SEQUENCES_FOUND,
                sequences.size(),
                String.format("[%d] sequence/s must be found for users with ids [%s]", SEQUENCES_FOUND, Arrays.asList(USER_ID_1, USER_ID_2)));
    }

    @Test
    @DisplayName("findByUsers: sequences found and only one userId provided- Success")
    void findByUsersWhenUserIsPartOfSequencesReturnsSuccess() {
        String USER_ID = "6027a3436fb12b99f63b0e23";
        int SEQUENCES_FOUND = 1;

        List<Sequence> sequences = getRepository().findByUsers(new HashSet<>(Collections.singletonList(USER_ID)));

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
        String USER_ID = "6027a2ff4542c0de858d2936";
        int SEQUENCES_FOUND = 2;

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
        String USER_ID = "6027a2ff4542c0de858d2936";
        int SEQUENCES_FOUND = 1;

        List<Sequence> sequences = getRepository()
                .findByUserIdsAndUserType(
                        new HashSet<>(Collections.singletonList(USER_ID)),
                        SequenceUserType.EDITOR);

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
}
