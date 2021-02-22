package com.sawoo.pipeline.api.repository.sequence;

import com.sawoo.pipeline.api.mock.SequenceStepMockFactory;
import com.sawoo.pipeline.api.model.sequence.SequenceStep;
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
public class SequenceStepRepositoryTest extends BaseRepositoryTest<SequenceStep, SequenceStepRepository, SequenceStepMockFactory> {

    private static final String SEQUENCE_STEP_JSON_DATA_FILE_NAME = "sequence-step-test-data.json";
    private static final String SEQUENCE_STEP_ID = "6028dbeab5b9825f6bcb3e41";


    @Autowired
    public SequenceStepRepositoryTest(
            SequenceStepRepository repository,
            SequenceStepMockFactory mockFactory) {
        super(repository, SEQUENCE_STEP_JSON_DATA_FILE_NAME, SEQUENCE_STEP_ID, SequenceStep.class.getSimpleName(), mockFactory);
    }

    @Override
    protected Class<SequenceStep[]> getClazz() {
        return SequenceStep[].class;
    }

    @Override
    protected String getComponentId(SequenceStep component) {
        return component.getId();
    }

    @Override
    protected SequenceStep getNewEntity() {
        String SEQUENCE_STEP_ID = getMockFactory().getFAKER().internet().uuid();
        return getMockFactory().newEntity(SEQUENCE_STEP_ID);
    }
}
