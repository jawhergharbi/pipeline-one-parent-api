package com.sawoo.pipeline.api.service.campaign;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignProspectDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignProspectAddDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignProspectBaseDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignProspectCreateDTO;
import com.sawoo.pipeline.api.mock.CampaignMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.campaign.Campaign;
import com.sawoo.pipeline.api.model.campaign.CampaignProspect;
import com.sawoo.pipeline.api.model.campaign.CampaignProspectStatus;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.repository.campaign.CampaignRepository;
import com.sawoo.pipeline.api.repository.prospect.ProspectRepository;
import com.sawoo.pipeline.api.repository.sequence.SequenceRepository;
import com.sawoo.pipeline.api.service.base.BaseLightServiceTest;
import com.sawoo.pipeline.api.service.prospect.ProspectService;
import lombok.Getter;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CampaignProspectServiceTest extends BaseLightServiceTest<CampaignDTO, Campaign, CampaignRepository, CampaignService, CampaignMockFactory> {

    @MockBean
    private CampaignRepository repository;

    @MockBean
    private ProspectService prospectService;

    @MockBean
    private ProspectRepository prospectRepository;

    @MockBean
    private SequenceRepository sequenceRepository;

    @Getter
    private final CampaignMapper mapper;

    @Autowired
    public CampaignProspectServiceTest(CampaignMockFactory mockFactory, CampaignService service, CampaignMapper mapper) {
        super(mockFactory, DBConstants.CAMPAIGN_DOCUMENT, service);
        this.mapper = mapper;
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }

    @Test
    @DisplayName("createProspect: campaign not found - Failure")
    void createProspectWhenCampaignNotFoundReturnsFailure() {
        // Set up mocked entities
        String CAMPAIGN_ID = getMockFactory().getComponentId();
        String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();
        String ACCOUNT_ID = getMockFactory().getFAKER().internet().uuid();
        CampaignProspectCreateDTO createProspectCampaignEntity = getMockFactory().newCampaignProspectCreateDTO(ACCOUNT_ID, SEQUENCE_ID);

        // Set up the mocked repository and services
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute the service call
        CampaignService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.createProspect(CAMPAIGN_ID, createProspectCampaignEntity),
                "createProspect must throw ResourceNotFoundException");

        // Assertions
        Assertions.assertAll("Exception must be correct informed",
                () -> Assertions.assertEquals(
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        exception.getMessage()),
                () -> Assertions.assertEquals(2, exception.getArgs().length),
                () -> Assertions.assertTrue(
                        Arrays.asList(exception.getArgs()).contains(DBConstants.CAMPAIGN_DOCUMENT),
                        String.format("Exception arguments must contain [%s]", DBConstants.CAMPAIGN_DOCUMENT)));
        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, never()).save(any(Campaign.class));
    }

    @Test
    @DisplayName("addProspect: campaign, prospect and sequence found - Success")
    void addProspectWhenCampaignAndProspectAndSequenceFoundReturnsSuccess() {
        // Set up mocked entities
        String CAMPAIGN_ID = getMockFactory().getComponentId();
        String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();
        String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();
        CampaignProspectAddDTO addProspectCampaignEntity = getMockFactory().newCampaignProspectAddDTO(PROSPECT_ID, SEQUENCE_ID);
        Campaign campaignEntity = getMockFactory().newEntity(CAMPAIGN_ID);
        Prospect prospectEntity = getMockFactory().getProspectMockFactory().newEntity(PROSPECT_ID);
        Sequence sequenceEntity = getMockFactory().getSequenceMockFactory().newEntity(SEQUENCE_ID);

        // Set up the mocked repository and services
        doReturn(Optional.of(campaignEntity)).when(repository).findById(anyString());
        doReturn(prospectRepository).when(prospectService).getRepository();
        doReturn(Optional.of(prospectEntity)).when(prospectRepository).findById(anyString());
        doReturn(Collections.emptyList()).when(prospectService).createTODOs(anyString(), anyString(), anyString());
        doReturn(Optional.of(sequenceEntity)).when(sequenceRepository).findById(anyString());

        // Execute the service call
        CampaignProspectDTO campaignProspectDTO = getService().addProspect(CAMPAIGN_ID, addProspectCampaignEntity);

        // Assertions
        Assertions.assertAll("Campaign Prospect component must be properly informed",
                () -> Assertions.assertNotNull(campaignProspectDTO, "Campaign prospect can not be null"),
                () -> Assertions.assertNotNull(campaignProspectDTO.getProspect(), "Campaign prospect entity can not be null"),
                () -> Assertions.assertEquals(PROSPECT_ID, campaignProspectDTO.getProspect().getId(), String.format("Campaign prospect id must be [%s]", PROSPECT_ID)),
                () -> Assertions.assertNotNull(campaignProspectDTO.getSequence(), "Campaign sequence entity can not be null"),
                () -> Assertions.assertEquals(SEQUENCE_ID, campaignProspectDTO.getSequence().getId(), String.format("Campaign sequence id must be [%s]", SEQUENCE_ID)));

        verify(repository, atMostOnce()).findById(anyString());
        verify(prospectRepository, atMostOnce()).findById(anyString());
        verify(sequenceRepository, atMostOnce()).findById(anyString());
        verify(repository, atMostOnce()).save(any(Campaign.class));
    }

    @Test
    @DisplayName("addProspect: campaign not found - Failure")
    void addProspectWhenCampaignNotFoundReturnsFailure() {
        // Set up mocked entities
        String CAMPAIGN_ID = getMockFactory().getComponentId();
        CampaignProspectAddDTO addProspectCampaignEntity = getMockFactory().newCampaignProspectAddDTO();

        // Set up the mocked repository and services
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute the service call
        CampaignService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.addProspect(CAMPAIGN_ID, addProspectCampaignEntity),
                "addProspect must throw ResourceNotFoundException");

        // Assertions
        Assertions.assertAll("Exception must be correct informed",
                () -> Assertions.assertEquals(
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        exception.getMessage()),
                () -> Assertions.assertEquals(2, exception.getArgs().length),
                () -> Assertions.assertTrue(
                        Arrays.asList(exception.getArgs()).contains(DBConstants.CAMPAIGN_DOCUMENT),
                        String.format("Exception arguments must contain [%s]", DBConstants.CAMPAIGN_DOCUMENT)));
        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, never()).save(any(Campaign.class));
    }

    @Test
    @DisplayName("addProspect: prospect not found - Failure")
    void addProspectWhenProspectNotFoundReturnsFailure() {
        // Set up mocked entities
        String CAMPAIGN_ID = getMockFactory().getComponentId();
        CampaignProspectAddDTO addProspectCampaignEntity = getMockFactory().newCampaignProspectAddDTO();
        Campaign campaignEntity = getMockFactory().newEntity(CAMPAIGN_ID);

        // Set up the mocked repository and services
        doReturn(Optional.of(campaignEntity)).when(repository).findById(anyString());
        doReturn(prospectRepository).when(prospectService).getRepository();
        doReturn(Optional.empty()).when(prospectRepository).findById(anyString());

        // Execute the service call
        CampaignService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.addProspect(CAMPAIGN_ID, addProspectCampaignEntity),
                "addaddProspect must throw ResourceNotFoundException");

        // Assertions
        Assertions.assertAll("Exception must be correct informed",
                () -> Assertions.assertEquals(
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        exception.getMessage()),
                () -> Assertions.assertEquals(2, exception.getArgs().length),
                () -> Assertions.assertTrue(
                        Arrays.asList(exception.getArgs()).contains(DBConstants.PROSPECT_DOCUMENT),
                        String.format("Exception arguments must contain [%s]", DBConstants.PROSPECT_DOCUMENT)));

        verify(repository, atMostOnce()).findById(anyString());
        verify(prospectRepository, atMostOnce()).findById(anyString());
        verify(repository, never()).save(any(Campaign.class));
    }

    @Test
    @DisplayName("addProspect: prospect added already - Failure")
    void addProspectWhenProspectAlreadyAddedReturnsFailure() {
        // Set up mocked entities
        String CAMPAIGN_ID = getMockFactory().getComponentId();
        String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();
        String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();
        CampaignProspectAddDTO addProspectCampaignEntity = getMockFactory().newCampaignProspectAddDTO(PROSPECT_ID, SEQUENCE_ID);
        Campaign campaignEntity = getMockFactory().newEntity(CAMPAIGN_ID);
        CampaignProspect campaignProspectEntity = getMockFactory().newCampaignProspectEntity(PROSPECT_ID, SEQUENCE_ID);
        campaignEntity.getProspects().add(campaignProspectEntity);

        // Set up the mocked repository and services
        doReturn(Optional.of(campaignEntity)).when(repository).findById(anyString());

        // Execute the service call
        CampaignService service = getService();
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> service.addProspect(CAMPAIGN_ID, addProspectCampaignEntity),
                "addProspect must throw CommonServiceException");

        Assertions.assertEquals(
                ExceptionMessageConstants.CAMPAIGN_ADD_PROSPECT_ALREADY_ADDED_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, atMostOnce()).findById(anyString());
        verify(prospectRepository, never()).findById(anyString());
        verify(repository, never()).save(any(Campaign.class));
    }

    @Test
    @DisplayName("addProspect: sequence not found - Failure")
    void addProspectWhenSequenceNotFoundReturnsFailure() {
        // Set up mocked entities
        String CAMPAIGN_ID = getMockFactory().getComponentId();
        String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();
        CampaignProspectAddDTO addProspectCampaignEntity = getMockFactory().newCampaignProspectAddDTO();
        Campaign campaignEntity = getMockFactory().newEntity(CAMPAIGN_ID);
        Prospect prospectEntity = getMockFactory().getProspectMockFactory().newEntity(PROSPECT_ID);

        // Set up the mocked repository and services
        doReturn(Optional.of(campaignEntity)).when(repository).findById(anyString());
        doReturn(prospectRepository).when(prospectService).getRepository();
        doReturn(Optional.of(prospectEntity)).when(prospectRepository).findById(anyString());
        doReturn(Optional.empty()).when(sequenceRepository).findById(anyString());

        // Execute the service call
        CampaignService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.addProspect(CAMPAIGN_ID, addProspectCampaignEntity),
                "addProspect must throw ResourceNotFoundException");

        // Assertions
        Assertions.assertAll("Exception must be correct informed",
                () -> Assertions.assertEquals(
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        exception.getMessage()),
                () -> Assertions.assertEquals(2, exception.getArgs().length),
                () -> Assertions.assertTrue(
                        Arrays.asList(exception.getArgs()).contains(DBConstants.SEQUENCE_DOCUMENT),
                        String.format("Exception arguments must contain [%s]", DBConstants.SEQUENCE_DOCUMENT)));

        verify(repository, atMostOnce()).findById(anyString());
        verify(prospectRepository, atMostOnce()).findById(anyString());
        verify(sequenceRepository, atMostOnce()).findById(anyString());
        verify(repository, never()).save(any(Campaign.class));
    }

    @Test
    @DisplayName("removeProspect: campaign and prospect found - Success")
    void removeProspectWhenCampaignAndProspectAndSequenceFoundReturnsSuccess() {
        // Set up mocked entities
        String CAMPAIGN_ID = getMockFactory().getComponentId();
        String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();
        String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();
        Campaign campaignEntity = getMockFactory().newEntity(CAMPAIGN_ID);
        CampaignProspect campaignProspect = getMockFactory().newCampaignProspectEntity(PROSPECT_ID, SEQUENCE_ID);
        campaignEntity.getProspects().add(campaignProspect);

        // Set up the mocked repository and services
        doReturn(Optional.of(campaignEntity)).when(repository).findById(anyString());

        // Execute the service call
        CampaignProspectDTO campaignProspectDTO = getService().removeProspect(CAMPAIGN_ID, PROSPECT_ID);

        // Assertions
        Assertions.assertAll("Campaign Prospect component must be properly informed",
                () -> Assertions.assertNotNull(campaignProspectDTO, "Campaign prospect can not be null"),
                () -> Assertions.assertNotNull(campaignProspectDTO.getProspect(), "Campaign prospect entity can not be null"),
                () -> Assertions.assertEquals(PROSPECT_ID, campaignProspectDTO.getProspect().getId(), String.format("Campaign prospect id must be [%s]", PROSPECT_ID)),
                () -> Assertions.assertNotNull(campaignProspectDTO.getSequence(), "Campaign sequence entity can not be null"),
                () -> Assertions.assertEquals(SEQUENCE_ID, campaignProspectDTO.getSequence().getId(), String.format("Campaign sequence id must be [%s]", SEQUENCE_ID)));

        verify(repository, atMostOnce()).findById(anyString());
        verify(prospectRepository, never()).findById(anyString());
        verify(sequenceRepository, never()).findById(anyString());
        verify(repository, atMostOnce()).save(any(Campaign.class));
    }

    @Test
    @DisplayName("removeProspect: campaign not found - Failure")
    void removeProspectWhenCampaignNotFoundReturnsFailure() {
        // Set up mocked entities
        String CAMPAIGN_ID = getMockFactory().getComponentId();
        String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();

        // Set up the mocked repository and services
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute the service call
        CampaignService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.removeProspect(CAMPAIGN_ID, PROSPECT_ID),
                "removeProspect must throw ResourceNotFoundException");

        // Assertions
        Assertions.assertAll("Exception must be correct informed",
                () -> Assertions.assertEquals(
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        exception.getMessage()),
                () -> Assertions.assertEquals(2, exception.getArgs().length),
                () -> Assertions.assertTrue(
                        Arrays.asList(exception.getArgs()).contains(DBConstants.CAMPAIGN_DOCUMENT),
                        String.format("Exception arguments must contain [%s]", DBConstants.CAMPAIGN_DOCUMENT)));
        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, never()).save(any(Campaign.class));
    }

    @Test
    @DisplayName("removeProspect: prospect not present - Failure")
    void removeProspectWhenProspectNotPresentReturnsFailure() {
        // Set up mocked entities
        String CAMPAIGN_ID = getMockFactory().getComponentId();
        String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();
        Campaign campaignEntity = getMockFactory().newEntity(CAMPAIGN_ID);

        // Set up the mocked repository and services
        doReturn(Optional.of(campaignEntity)).when(repository).findById(anyString());

        // Execute the service call
        CampaignService service = getService();
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> service.removeProspect(CAMPAIGN_ID, PROSPECT_ID),
                "removeProspect must throw CommonServiceException");

        Assertions.assertEquals(
                ExceptionMessageConstants.CAMPAIGN_REMOVE_PROSPECT_NOT_PRESENT_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, never()).save(any(Campaign.class));
    }

    @Test
    @DisplayName("updateProspect: prospect not present - Failure")
    void updateProspectWhenProspectNotPresentReturnsFailure() {
        // Set up mocked entities
        String CAMPAIGN_ID = getMockFactory().getComponentId();
        String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();
        String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();
        Campaign campaignEntity = getMockFactory().newEntity(CAMPAIGN_ID);
        CampaignProspect campaignProspectEntity = getMockFactory().newCampaignProspectEntity(PROSPECT_ID, SEQUENCE_ID);
        campaignEntity.getProspects().add(campaignProspectEntity);
        CampaignProspectBaseDTO postEntity = CampaignProspectBaseDTO
                .builder()
                .prospectId(PROSPECT_ID)
                .status(CampaignProspectStatus.PAUSED.getValue())
                .build();

        // Set up the mocked repository and services
        doReturn(Optional.of(campaignEntity)).when(repository).findById(anyString());

        // Execute the service call
        CampaignProspectDTO returnedDTO = getService().updateProspect(CAMPAIGN_ID, PROSPECT_ID, postEntity);

        Assertions.assertNotNull(returnedDTO, "CampaignProspect can not be null");
        Assertions.assertNotNull(returnedDTO.getProspect(), "Prospect can not be null");
        Assertions.assertEquals(PROSPECT_ID, returnedDTO.getProspect().getId(), String.format("Prospect id must be [%s]", PROSPECT_ID));

        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, atMostOnce()).save(any(Campaign.class));
    }

    @Test
    @DisplayName("updateProspect: campaign prospect valid - Success")
    void updateProspectWhenCampaignProspectValidReturnsSuccess() {
        // Set up mocked entities
        String CAMPAIGN_ID = getMockFactory().getComponentId();
        String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();
        Campaign campaignEntity = getMockFactory().newEntity(CAMPAIGN_ID);
        CampaignProspectBaseDTO postEntity = CampaignProspectBaseDTO
                .builder()
                .prospectId(PROSPECT_ID)
                .status(CampaignProspectStatus.PAUSED.getValue())
                .build();

        // Set up the mocked repository and services
        doReturn(Optional.of(campaignEntity)).when(repository).findById(anyString());

        // Execute the service call
        CampaignService service = getService();
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> service.updateProspect(CAMPAIGN_ID, PROSPECT_ID, postEntity),
                "updateProspect must throw CommonServiceException");

        Assertions.assertEquals(
                ExceptionMessageConstants.CAMPAIGN_UPDATE_PROSPECT_NOT_PRESENT_EXCEPTION,
                exception.getMessage());
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, never()).save(any(Campaign.class));
    }

    @Test
    @DisplayName("findAllProspects: campaign not found - Failure")
    void findAllProspectsWhenCampaignNotFoundReturnsFailure() {
        // Set up mocked entities
        String CAMPAIGN_ID = getMockFactory().getComponentId();

        // Set up the mocked repository and services
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute the service call
        CampaignService service = getService();
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.findAllProspects(CAMPAIGN_ID),
                "findAllProspects must throw ResourceNotFoundException");

        // Assertions
        Assertions.assertAll("Exception must be correct informed",
                () -> Assertions.assertEquals(
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        exception.getMessage()),
                () -> Assertions.assertEquals(2, exception.getArgs().length),
                () -> Assertions.assertTrue(
                        Arrays.asList(exception.getArgs()).contains(DBConstants.CAMPAIGN_DOCUMENT),
                        String.format("Exception arguments must contain [%s]", DBConstants.CAMPAIGN_DOCUMENT)));
        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, never()).save(any(Campaign.class));
    }

    @Test
    @DisplayName("findAllProspects: campaign found - Success")
    void findAllProspectsWhenCampaignNotFoundReturnsSuccess() {
        // Set up mocked entities
        String CAMPAIGN_ID = getMockFactory().getComponentId();
        Campaign campaignEntity = getMockFactory().newEntity(CAMPAIGN_ID);
        int LIST_SIZE = 4;
        List<CampaignProspect> prospects = IntStream.range(0, LIST_SIZE).mapToObj((l) -> {
            String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();
            String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();
           return getMockFactory().newCampaignProspectEntity(PROSPECT_ID, SEQUENCE_ID);
        }).collect(Collectors.toList());
        campaignEntity.getProspects().addAll(prospects);

        // Set up the mocked repository and services
        doReturn(Optional.of(campaignEntity)).when(repository).findById(anyString());

        // Execute the service call
        List<CampaignProspectDTO> prospectList = getService().findAllProspects(CAMPAIGN_ID);

        Assertions.assertFalse(prospectList.isEmpty(), "List of campaign prospect can not be empty");
    }
}
