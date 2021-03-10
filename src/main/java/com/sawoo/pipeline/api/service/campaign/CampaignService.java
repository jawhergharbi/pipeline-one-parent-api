package com.sawoo.pipeline.api.service.campaign;

import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.service.base.BaseService;

import java.util.Optional;

public interface CampaignService extends BaseService<CampaignDTO> {

    Optional<CampaignDTO> findByComponentIdAndName(String componentId, String name);
}
