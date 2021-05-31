package com.sawoo.pipeline.api.service.prospect; // Set up mocked entities

import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.dto.sequence.SequenceStepDTO;
import com.sawoo.pipeline.api.mock.ProspectMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.model.sequence.SequenceStep;
import com.sawoo.pipeline.api.repository.prospect.ProspectRepository;
import com.sawoo.pipeline.api.service.base.BaseLightServiceTest;
import com.sawoo.pipeline.api.service.sequence.SequenceService;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProspectSequenceTodoServiceTest extends BaseLightServiceTest<ProspectDTO, Prospect, ProspectRepository, ProspectService, ProspectMockFactory> {

    @MockBean
    private ProspectRepository repository;

    @MockBean
    private SequenceService sequenceService;

    @Autowired
    public ProspectSequenceTodoServiceTest(ProspectMockFactory mockFactory, ProspectService service) {
        super(mockFactory, DBConstants.PROSPECT_DOCUMENT, service);
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }

    @Test
    @DisplayName("evalTODOs: prospect and sequence found - Success")
    void evalTODOsWhenProspectAndSequenceFoundReturnsSuccess() {
        // Set up mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();
        Prospect prospectEntity = getMockFactory().newEntity(PROSPECT_ID);
        /*List<SequenceStepDTO> steps = newSequenceWithSteps(SEQUENCE_ID, 3);*/


        // Set up the mocked repository
        doReturn(prospectEntity).when(repository).findById(PROSPECT_ID);
        /*doReturn(steps).when(sequenceService).getStepsByPersonality(anyString(), anyInt());*/
    }

    private Sequence newSequenceWithSteps(String sequenceId, int stepsNumber) {
        Sequence mockEntity = getMockFactory().getSequenceMockFactory().newEntity(sequenceId);
        List<SequenceStep> steps = IntStream.range(0, stepsNumber).mapToObj( (idx) -> {
            String SEQUENCE_STEP_ID = getMockFactory().getSequenceMockFactory().getSequenceStepMockFactory().getComponentId();
            return getMockFactory().getSequenceMockFactory().getSequenceStepMockFactory().newSequenceStepEntity(SEQUENCE_STEP_ID, idx + 1);
        }).collect(Collectors.toList());
        mockEntity.setSteps(steps);
        return mockEntity;
    }
}
