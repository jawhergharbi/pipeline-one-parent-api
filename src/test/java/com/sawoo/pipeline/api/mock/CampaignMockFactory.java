package com.sawoo.pipeline.api.mock;

import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignProspectDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignProspectAddDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignProspectCreateDTO;
import com.sawoo.pipeline.api.model.campaign.Campaign;
import com.sawoo.pipeline.api.model.campaign.CampaignProspect;
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
    private final ProspectMockFactory prospectMockFactory;
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

    public CampaignProspectAddDTO newCampaignProspectAddDTO() {
        String PROSPECT_ID = getFAKER().internet().uuid();
        String SEQUENCE_ID = getFAKER().internet().uuid();
        return newCampaignProspectAddDTO(PROSPECT_ID, SEQUENCE_ID);
    }

    public CampaignProspectAddDTO newCampaignProspectAddDTO(String prospectId, String sequenceId) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        return CampaignProspectAddDTO.builder()
                .prospectId(prospectId)
                .sequenceId(sequenceId)
                .startDate(now)
                .endDate(now.plusDays(20))
                .build();
    }

    public CampaignProspectAddDTO newCampaignProspectAddDTO(String sequenceId) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        return CampaignProspectAddDTO.builder()
                .sequenceId(sequenceId)
                .startDate(now)
                .endDate(now.plusDays(20))
                .build();
    }

    public CampaignProspectDTO newCampaignProspectDTO(String prospectId, String sequenceId) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        return CampaignProspectDTO.builder()
                .prospect(prospectMockFactory.newDTO(prospectId))
                .sequence(sequenceMockFactory.newDTO(sequenceId))
                .startDate(now)
                .endDate(now.plusDays(10))
                .build();
    }

    public CampaignProspectCreateDTO newCampaignProspectCreateDTO(String accountId, String sequenceId) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        String LEAD_ID = getFAKER().internet().uuid();
        return CampaignProspectCreateDTO.builder()
                .prospect(prospectMockFactory.newDTO(LEAD_ID))
                .sequenceId(sequenceId)
                .accountId(accountId)
                .startDate(now)
                .endDate(now.plusDays(10))
                .build();
    }

    public CampaignProspect newCampaignProspectEntity() {
        String prospectId = getFAKER().internet().uuid();
        String sequenceId = getFAKER().internet().uuid();
        return newCampaignProspectEntity(prospectId, sequenceId);
    }

    public CampaignProspect newCampaignProspectEntity(String prospectId, String sequenceId) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        return CampaignProspect.builder()
                .prospect(prospectMockFactory.newEntity(prospectId))
                .sequence(sequenceMockFactory.newEntity(sequenceId))
                .startDate(now)
                .endDate(now.plusDays(10))
                .build();
    }
}
