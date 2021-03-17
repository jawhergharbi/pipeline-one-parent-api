package com.sawoo.pipeline.api.service.campaign;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.audit.VersionDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.campaign.Campaign;
import com.sawoo.pipeline.api.repository.campaign.CampaignRepository;
import com.sawoo.pipeline.api.service.infra.audit.AuditService;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
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
    private final AuditService audit;

    @Autowired
    public CampaignServiceImpl(CampaignRepository repository,
                               CampaignMapper mapper,
                               ApplicationEventPublisher publisher,
                               CampaignAccountService campaignAccountService,
                               AuditService audit) {
        super(repository, mapper, DBConstants.CAMPAIGN_DOCUMENT, publisher);
        this.campaignAccountService = campaignAccountService;
        this.audit = audit;
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
    public List<VersionDTO<CampaignDTO>> getVersions(String id) {
        return getRepository()
                .findById(id)
                .map( (entity) -> audit.getVersions(entity, id, getMapper().getMapperOut()))
                .orElse(null);
    }
}
