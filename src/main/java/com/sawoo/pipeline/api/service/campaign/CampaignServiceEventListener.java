package com.sawoo.pipeline.api.service.campaign;

import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.model.campaign.Campaign;
import com.sawoo.pipeline.api.model.campaign.CampaignStatus;
import com.sawoo.pipeline.api.service.base.BaseServiceEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CampaignServiceEventListener implements BaseServiceEventListener<CampaignDTO, Campaign> {

    @Override
    public void onBeforeInsert(CampaignDTO dto, Campaign entity) {
        if (dto.getStatus() == null) {
            entity.setStatus(CampaignStatus.UNDER_CONSTRUCTION);
        }
    }

    @Override
    public void onBeforeSave(CampaignDTO dto, Campaign entity) {
        // nothing to do atm
    }

    @Override
    public void onBeforeUpdate(CampaignDTO dto, Campaign entity) {
        // nothing to do atm
    }
}
