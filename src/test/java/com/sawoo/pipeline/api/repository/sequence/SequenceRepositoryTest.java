package com.sawoo.pipeline.api.repository.sequence;

import com.sawoo.pipeline.api.mock.SequenceMockFactory;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.repository.base.BaseRepositoryTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

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
}
