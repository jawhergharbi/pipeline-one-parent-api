package com.sawoo.pipeline.api.service.campaign;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignLeadDTO;
import com.sawoo.pipeline.api.model.campaign.Campaign;
import com.sawoo.pipeline.api.model.campaign.CampaignLead;
import com.sawoo.pipeline.api.service.base.BaseMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class CampaignMapper implements BaseMapper<CampaignDTO, Campaign> {

    private final JMapper<CampaignDTO, Campaign> mapperOut = new JMapper<>(CampaignDTO.class, Campaign.class);
    private final JMapper<Campaign, CampaignDTO> mapperIn = new JMapper<>(Campaign.class, CampaignDTO.class);

    private final JMapper<CampaignLeadDTO, CampaignLead> mapperLeadCampaignOut = new JMapper<>(CampaignLeadDTO.class, CampaignLead.class);
    private final JMapper<CampaignLead, CampaignLeadDTO> mapperLeadCampaignIn = new JMapper<>(CampaignLead.class, CampaignLeadDTO.class);
}
