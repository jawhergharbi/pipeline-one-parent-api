package com.sawoo.pipeline.api.service.campaign;

import com.googlecode.jmapper.JMapper;
import com.googlecode.jmapper.api.enums.MappingType;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignProspectBaseDTO;
import com.sawoo.pipeline.api.mock.CampaignMockFactory;
import com.sawoo.pipeline.api.model.campaign.CampaignProspect;
import com.sawoo.pipeline.api.model.campaign.CampaignProspectStatus;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CampaignServiceMapperTest {

    @Getter
    @Autowired
    private CampaignMockFactory mockFactory;

    @Test
    @DisplayName("updateProspect: mapping only new status - Success")
    void updateProspectWhenMappingOnlyStatusReturnsSuccess() {
        String PROSPECT_ID = getMockFactory().getFAKER().internet().uuid();
        String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();
        CampaignProspect campaignProspect = getMockFactory().newCampaignProspectEntity(PROSPECT_ID, SEQUENCE_ID);

        CampaignProspectBaseDTO campaignProspectBase = CampaignProspectBaseDTO
                .builder()
                .status(CampaignProspectStatus.ARCHIVED.getValue())
                .build();

        JMapper<CampaignProspect, CampaignProspectBaseDTO> mapper = new JMapper<>(CampaignProspect.class, CampaignProspectBaseDTO.class);
        campaignProspect = mapper.getDestination(
                campaignProspect,
                campaignProspectBase,
                MappingType.ALL_FIELDS,
                MappingType.ONLY_VALUED_FIELDS);

        Assertions.assertEquals(
                CampaignProspectStatus.ARCHIVED,
                campaignProspect.getStatus(),
                String.format("Campaign Prospect status must be [%s]", CampaignProspectStatus.ARCHIVED));
    }
}
