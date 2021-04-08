package com.sawoo.pipeline.api.service.campaign;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.mock.CampaignMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.campaign.Campaign;
import com.sawoo.pipeline.api.model.campaign.CampaignStatus;
import com.sawoo.pipeline.api.repository.campaign.CampaignRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceTest;
import org.junit.jupiter.api.Assertions;
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

import javax.validation.ConstraintViolationException;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CampaignServiceTest extends BaseServiceTest<CampaignDTO, Campaign, CampaignRepository, CampaignService, CampaignMockFactory> {

    @MockBean
    private CampaignRepository repository;

    @Autowired
    public CampaignServiceTest(CampaignMockFactory mockFactory, CampaignService service) {
        super(mockFactory, DBConstants.CAMPAIGN_DOCUMENT, service);
    }

    @Override
    protected String getEntityId(Campaign component) {
        return component.getId();
    }

    @Override
    protected String getDTOId(CampaignDTO component) {
        return component.getId();
    }

    @Override
    protected void mockedEntityExists(Campaign entity) {
        doReturn(Optional.of(entity)).when(repository).findByComponentIdAndName(anyString(), anyString());
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }

    @Test
    @DisplayName("create: status not informed - default status must be assigned - Success")
    void createWhenStatusNotInformedReturnsSuccess() {
        // Set up mock entities
        CampaignDTO postDTO = spy(getMockFactory().newDTO(null));
        postDTO.setStatus(null);
        CampaignMapper mapper = new CampaignMapper();
        Campaign mockedEntity = mapper.getMapperIn().getDestination(postDTO);
        mockedEntity.setStatus(CampaignStatus.NOT_STARTED);
        mockedEntity.setId(getMockFactory().getComponentId());

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByComponentIdAndName(anyString(), anyString());
        doReturn(mockedEntity).when(repository).insert(any(Campaign.class));

        // Execute the service call
        CampaignDTO returnedDTO = getService().create(postDTO);

        // Assert
        Assertions.assertAll("Campaign must have been created, Id can not be null and status must have been initialized",
                () -> Assertions.assertNotNull(returnedDTO, "Campaign can not be null"),
                () -> Assertions.assertNotNull(returnedDTO.getId(), "Campaign id can not be null"),
                () -> Assertions.assertEquals(
                        CampaignStatus.NOT_STARTED.getValue(),
                        returnedDTO.getStatus(),
                        String.format("Campaign status must be [%s]", CampaignStatus.NOT_STARTED)));

        verify(repository, atMostOnce()).findByComponentIdAndName(anyString(), anyString());
        verify(repository, atMostOnce()).insert(any(Campaign.class));
    }

    @Test
    @DisplayName("create: name not informed - Failure")
    void createWhenNameNotInformedReturnsFailure() {
        // Set up mock entities
        CampaignDTO postDTO = getMockFactory().newDTO(null);
        postDTO.setName(null);

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByComponentIdAndName(anyString(), anyString());

        // Execute the service call
        CampaignService service = getService();
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.create(postDTO),
                "create must throw a ConstraintViolationException");

        // Assertions
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR)
                        .matches(exception.getMessage()));

        verify(repository, never()).findByComponentIdAndName(anyString(), anyString());
        verify(repository, never()).insert(any(Campaign.class));
    }

    @Test
    @DisplayName("create: name and startDate not informed - Failure")
    void createWhenNameAndStartDateAreNotInformedReturnsFailure() {
        // Set up mock entities
        CampaignDTO postDTO = getMockFactory().newDTO(null);
        postDTO.setStartDate(null);
        postDTO.setName(null);
        int CONSTRAINTS_VIOLATED = 2;

        // Set up the mocked repository
        doReturn(Optional.empty()).when(repository).findByComponentIdAndName(anyString(), anyString());

        // Execute the service call
        CampaignService service = getService();
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.create(postDTO),
                "create must throw a ConstraintViolationException");

        // Assertions
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR)
                        .matches(exception.getMessage()));

        Assertions.assertEquals(
                CONSTRAINTS_VIOLATED,
                exception.getConstraintViolations().size(),
                String.format("Number of constrains violated must be [%d]", CONSTRAINTS_VIOLATED));

        verify(repository, never()).findByComponentIdAndName(anyString(), anyString());
        verify(repository, never()).insert(any(Campaign.class));
    }
}
