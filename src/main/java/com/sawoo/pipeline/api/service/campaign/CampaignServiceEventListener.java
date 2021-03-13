package com.sawoo.pipeline.api.service.campaign;

import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.model.campaign.Campaign;
import com.sawoo.pipeline.api.model.campaign.CampaignStatus;
import com.sawoo.pipeline.api.service.base.event.BaseServiceBeforeInsertEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CampaignServiceEventListener {

    @EventListener
    public void handleBeforeInsertEvent(BaseServiceBeforeInsertEvent<CampaignDTO, Campaign> event) {
        log.debug("Campaign before insert listener");
        CampaignDTO dto = event.getDto();
        Campaign entity = event.getModel();
        if (dto.getStatus() == null) {
            entity.setStatus(CampaignStatus.UNDER_CONSTRUCTION);
        }
    }
}
