package com.sawoo.pipeline.api.service.campaign;

import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.campaign.Campaign;
import com.sawoo.pipeline.api.repository.campaign.CampaignRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Validated
public class CampaignServiceImpl extends BaseServiceImpl<CampaignDTO, Campaign, CampaignRepository, CampaignMapper> implements CampaignService {

    @Autowired
    public CampaignServiceImpl(CampaignRepository repository, CampaignMapper mapper, CampaignServiceEventListener eventListener) {
        super(repository, mapper, DBConstants.CAMPAIGN_DOCUMENT, eventListener);
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
}
