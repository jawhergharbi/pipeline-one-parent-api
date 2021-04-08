package com.sawoo.pipeline.api.service.campaign;

import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.model.campaign.Campaign;
import com.sawoo.pipeline.api.model.campaign.CampaignStatus;
import com.sawoo.pipeline.api.service.base.event.BaseServiceBeforeInsertEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Component
public class CampaignServiceEventListener {

    @EventListener
    public void handleBeforeInsertEvent(BaseServiceBeforeInsertEvent<CampaignDTO, Campaign> event) {
        CampaignDTO dto = event.getDto();
        Campaign entity = event.getModel();
        log.debug("Campaign before insert listener. Campaign name: {}", dto.getName());
        if (dto.getStatus() == null) {
            log.debug("Status set to default {} for campaign name: {}", CampaignStatus.NOT_STARTED, dto.getName());
            entity.setStatus(CampaignStatus.NOT_STARTED);
        }
        if (dto.getStartDate() == null) {
            LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
            log.debug("Start date set to the current datetime {} for campaign name: {}", now, dto.getName());
            entity.setStartDate(now);
        }
    }
}
