package com.sawoo.pipeline.api.service.campaign;

import com.googlecode.jmapper.JMapper;
import com.googlecode.jmapper.api.enums.MappingType;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.campaign.CampaignProspectDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignProspectAddDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignProspectBaseDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignProspectCreateDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.campaign.Campaign;
import com.sawoo.pipeline.api.model.campaign.CampaignProspect;
import com.sawoo.pipeline.api.model.campaign.CampaignProspectStatus;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.repository.sequence.SequenceRepository;
import com.sawoo.pipeline.api.service.account.AccountProspectService;
import com.sawoo.pipeline.api.service.prospect.ProspectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CampaignProspectServiceDecorator implements CampaignProspectService {

    private final CampaignService campaignService;
    private final ProspectService prospectService;
    private final AccountProspectService accountProspectService;
    private final SequenceRepository sequenceRepository;

    @Autowired
    public CampaignProspectServiceDecorator(
            @Lazy CampaignService campaignService,
            AccountProspectService accountProspectService,
            ProspectService prospectService,
            SequenceRepository sequenceRepository) {
        this.campaignService = campaignService;
        this.accountProspectService = accountProspectService;
        this.prospectService = prospectService;
        this.sequenceRepository = sequenceRepository;
    }

    @Override
    public CampaignProspectDTO createProspect(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId,
            @Valid CampaignProspectCreateDTO campaignProspect) throws ResourceNotFoundException, CommonServiceException {

        log.debug("Create prospect with person [{}] to campaign id [{}] and using the sequence id [{}]",
                campaignId,
                campaignProspect.getProspect().getPerson().getFullName(),
                campaignProspect.getSequenceId());

        Campaign campaign = findCampaignById(campaignId);
        ProspectDTO prospectCreated = accountProspectService.createProspect(campaignProspect.getAccountId(), campaignProspect.getProspect());

        CampaignProspectAddDTO addCampaignProspect = CampaignProspectAddDTO.builder()
                .prospectId(prospectCreated.getId())
                .sequenceId(campaignProspect.getSequenceId())
                .status(campaignProspect.getStatus())
                .startDate(campaignProspect.getStartDate())
                .endDate(campaignProspect.getEndDate())
                .build();

        return addProspect(campaign, addCampaignProspect);
    }

    @Override
    public CampaignProspectDTO addProspect(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId,
            @Valid CampaignProspectAddDTO campaignProspect) throws ResourceNotFoundException, CommonServiceException {
        log.debug("Add prospect with id [{}] to campaign id [{}] and using the sequence id [{}]",
                campaignId,
                campaignProspect.getProspectId(),
                campaignProspect.getSequenceId());

        Campaign campaign = findCampaignById(campaignId);

        return addProspect(campaign, campaignProspect);
    }

    @Override
    public CampaignProspectDTO removeProspect(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String prospectId) {
        log.debug("Remove prospect with id [{}] to campaign id [{}]", campaignId, prospectId);

        Campaign campaign = findCampaignById(campaignId);

        return campaign.getProspects()
                .stream()
                .filter(p -> prospectId.equals(p.getProspect().getId()))
                .findFirst()
                .map(prospect -> {
                    log.debug("Prospect id [{}] for campaign id [{}] has been found", prospectId, campaignId);

                    removeCampaignProspect(campaign, prospect);
                    campaign.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
                    campaignService.getRepository().save(campaign);

                    return campaignService.getMapper().getMapperProspectCampaignOut().getDestination(prospect);
                })
                .orElseThrow(() -> new CommonServiceException(
                        ExceptionMessageConstants.CAMPAIGN_REMOVE_PROSPECT_NOT_PRESENT_EXCEPTION,
                        new Object[] {prospectId, campaignId}));
    }

    @Override
    public CampaignProspectDTO updateProspect(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String prospectId,
            @Valid CampaignProspectBaseDTO campaignProspect) throws ResourceNotFoundException, CommonServiceException {
        log.debug("Update prospect with id [{}] to campaign id [{}]", campaignId, prospectId);

        Campaign campaign = findCampaignById(campaignId);

        return campaign.getProspects()
                .stream()
                .filter(p -> prospectId.equals(p.getProspect().getId()))
                .findFirst()
                .map(prospect -> {
                    log.debug("Prospect id [{}] for campaign id [{}] has been found", prospectId, campaignId);

                    updateCampaignProspect(campaignProspect, prospect);
                    campaign.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
                    campaignService.getRepository().save(campaign);

                    return campaignService.getMapper().getMapperProspectCampaignOut().getDestination(prospect);
                })
                .orElseThrow(() -> new CommonServiceException(
                        ExceptionMessageConstants.CAMPAIGN_UPDATE_PROSPECT_NOT_PRESENT_EXCEPTION,
                        new Object[] {prospectId, campaignId}));
    }

    @Override
    public List<CampaignProspectDTO> findAllProspects(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId)
            throws ResourceNotFoundException, CommonServiceException {
        log.debug("Find all prospects for campaign id [{}]", campaignId);

        Campaign campaign = findCampaignById(campaignId);
        return campaign
                .getProspects()
                .stream()
                .map(l -> campaignService.getMapper().getMapperProspectCampaignOut().getDestination(l))
                .collect(Collectors.toList());
    }


    private CampaignProspectDTO addProspect(Campaign campaign, CampaignProspectAddDTO campaignProspect) {

        // Check whether prospect is already added to the campaign
        if (campaign.getProspects()
                .stream()
                .anyMatch(l -> campaignProspect.getProspectId().equals(l.getProspect().getId()))) {
            throw new CommonServiceException(
                    ExceptionMessageConstants.CAMPAIGN_ADD_PROSPECT_ALREADY_ADDED_EXCEPTION,
                    new Object[] {campaignProspect.getProspectId(), campaign.getId()});
        }

        Prospect prospect = findProspectById(campaignProspect.getProspectId());
        Sequence sequence = findSequenceById(campaignProspect.getSequenceId());

        log.debug("Prospect id [{}] and sequence id [{}] correctly found", campaignProspect.getProspectId(), campaignProspect.getSequenceId());

        // Create campaign prospect
        CampaignProspect campaignProspectEntity = createCampaignProspect(campaign, prospect, sequence, campaignProspect);
        log.debug("Campaign prospect has been correctly created. Campaign Prospect: [{}]", campaignProspectEntity);

        // Create todos based on the sequence
        prospectService.createTODOs(campaignProspect.getProspectId(), campaignProspect.getSequenceId(), campaignProspect.getAssigneeId());

        return campaignService.getMapper().getMapperProspectCampaignOut().getDestination(campaignProspectEntity);
    }

    private void updateCampaignProspect(CampaignProspectBaseDTO campaignProspect, CampaignProspect prospect) {
        BiConsumer<CampaignProspectBaseDTO, CampaignProspect> f = (d, m) -> new JMapper<>(CampaignProspect.class, CampaignProspectBaseDTO.class)
                .getDestination(m, d, MappingType.ALL_FIELDS, MappingType.ONLY_VALUED_FIELDS);
        f.accept(campaignProspect, prospect);
    }

    private void removeCampaignProspect(Campaign campaign, CampaignProspect prospect) {
        BiConsumer<Campaign, CampaignProspect> f = (c, l) -> c.getProspects().remove(l);
        f.accept(campaign, prospect);
    }

    private Campaign findCampaignById(String campaignId) throws ResourceNotFoundException {
        return campaignService
                .getRepository()
                .findById(campaignId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.CAMPAIGN_DOCUMENT, campaignId }));
    }

    private Sequence findSequenceById(String sequenceId) throws ResourceNotFoundException {
        return sequenceRepository
                .findById(sequenceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.SEQUENCE_DOCUMENT, sequenceId }));
    }

    private Prospect findProspectById(String prospectId) throws ResourceNotFoundException {
        return prospectService
                .getRepository()
                .findById(prospectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.PROSPECT_DOCUMENT, prospectId }));
    }

    private CampaignProspect createCampaignProspect(Campaign campaign, Prospect prospect, Sequence sequence, CampaignProspectAddDTO campaignProspect) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        CampaignProspect campaignProspectEntity = CampaignProspect.builder()
                .prospect(prospect)
                .sequence(sequence)
                .status(campaignProspect.getStatus() != null ? CampaignProspectStatus.fromValue(campaign.getStatus().getValue()) : CampaignProspectStatus.RUNNING)
                .startDate(campaignProspect.getStartDate())
                .endDate(campaignProspect.getEndDate())
                .created(now)
                .updated(now)
                .build();

        campaign.getProspects().add(campaignProspectEntity);
        campaign.setUpdated(now);
        campaignService.getRepository().save(campaign);

        return campaignProspectEntity;
    }
}
