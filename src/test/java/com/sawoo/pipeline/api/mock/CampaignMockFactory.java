package com.sawoo.pipeline.api.mock;

import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignLeadAddDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignLeadDTO;
import com.sawoo.pipeline.api.model.campaign.Campaign;
import com.sawoo.pipeline.api.model.campaign.CampaignLead;
import com.sawoo.pipeline.api.model.campaign.CampaignStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Getter
@Component
@RequiredArgsConstructor
public class CampaignMockFactory extends BaseMockFactory<CampaignDTO, Campaign> {

    private final AccountMockFactory accountMockFactory;
    private final LeadMockFactory leadMockFactory;
    private final SequenceMockFactory sequenceMockFactory;

    @Override
    public String getComponentId() {
        return getFAKER().internet().uuid();
    }

    @Override
    public Campaign newEntity(String id) {
        Faker FAKER = getFAKER();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        return Campaign
                .builder()
                .id(id)
                .name(FAKER.funnyName().name())
                .description(FAKER.lebowski().quote())
                .status(CampaignStatus.RUNNING)
                .componentId(FAKER.internet().uuid())
                .created(now)
                .updated(now)
                .startDate(now.plusDays(10))
                .endDate(now.plusDays(40))
                .actualStartDate(now.plusDays(5))
                .build();
    }

    @Override
    public CampaignDTO newDTO(String id) {
        Faker FAKER = getFAKER();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        CampaignDTO dto = CampaignDTO
                .builder()
                .id(id)
                .name(FAKER.funnyName().name())
                .status(CampaignStatus.RUNNING.getValue())
                .componentId(FAKER.internet().uuid())
                .description(FAKER.lebowski().quote())
                .startDate(now.plusDays(10))
                .endDate(now.plusDays(40))
                .actualStartDate(now.plusDays(5))
                .build();
        dto.setCreated(now);
        dto.setUpdated(now);
        return dto;
    }

    @Override
    public CampaignDTO newDTO(String id, CampaignDTO dto) {
        return dto.toBuilder().id(id).build();
    }

    public CampaignLeadAddDTO newCampaignLeadAddDTO() {
        String LEAD_ID = getFAKER().internet().uuid();
        String SEQUENCE_ID = getFAKER().internet().uuid();
        return newCampaignLeadAddDTO(LEAD_ID, SEQUENCE_ID);
    }

    public CampaignLeadAddDTO newCampaignLeadAddDTO(String leadId, String sequenceId) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        return CampaignLeadAddDTO.builder()
                .leadId(leadId)
                .sequenceId(sequenceId)
                .startDate(now)
                .endDate(now.plusDays(20))
                .build();
    }

    public CampaignLeadDTO newCampaignLeadDTO(String leadId, String sequenceId) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        return CampaignLeadDTO.builder()
                .lead(leadMockFactory.newDTO(leadId))
                .sequence(sequenceMockFactory.newDTO(sequenceId))
                .startDate(now)
                .endDate(now.plusDays(10))
                .build();
    }

    public CampaignLeadDTO newCampaignLeadDTO() {
        String leadId = getComponentId();
        String sequenceId = getComponentId();
        return newCampaignLeadDTO(leadId, sequenceId);
    }

    public CampaignLead newCampaignLeadEntity() {
        String leadId = getComponentId();
        String sequenceId = getComponentId();
        return newCampaignLeadEntity(leadId, sequenceId);
    }

    public CampaignLead newCampaignLeadEntity(String leadId, String sequenceId) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        return CampaignLead.builder()
                .lead(leadMockFactory.newEntity(leadId))
                .sequence(sequenceMockFactory.newEntity(sequenceId))
                .startDate(now)
                .endDate(now.plusDays(10))
                .build();
    }
}
