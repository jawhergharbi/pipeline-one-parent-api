package com.sawoo.pipeline.api.service.campaign;

import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.mock.CampaignMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.campaign.Campaign;
import com.sawoo.pipeline.api.repository.campaign.CampaignRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CampaignServiceTest extends BaseServiceTest<CampaignDTO, Campaign, CampaignRepository, CampaignService, CampaignMockFactory> {

    @MockBean
    private CampaignRepository repository;

    @Autowired
    public CampaignServiceTest(CampaignMockFactory mockFactory, CampaignService service) {
        super(mockFactory, DBConstants.CAMPAIGN_DOCUMENT, service);
    }

    @Override
    protected String getEntityId(Campaign component) {
        return component.getId();
    }

    @Override
    protected String getDTOId(CampaignDTO component) {
        return component.getId();
    }

    @Override
    protected void mockedEntityExists(Campaign entity) {
        doReturn(Optional.of(entity)).when(repository).findByComponentIdAndName(anyString(), anyString());
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }
}
