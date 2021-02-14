package com.sawoo.pipeline.api.service.sequence;

import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.mock.SequenceMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.repository.sequence.SequenceRepository;
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
}
