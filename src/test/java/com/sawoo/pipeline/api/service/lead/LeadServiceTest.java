package com.sawoo.pipeline.api.service.lead;

import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.mock.LeadMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.repository.lead.LeadRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceTest;
import org.junit.jupiter.api.*;
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
public class LeadServiceTest extends BaseServiceTest<LeadDTO, Lead, LeadRepository, LeadService, LeadMockFactory> {

    @MockBean
    private LeadRepository repository;

    @Autowired
    public LeadServiceTest(LeadMockFactory mockFactory, LeadService service) {
        super(mockFactory, DBConstants.LEAD_DOCUMENT, service);
    }

    @Override
    protected String getEntityId(Lead component) {
        return component.getId();
    }

    @Override
    protected String getDTOId(LeadDTO component) {
        return component.getId();
    }

    @Override
    protected void mockedEntityExists(Lead entity) {
        doReturn(Optional.of(entity)).when(repository).findById(anyString());
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }
}
