package com.sawoo.pipeline.api.service.campaign;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignLeadAddDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignLeadDTO;
import com.sawoo.pipeline.api.mock.CampaignMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.campaign.Campaign;
import com.sawoo.pipeline.api.model.campaign.CampaignLead;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.repository.campaign.CampaignRepository;
import com.sawoo.pipeline.api.repository.lead.LeadRepository;
import com.sawoo.pipeline.api.repository.sequence.SequenceRepository;
import com.sawoo.pipeline.api.service.base.BaseLightServiceTest;
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
public class CampaignLeadServiceTest extends BaseLightServiceTest<CampaignDTO, Campaign, CampaignRepository, CampaignService, CampaignMockFactory> {

    @MockBean
    private CampaignRepository repository;

    @MockBean
    private LeadRepository leadRepository;

    @MockBean
    private SequenceRepository sequenceRepository;

    @Getter
    private final CampaignMapper mapper;

    @Autowired
    public CampaignLeadServiceTest(CampaignMockFactory mockFactory, CampaignService service, CampaignMapper mapper) {
        super(mockFactory, DBConstants.CAMPAIGN_DOCUMENT, service);
        this.mapper = mapper;
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }

    @Test
    @DisplayName("addLead: campaign, lead and sequence found - Success")
    void addLeadWhenCampaignAndLeadAndSequenceFoundReturnsSuccess() {
        // Set up mocked entities
        String CAMPAIGN_ID = getMockFactory().getComponentId();
        String LEAD_ID = getMockFactory().getFAKER().internet().uuid();
        String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();
        CampaignLeadAddDTO addLeadCampaignEntity = getMockFactory().newCampaignLeadAddDTO(LEAD_ID, SEQUENCE_ID);
        Campaign campaignEntity = getMockFactory().newEntity(CAMPAIGN_ID);
        Lead leadEntity = getMockFactory().getLeadMockFactory().newEntity(LEAD_ID);
        Sequence sequenceEntity = getMockFactory().getSequenceMockFactory().newEntity(SEQUENCE_ID);

        // Set up the mocked repository and services
        doReturn(Optional.of(campaignEntity)).when(repository).findById(anyString());
        doReturn(Optional.of(leadEntity)).when(leadRepository).findById(anyString());
        doReturn(Optional.of(sequenceEntity)).when(sequenceRepository).findById(anyString());

        // Execute the service call
        CampaignLeadDTO campaignLeadDTO = getService().addLead(CAMPAIGN_ID, addLeadCampaignEntity);

        // Assertions
        Assertions.assertAll("Campaign Lead component must be properly informed",
                () -> Assertions.assertNotNull(campaignLeadDTO, "Campaign lead can not be null"),
                () -> Assertions.assertNotNull(campaignLeadDTO.getLead(), "Campaign lead entity can not be null"),
                () -> Assertions.assertEquals(LEAD_ID, campaignLeadDTO.getLead().getId(), String.format("Campaign lead id must be [%s]", LEAD_ID)),
                () -> Assertions.assertNotNull(campaignLeadDTO.getSequence(), "Campaign sequence entity can not be null"),
                () -> Assertions.assertEquals(SEQUENCE_ID, campaignLeadDTO.getSequence().getId(), String.format("Campaign sequence id must be [%s]", SEQUENCE_ID)));

        verify(repository, atMostOnce()).findById(anyString());
        verify(leadRepository, atMostOnce()).findById(anyString());
        verify(sequenceRepository, atMostOnce()).findById(anyString());
        verify(repository, atMostOnce()).save(any(Campaign.class));
    }

    @Test
    @DisplayName("addLead: campaign not found - Failure")
    void addLeadWhenCampaignNotFoundFailure() {
        // Set up mocked entities
        String CAMPAIGN_ID = getMockFactory().getComponentId();
        CampaignLeadAddDTO addLeadCampaignEntity = getMockFactory().newCampaignLeadAddDTO();

        // Set up the mocked repository and services
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute the service call
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> getService().addLead(CAMPAIGN_ID, addLeadCampaignEntity),
                "addCampaignLead must throw ResourceNotFoundException");

