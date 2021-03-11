package com.sawoo.pipeline.api.controller.campaign;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerDelegator;
import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.service.campaign.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CampaignControllerDelegator extends BaseControllerDelegator<CampaignDTO, CampaignService> {

    @Autowired
    public CampaignControllerDelegator(CampaignService service) {
        super(service, ControllerConstants.CAMPAIGN_CONTROLLER_API_BASE_URI);
    }

    @Override
    public String getComponentId(CampaignDTO dto) {
        return dto.getId();
    }
}
