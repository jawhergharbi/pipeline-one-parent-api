package com.sawoo.pipeline.api.mock;

import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.model.campaign.Campaign;
import com.sawoo.pipeline.api.model.campaign.CampaignStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class CampaignMockFactory extends BaseMockFactory<CampaignDTO, Campaign> {

    @Getter
    private final AccountMockFactory accountMockFactory;

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
}
