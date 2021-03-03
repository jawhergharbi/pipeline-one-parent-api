package com.sawoo.pipeline.api.service.sequencestep;

import com.sawoo.pipeline.api.dto.sequence.SequenceStepDTO;
import com.sawoo.pipeline.api.mock.SequenceStepMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.sequence.SequenceStep;
import com.sawoo.pipeline.api.repository.sequencestep.SequenceStepRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SequenceStepServiceTest extends BaseServiceTest<SequenceStepDTO, SequenceStep, SequenceStepRepository, SequenceStepService, SequenceStepMockFactory> {

    @MockBean
    private SequenceStepRepository repository;

    @Autowired
    public SequenceStepServiceTest(SequenceStepMockFactory mockFactory, SequenceStepService service) {
        super(mockFactory, DBConstants.SEQUENCE_STEP_DOCUMENT, service);
    }

    @Override
    protected String getEntityId(SequenceStep component) {
        return component.getId();
    }

    @Override
    protected String getDTOId(SequenceStepDTO component) {
        return component.getId();
    }

    @Override
    protected void mockedEntityExists(SequenceStep entity) {
        doReturn(Optional.of(entity)).when(repository).findById(anyString());
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }
}
