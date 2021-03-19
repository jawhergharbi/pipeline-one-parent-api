package com.sawoo.pipeline.api.service.campaign;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignLeadAddDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignLeadDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.campaign.Campaign;
import com.sawoo.pipeline.api.repository.campaign.CampaignRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
import com.sawoo.pipeline.api.service.infra.audit.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Validated
@Primary
public class CampaignServiceImpl extends BaseServiceImpl<CampaignDTO, Campaign, CampaignRepository, CampaignMapper> implements CampaignService {

    private final CampaignAccountService campaignAccountService;
    private final CampaignLeadService campaignLeadService;

    @Autowired
    public CampaignServiceImpl(CampaignRepository repository,
                               CampaignMapper mapper,
                               ApplicationEventPublisher publisher,
                               CampaignAccountService campaignAccountService,
                               CampaignLeadService campaignLeadService,
                               AuditService audit) {
        super(repository, mapper, DBConstants.CAMPAIGN_DOCUMENT, publisher, audit);
        this.campaignAccountService = campaignAccountService;
        this.campaignLeadService = campaignLeadService;
    }

    @Override
    public Optional<Campaign> entityExists(CampaignDTO entityToCreate) {
        log.debug(
                "Checking entity existence. [type: {}, name: {}, componentId: {}]",
                DBConstants.CAMPAIGN_DOCUMENT,
                entityToCreate.getName(),
                entityToCreate.getComponentId());
        return getRepository().findByComponentIdAndName(entityToCreate.getComponentId(), entityToCreate.getName());
    }

    @Override
    public List<CampaignDTO> findByAccountIds(Set<String> accountIds) throws CommonServiceException {
        return campaignAccountService.findByAccountIds(accountIds);
    }

    @Override
    public CampaignLeadDTO addLead(String campaignId, CampaignLeadAddDTO campaignLead)
            throws ResourceNotFoundException, CommonServiceException {
        return campaignLeadService.addLead(campaignId, campaignLead);
    }

    @Override
    public CampaignLeadDTO removeLead(String campaignId, String leadId)
            throws ResourceNotFoundException, CommonServiceException {
        return campaignLeadService.removeLead(campaignId, leadId);
    }

    @Override
    public List<CampaignLeadDTO> findAllLeads(String campaignId)
            throws ResourceNotFoundException, CommonServiceException {
        return campaignLeadService.findAllLeads(campaignId);
    }
}
