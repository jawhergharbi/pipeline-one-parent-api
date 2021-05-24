package com.sawoo.pipeline.api.repository.campaign;

import com.sawoo.pipeline.api.mock.CampaignMockFactory;
import com.sawoo.pipeline.api.model.campaign.Campaign;
import com.sawoo.pipeline.api.model.campaign.CampaignStatus;
import com.sawoo.pipeline.api.repository.base.BaseRepositoryTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tags(value = {@Tag(value = "data")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
class CampaignRepositoryTest extends BaseRepositoryTest<Campaign, CampaignRepository, CampaignMockFactory> {

    private static final String CAMPAIGN_JSON_DATA_FILE_NAME = "campaign-test-data.json";
    private static final String CAMPAIGN_ID = "60488a88959ceb1ce1e518fa";

    @Autowired
    public CampaignRepositoryTest(CampaignRepository repository, CampaignMockFactory mockFactory) {
        super(repository, CAMPAIGN_JSON_DATA_FILE_NAME, CAMPAIGN_ID, Campaign.class.getSimpleName(), mockFactory);
    }

    @Override
    protected Class<Campaign[]> getClazz() {
        return Campaign[].class;
    }

    @Override
    protected String getComponentId(Campaign component) {
        return component.getId();
    }

    @Override
    protected Campaign getNewEntity() {
        String ENTITY_ID = getMockFactory().getFAKER().internet().uuid();
        return getMockFactory().newEntity(ENTITY_ID);
    }

    @Test
    @DisplayName("findByStatus: campaigns with status 0 and 2 - Success")
    void findByStatusWhenStatusIsFoundReturnsSuccess() {
        // Arrange
        int ENTITIES_FOUND = 1;

        // Act
        List<Campaign> campaignsInProgress = getRepository().findByStatus(CampaignStatus.RUNNING);
        List<Campaign> campaignsPaused = getRepository().findByStatus(CampaignStatus.PAUSED);

        // Assert
        assertListOfCampaignsWithStatus(campaignsInProgress, ENTITIES_FOUND, CampaignStatus.RUNNING);
        assertListOfCampaignsWithStatus(campaignsPaused, 0, CampaignStatus.PAUSED);
    }

    @Test
    @DisplayName("findByComponentId: campaigns by component id and entities found - Success")
    void findByComponentIdWhenEntitiesFoundReturnsSuccess() {
        // Arrange
        String COMPONENT_ID = "6030d640f3022dc07d72d786";
        int ENTITIES_FOUND = 2;

        // Act
        List<Campaign> campaigns = getRepository().findByComponentId(COMPONENT_ID);

        // Assert
        assertListOfCampaigns(campaigns, ENTITIES_FOUND);
    }

    @Test
    @DisplayName("findByComponentIdIn: campaigns by component id and entities found - Success")
    void findByComponentIdInWhenEntitiesFoundReturnsSuccess() {
        // Arrange
        String COMPONENT_ID_1 = "6030d640f3022dc07d72d786";
        String COMPONENT_ID_2 = "6030d65af796188aabff390b";
        int ENTITIES_FOUND = 3;

        // Act
        List<Campaign> campaigns = getRepository().findByComponentIdIn(new HashSet<>(Arrays.asList(COMPONENT_ID_1, COMPONENT_ID_2)));

        // Assert
        assertListOfCampaigns(campaigns, ENTITIES_FOUND);
    }

    @Test
    @DisplayName("findByComponentIdIn: campaigns by component id and entities not found - Success")
    void findByComponentIdInWhenEntitiesNotFoundReturnsSuccess() {
        // Arrange
        String COMPONENT_ID_1 = "wrong_id_1";
        String COMPONENT_ID_2 = "wrong_id_2";
        int ENTITIES_FOUND = 0;

        // Act
        List<Campaign> campaigns = getRepository().findByComponentIdIn(new HashSet<>(Arrays.asList(COMPONENT_ID_1, COMPONENT_ID_2)));

        // Assert
        assertListOfCampaigns(campaigns, ENTITIES_FOUND);
    }

    @Test
    @DisplayName("findByComponentId: campaigns by component id and status when entities found - Success")
    void findByComponentIdAndStatusWhenEntitiesFoundReturnsSuccess() {
        // Arrange
        String COMPONENT_ID = "6030d640f3022dc07d72d786";
        int ENTITIES_FOUND = 1;

        // Act
        List<Campaign> campaigns = getRepository().findByComponentIdAndStatus(COMPONENT_ID, CampaignStatus.RUNNING);

        // Assert
        assertListOfCampaignsWithStatus(campaigns, ENTITIES_FOUND, CampaignStatus.RUNNING);
    }


    @Test
    @DisplayName("findByComponentId: campaigns by component id and entities found - Success")
    void findByComponentIdInAndStatusWhenEntitiesFoundReturnsSuccess() {
        // Arrange
        String COMPONENT_ID_1 = "6030d640f3022dc07d72d786";
        String COMPONENT_ID_2 = "6030d65af796188aabff390b";
        int ENTITIES_FOUND = 1;

        // Act
        List<Campaign> campaigns = getRepository().findByComponentIdInAndStatus(
                new HashSet<>(Arrays.asList(COMPONENT_ID_1, COMPONENT_ID_2)),
                CampaignStatus.NOT_STARTED);

        // Assert
        assertListOfCampaignsWithStatus(campaigns, ENTITIES_FOUND, CampaignStatus.NOT_STARTED);
    }

    @Test
    @DisplayName("findByComponentIdAndName: campaign by component id and name when entity found - Success")
    void findByComponentIdAndNameWhenEntityFoundReturnsSuccess() {
        // Arrange
        String COMPONENT_ID = "6030d640f3022dc07d72d786";
        String NAME = "Campaign for CTOs";

        // Act
        Optional<Campaign> campaign = getRepository().findByComponentIdAndName(COMPONENT_ID, NAME);

        // Assert
        Assertions.assertTrue(campaign.isPresent(), String.format("Campaign with id: [%s] must be found", COMPONENT_ID));
    }

    @Test
    @DisplayName("findByComponentIdAndName: campaign by component id and name when entity not found - Failure")
    void findByComponentIdAndNameWhenEntityNotFoundReturnsFailure() {
        // Arrange
        String COMPONENT_ID = "6030d640f3022dc07d72d786";
        String NAME = "Campaign for CTO";

        // Act
        Optional<Campaign> campaign = getRepository().findByComponentIdAndName(COMPONENT_ID, NAME);

        // Assert
        Assertions.assertTrue(campaign.isEmpty(), String.format("Campaign with id: [%s] can not be found", COMPONENT_ID));
    }

    private void assertListOfCampaigns(List<Campaign> campaigns, int expectedSize) {
        if (expectedSize > 0) {
            Assertions.assertFalse(campaigns.isEmpty(), "Campaign list can not be empty");
            Assertions.assertEquals(expectedSize, campaigns.size(), String.format("Campaign list size must be [%d]", expectedSize));
        } else {
            Assertions.assertTrue(campaigns.isEmpty(), "Campaign list must be empty");
        }
    }

    private void assertListOfCampaignsWithStatus(List<Campaign> campaigns, int expectedSize, CampaignStatus status) {
        if (expectedSize > 0) {
            Assertions.assertFalse(
                    campaigns.isEmpty(),
                    String.format("Campaign list for status [%s] can not be not empty", status));
            Assertions.assertEquals(
                    expectedSize,
                    campaigns.size(),
                    String.format("Campaign list size for status [%s] must be [%d]", status, expectedSize));
        } else {
            Assertions.assertTrue(
                    campaigns.isEmpty(),
                    String.format("Campaign list for status [%s] must be not empty", status));
        }
    }
}