        // Assertions
        Assertions.assertAll("Exception must be correct informed",
                () -> Assertions.assertEquals(
                        exception.getMessage(),
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION),
                () -> Assertions.assertEquals(2, exception.getArgs().length),
                () -> Assertions.assertTrue(
                        Arrays.asList(exception.getArgs()).contains(DBConstants.CAMPAIGN_DOCUMENT),
                        String.format("Exception arguments must contain [%s]", DBConstants.CAMPAIGN_DOCUMENT)));
        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, never()).save(any(Campaign.class));
    }

    @Test
    @DisplayName("addLead: lead not found - Failure")
    void addLeadWhenLeadNotFoundFailure() {
        // Set up mocked entities
        String CAMPAIGN_ID = getMockFactory().getComponentId();
        CampaignLeadAddDTO addLeadCampaignEntity = getMockFactory().newCampaignLeadAddDTO();
        Campaign campaignEntity = getMockFactory().newEntity(CAMPAIGN_ID);

        // Set up the mocked repository and services
        doReturn(Optional.of(campaignEntity)).when(repository).findById(anyString());
        doReturn(Optional.empty()).when(leadRepository).findById(anyString());

        // Execute the service call
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> getService().addLead(CAMPAIGN_ID, addLeadCampaignEntity),
                "addCampaignLead must throw ResourceNotFoundException");

        // Assertions
        Assertions.assertAll("Exception must be correct informed",
                () -> Assertions.assertEquals(
                        exception.getMessage(),
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION),
                () -> Assertions.assertEquals(2, exception.getArgs().length),
                () -> Assertions.assertTrue(
                        Arrays.asList(exception.getArgs()).contains(DBConstants.LEAD_DOCUMENT),
                        String.format("Exception arguments must contain [%s]", DBConstants.LEAD_DOCUMENT)));

        verify(repository, atMostOnce()).findById(anyString());
        verify(leadRepository, atMostOnce()).findById(anyString());
        verify(repository, never()).save(any(Campaign.class));
    }

    @Test
    @DisplayName("addLead: lead added already - Failure")
    void addLeadWhenLeadAlreadyAddedFailure() {
        // Set up mocked entities
        String CAMPAIGN_ID = getMockFactory().getComponentId();
        String LEAD_ID = getMockFactory().getFAKER().internet().uuid();
        String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();
        CampaignLeadAddDTO addLeadCampaignEntity = getMockFactory().newCampaignLeadAddDTO(LEAD_ID, SEQUENCE_ID);
        Campaign campaignEntity = getMockFactory().newEntity(CAMPAIGN_ID);
        CampaignLead campaignLeadEntity = getMockFactory().newCampaignLeadEntity(LEAD_ID, SEQUENCE_ID);
        campaignEntity.getLeads().add(campaignLeadEntity);

        // Set up the mocked repository and services
        doReturn(Optional.of(campaignEntity)).when(repository).findById(anyString());

        // Execute the service call
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> getService().addLead(CAMPAIGN_ID, addLeadCampaignEntity),
                "addCampaignLead must throw CommonServiceException");

        Assertions.assertEquals(
                exception.getMessage(),
                ExceptionMessageConstants.CAMPAIGN_ADD_LEAD_ALREADY_ADDED_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, atMostOnce()).findById(anyString());
        verify(leadRepository, never()).findById(anyString());
        verify(repository, never()).save(any(Campaign.class));
    }

    @Test
    @DisplayName("addLead: sequence not found - Failure")
    void addLeadWhenSequenceNotFoundFailure() {
        // Set up mocked entities
        String CAMPAIGN_ID = getMockFactory().getComponentId();
        String LEAD_ID = getMockFactory().getFAKER().internet().uuid();
        CampaignLeadAddDTO addLeadCampaignEntity = getMockFactory().newCampaignLeadAddDTO();
        Campaign campaignEntity = getMockFactory().newEntity(CAMPAIGN_ID);
        Lead leadEntity = getMockFactory().getLeadMockFactory().newEntity(LEAD_ID);

        // Set up the mocked repository and services
        doReturn(Optional.of(campaignEntity)).when(repository).findById(anyString());
        doReturn(Optional.of(leadEntity)).when(leadRepository).findById(anyString());
        doReturn(Optional.empty()).when(sequenceRepository).findById(anyString());

        // Execute the service call
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> getService().addLead(CAMPAIGN_ID, addLeadCampaignEntity),
                "addCampaignLead must throw ResourceNotFoundException");

        // Assertions
        Assertions.assertAll("Exception must be correct informed",
                () -> Assertions.assertEquals(
                        exception.getMessage(),
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION),
                () -> Assertions.assertEquals(2, exception.getArgs().length),
                () -> Assertions.assertTrue(
                        Arrays.asList(exception.getArgs()).contains(DBConstants.SEQUENCE_DOCUMENT),
                        String.format("Exception arguments must contain [%s]", DBConstants.SEQUENCE_DOCUMENT)));

        verify(repository, atMostOnce()).findById(anyString());
        verify(leadRepository, atMostOnce()).findById(anyString());
        verify(sequenceRepository, atMostOnce()).findById(anyString());
        verify(repository, never()).save(any(Campaign.class));
    }

    @Test
    @DisplayName("removeLead: campaign and lead found - Success")
    void removeLeadWhenCampaignAndLeadAndSequenceFoundReturnsSuccess() {
        // Set up mocked entities
        String CAMPAIGN_ID = getMockFactory().getComponentId();
        String LEAD_ID = getMockFactory().getFAKER().internet().uuid();
        String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();
        Campaign campaignEntity = getMockFactory().newEntity(CAMPAIGN_ID);
        CampaignLead campaignLead = getMockFactory().newCampaignLeadEntity(LEAD_ID, SEQUENCE_ID);
        campaignEntity.getLeads().add(campaignLead);

        // Set up the mocked repository and services
        doReturn(Optional.of(campaignEntity)).when(repository).findById(anyString());

        // Execute the service call
        CampaignLeadDTO campaignLeadDTO = getService().removeLead(CAMPAIGN_ID, LEAD_ID);

        // Assertions
        Assertions.assertAll("Campaign Lead component must be properly informed",
                () -> Assertions.assertNotNull(campaignLeadDTO, "Campaign lead can not be null"),
                () -> Assertions.assertNotNull(campaignLeadDTO.getLead(), "Campaign lead entity can not be null"),
                () -> Assertions.assertEquals(LEAD_ID, campaignLeadDTO.getLead().getId(), String.format("Campaign lead id must be [%s]", LEAD_ID)),
                () -> Assertions.assertNotNull(campaignLeadDTO.getSequence(), "Campaign sequence entity can not be null"),
                () -> Assertions.assertEquals(SEQUENCE_ID, campaignLeadDTO.getSequence().getId(), String.format("Campaign sequence id must be [%s]", SEQUENCE_ID)));

        verify(repository, atMostOnce()).findById(anyString());
        verify(leadRepository, never()).findById(anyString());
        verify(sequenceRepository, never()).findById(anyString());
        verify(repository, atMostOnce()).save(any(Campaign.class));
    }

    @Test
    @DisplayName("removeLead: campaign not found - Failure")
    void removeLeadWhenCampaignNotFoundFailure() {
        // Set up mocked entities
        String CAMPAIGN_ID = getMockFactory().getComponentId();
        String LEAD_ID = getMockFactory().getFAKER().internet().uuid();

        // Set up the mocked repository and services
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute the service call
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> getService().removeLead(CAMPAIGN_ID, LEAD_ID),
                "removeCampaignLead must throw ResourceNotFoundException");

        // Assertions
        Assertions.assertAll("Exception must be correct informed",
                () -> Assertions.assertEquals(
                        exception.getMessage(),
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION),
                () -> Assertions.assertEquals(2, exception.getArgs().length),
                () -> Assertions.assertTrue(
                        Arrays.asList(exception.getArgs()).contains(DBConstants.CAMPAIGN_DOCUMENT),
                        String.format("Exception arguments must contain [%s]", DBConstants.CAMPAIGN_DOCUMENT)));
        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, never()).save(any(Campaign.class));
    }

    @Test
    @DisplayName("removeLead: lead not present - Failure")
    void removeLeadWhenLeadNotPresentFailure() {
        // Set up mocked entities
        String CAMPAIGN_ID = getMockFactory().getComponentId();
        String LEAD_ID = getMockFactory().getFAKER().internet().uuid();
        Campaign campaignEntity = getMockFactory().newEntity(CAMPAIGN_ID);

        // Set up the mocked repository and services
        doReturn(Optional.of(campaignEntity)).when(repository).findById(anyString());

        // Execute the service call
        CommonServiceException exception = Assertions.assertThrows(
                CommonServiceException.class,
                () -> getService().removeLead(CAMPAIGN_ID, LEAD_ID),
                "removeCampaignLead must throw CommonServiceException");

        Assertions.assertEquals(
                exception.getMessage(),
                ExceptionMessageConstants.CAMPAIGN_REMOVE_LEAD_NOT_PRESENT_EXCEPTION);
        Assertions.assertEquals(2, exception.getArgs().length);

        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, never()).save(any(Campaign.class));
    }

    @Test
    @DisplayName("findAllLeads: campaign not found - Failure")
    void findAllLeadsWhenCampaignNotFoundFailure() {
        // Set up mocked entities
        String CAMPAIGN_ID = getMockFactory().getComponentId();

        // Set up the mocked repository and services
        doReturn(Optional.empty()).when(repository).findById(anyString());

        // Execute the service call
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> getService().findAllLeads(CAMPAIGN_ID),
                "findAllLeads must throw ResourceNotFoundException");

        // Assertions
        Assertions.assertAll("Exception must be correct informed",
                () -> Assertions.assertEquals(
                        exception.getMessage(),
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION),
                () -> Assertions.assertEquals(2, exception.getArgs().length),
                () -> Assertions.assertTrue(
                        Arrays.asList(exception.getArgs()).contains(DBConstants.CAMPAIGN_DOCUMENT),
                        String.format("Exception arguments must contain [%s]", DBConstants.CAMPAIGN_DOCUMENT)));
        verify(repository, atMostOnce()).findById(anyString());
        verify(repository, never()).save(any(Campaign.class));
    }

    @Test
    @DisplayName("findAllLeads: campaign found - Success")
    void findAllLeadsWhenCampaignNotFoundSuccess() {
        // Set up mocked entities
        String CAMPAIGN_ID = getMockFactory().getComponentId();
        Campaign campaignEntity = getMockFactory().newEntity(CAMPAIGN_ID);
        int LIST_SIZE = 4;
        List<CampaignLead> leads = IntStream.range(0, LIST_SIZE).mapToObj((l) -> {
            String LEAD_ID = getMockFactory().getFAKER().internet().uuid();
            String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();
           return getMockFactory().newCampaignLeadEntity(LEAD_ID, SEQUENCE_ID);
        }).collect(Collectors.toList());
        campaignEntity.getLeads().addAll(leads);

        // Set up the mocked repository and services
        doReturn(Optional.of(campaignEntity)).when(repository).findById(anyString());

        // Execute the service call
        List<CampaignLeadDTO> leadList = getService().findAllLeads(CAMPAIGN_ID);

        Assertions.assertFalse(leadList.isEmpty(), "List of campaign lead can not be empty");
    }
}
