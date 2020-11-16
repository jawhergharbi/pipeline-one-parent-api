package com.sawoo.pipeline.api.service.prospect;

import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.mock.ProspectMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.repository.ProspectRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceTest;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProspectServiceTest extends BaseServiceTest<ProspectDTO, Prospect, ProspectRepository, ProspectService, ProspectMockFactory> {

    @MockBean
    private ProspectRepository repository;

    @Autowired
    public ProspectServiceTest(ProspectMockFactory mockFactory, ProspectService service) {
        super(mockFactory, DBConstants.PROSPECT_DOCUMENT, service);
    }

    @Override
    protected String getEntityId(Prospect component) {
        return component.getId();
    }

    @Override
    protected String getDTOId(ProspectDTO component) {
        return component.getId();
    }

    @Override
    protected void mockedEntityExists(Prospect entity) {
        doReturn(Optional.of(entity)).when(repository).findByLinkedInUrl(anyString());
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }

    @Test
    @DisplayName("create: when entity does not exist - Success")
    void createWhenEntityDoesNotExistReturnsSuccess() {
        // Set up mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        String PROSPECT_LINKED_IN_URL = getMockFactory().getFAKER().internet().url();
        ProspectDTO mockedDTO = getMockFactory().newDTO(PROSPECT_ID);
        mockedDTO.setLinkedInUrl(PROSPECT_LINKED_IN_URL);
        Prospect mockedEntity = getMockFactory().newEntity(PROSPECT_ID);
        mockedEntity.setLinkedInUrl(PROSPECT_LINKED_IN_URL);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByLinkedInUrl(anyString());
        doReturn(mockedEntity).when(repository).insert(any(Prospect.class));

        // Execute the service call
        ProspectDTO returnedEntity = getService().create(mockedDTO);

        // Assert the response
        Assertions.assertNotNull(returnedEntity, "Entity can not be null");
        Assertions.assertEquals(PROSPECT_LINKED_IN_URL, returnedEntity.getLinkedInUrl(), "Prospect.linkedInUrl should be the same");
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), returnedEntity.getCreated().toLocalDate(), "Creation time must be today");
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), returnedEntity.getUpdated().toLocalDate(), "Update time must be today");

        verify(repository, Mockito.times(1)).findByLinkedInUrl(anyString());
        verify(repository, Mockito.times(1)).insert(any(Prospect.class));
    }

    @Test
    @DisplayName("update: entity does exist - Success")
    void updateWhenEntityFoundReturnsSuccess() {
        // Set up mocked entities
        String PROSPECT_ID = getMockFactory().getComponentId();
        ProspectDTO mockedDTO = new ProspectDTO();
        String PROSPECT_NEW_LAST_NAME = getMockFactory().getFAKER().name().lastName();
        mockedDTO.setLastName(PROSPECT_NEW_LAST_NAME);
        mockedDTO.setId(PROSPECT_ID);
        Prospect mockedEntity = getMockFactory().newEntity(PROSPECT_ID);

        // Set up the mocked repository
        doReturn(Optional.of(mockedEntity)).when(repository).findById(PROSPECT_ID);

        // Execute the service call
        ProspectDTO returnedDTO = getService().update(PROSPECT_ID, mockedDTO);

        Assertions.assertNotNull(returnedDTO, "Prospect entity can not be null");
        Assertions.assertEquals(
                PROSPECT_NEW_LAST_NAME,
                returnedDTO.getLastName(),
                String.format("LastName must be '%s'", PROSPECT_NEW_LAST_NAME));

        verify(repository, Mockito.times(1)).findById(anyString());
        verify(repository, Mockito.times(1)).save(any());
    }
}
