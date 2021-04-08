package com.sawoo.pipeline.api.service.prospect;

import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.mock.ProspectMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.prospect.Prospect;
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

    }
}
