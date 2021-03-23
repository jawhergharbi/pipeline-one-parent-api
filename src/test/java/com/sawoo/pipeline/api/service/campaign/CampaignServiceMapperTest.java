package com.sawoo.pipeline.api.service.campaign;

import com.googlecode.jmapper.JMapper;
import com.googlecode.jmapper.api.enums.MappingType;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignLeadBaseDTO;
import com.sawoo.pipeline.api.mock.CampaignMockFactory;
import com.sawoo.pipeline.api.model.campaign.CampaignLead;
import com.sawoo.pipeline.api.model.campaign.CampaignLeadStatus;
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
public class CampaignServiceMapperTest {

    @Getter
    @Autowired
    private CampaignMockFactory mockFactory;

    @Test
    @DisplayName("updateLead: mapping only new status - Success")
    void updateLeadWhenMappingOnlyStatusSuccess() {
        String LEAD_ID = getMockFactory().getFAKER().internet().uuid();
        String SEQUENCE_ID = getMockFactory().getFAKER().internet().uuid();
        CampaignLead campaignLead = getMockFactory().newCampaignLeadEntity(LEAD_ID, SEQUENCE_ID);

        CampaignLeadBaseDTO campaignLeadBase = CampaignLeadBaseDTO
                .builder()
                .status(CampaignLeadStatus.ARCHIVED.getValue())
                .build();

        JMapper<CampaignLead, CampaignLeadBaseDTO> mapper = new JMapper<>(CampaignLead.class, CampaignLeadBaseDTO.class);
        campaignLead = mapper.getDestination(
                campaignLead,
                campaignLeadBase,
                MappingType.ALL_FIELDS,
                MappingType.ONLY_VALUED_FIELDS);

        Assertions.assertEquals(
                CampaignLeadStatus.ARCHIVED,
                campaignLead.getStatus(),
                String.format("Campaign Lead status must be [%s]", CampaignLeadStatus.ARCHIVED));
    }
}
