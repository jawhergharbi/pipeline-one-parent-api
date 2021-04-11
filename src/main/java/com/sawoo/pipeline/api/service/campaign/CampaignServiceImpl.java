package com.sawoo.pipeline.api.service.campaign;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignProspectDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignProspectAddDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignProspectBaseDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignProspectCreateDTO;
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
    private final CampaignProspectService campaignProspectService;

    @Autowired
    public CampaignServiceImpl(CampaignRepository repository,
                               CampaignMapper mapper,
                               ApplicationEventPublisher publisher,
                               CampaignAccountService campaignAccountService,
                               CampaignProspectService campaignProspectService,
                               AuditService audit) {
        super(repository, mapper, DBConstants.CAMPAIGN_DOCUMENT, publisher, audit);
        this.campaignAccountService = campaignAccountService;
        this.campaignProspectService = campaignProspectService;
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
    public CampaignProspectDTO createProspect(String campaignId, CampaignProspectCreateDTO campaignProspect)
            throws ResourceNotFoundException, CommonServiceException {
        return campaignProspectService.createProspect(campaignId, campaignProspect);
    }

    @Override
    public CampaignProspectDTO addProspect(String campaignId, CampaignProspectAddDTO campaignProspect)
            throws ResourceNotFoundException, CommonServiceException {
        return campaignProspectService.addProspect(campaignId, campaignProspect);
    }

    @Override
    public CampaignProspectDTO removeProspect(String campaignId, String prospectId, List<String> todoIds)
            throws ResourceNotFoundException, CommonServiceException {
        return campaignProspectService.removeProspect(campaignId, prospectId, todoIds);
    }

    @Override
    public CampaignProspectDTO updateProspect(String campaignId, String prospectId, CampaignProspectBaseDTO campaignProspect)
            throws ResourceNotFoundException, CommonServiceException {
        return campaignProspectService.updateProspect(campaignId, prospectId, campaignProspect);
    }

    @Override
    public List<CampaignProspectDTO> findAllProspects(String campaignId)
            throws ResourceNotFoundException, CommonServiceException {
        return campaignProspectService.findAllProspects(campaignId);
    }
}
