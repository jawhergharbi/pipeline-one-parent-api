package com.sawoo.pipeline.api.service.campaign;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.campaign.CampaignLeadAddDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignLeadDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.campaign.Campaign;
import com.sawoo.pipeline.api.model.campaign.CampaignLead;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.repository.lead.LeadRepository;
import com.sawoo.pipeline.api.repository.sequence.SequenceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CampaignLeadServiceDecorator implements CampaignLeadService {

    private final CampaignService campaignService;
    private final LeadRepository leadRepository;
    private final SequenceRepository sequenceRepository;

    @Autowired
    public CampaignLeadServiceDecorator(
            @Lazy CampaignService campaignService,
            LeadRepository leadRepository,
            SequenceRepository sequenceRepository) {
        this.campaignService = campaignService;
        this.leadRepository = leadRepository;
        this.sequenceRepository = sequenceRepository;
    }

    @Override
    public CampaignLeadDTO addLead(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId,
            @Valid CampaignLeadAddDTO campaignLead) throws ResourceNotFoundException, CommonServiceException {
        log.debug("Add lead with id [{}] to campaign id [{}] and using the sequence id [{}]",
                campaignId,
                campaignLead.getLeadId(),
                campaignLead.getSequenceId());

        Campaign campaign = findCampaignById(campaignId);

        // Check whether lead is already added to the campaign
        if (campaign.getLeads()
                .stream()
                .anyMatch(l -> campaignLead.getLeadId().equals(l.getLead().getId()))) {
            throw new CommonServiceException(
                    ExceptionMessageConstants.CAMPAIGN_ADD_LEAD_ALREADY_ADDED_EXCEPTION,
                    new Object[] {campaignLead.getLeadId(), campaignId});
        }

        Lead lead = findLeadById(campaignLead.getLeadId());
        Sequence sequence = findSequenceById(campaignLead.getSequenceId());

        log.debug("Lead id [{}] and sequence id [{}] correctly found", campaignLead.getLeadId(), campaignLead.getSequenceId());

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        CampaignLead campaignLeadEntity = CampaignLead.builder()
                .lead(lead)
                .sequence(sequence)
                .startDate(campaignLead.getStartDate())
                .endDate(campaignLead.getEndDate())
                .created(now)
                .updated(now)
                .build();

        campaign.getLeads().add(campaignLeadEntity);
        campaign.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
        campaignService.getRepository().save(campaign);

        return campaignService.getMapper().getMapperLeadCampaignOut().getDestination(campaignLeadEntity);
    }

    @Override
    public CampaignLeadDTO removeLead(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String leadId) {
        log.debug("Remove lead with id [{}] to campaign id [{}]", campaignId, leadId);

        Campaign campaign = findCampaignById(campaignId);

        return campaign.getLeads()
                .stream()
                .filter(l -> leadId.equals(l.getLead().getId()))
                .findFirst()
                .map((lead) -> {
                    log.debug("Lead id [{}] for campaign id [{}] has been found", leadId, campaignId);

                    campaign.getLeads().remove(lead);
                    campaign.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
                    campaignService.getRepository().save(campaign);

                    return campaignService.getMapper().getMapperLeadCampaignOut().getDestination(lead);
                })
                .orElseThrow(() -> new CommonServiceException(
                        ExceptionMessageConstants.CAMPAIGN_REMOVE_LEAD_NOT_PRESENT_EXCEPTION,
                        new Object[] {leadId, campaignId}));
    }

    @Override
    public List<CampaignLeadDTO> findAllLeads(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId)
            throws ResourceNotFoundException, CommonServiceException {
        log.debug("Find all leads for campaign id [{}]", campaignId);

        Campaign campaign = findCampaignById(campaignId);
        return campaign
                .getLeads()
                .stream()
                .map((l) -> campaignService.getMapper().getMapperLeadCampaignOut().getDestination(l))
                .collect(Collectors.toList());
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

    private Lead findLeadById(String leadId) throws ResourceNotFoundException {
        return leadRepository
                .findById(leadId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.LEAD_DOCUMENT, leadId }));
    }
}
