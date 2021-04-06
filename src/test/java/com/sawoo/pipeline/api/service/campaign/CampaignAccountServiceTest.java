package com.sawoo.pipeline.api.service.campaign;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.mock.CampaignMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.campaign.Campaign;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
import com.sawoo.pipeline.api.repository.campaign.CampaignRepository;
import com.sawoo.pipeline.api.service.base.BaseLightServiceTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doReturn;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CampaignAccountServiceTest extends BaseLightServiceTest<CampaignDTO, Campaign, CampaignRepository, CampaignService, CampaignMockFactory> {

    @MockBean
    private CampaignRepository repository;

    @MockBean
    private AccountRepository accountRepository;

    @Autowired
    public CampaignAccountServiceTest(CampaignMockFactory mockFactory, CampaignService service) {
        super(mockFactory, DBConstants.CAMPAIGN_DOCUMENT, service);
    }

    @BeforeAll
    public void setup() {
        setRepository(repository);
    }

    @Test
    @DisplayName("findByAccountIds: campaign is present - Success")
    void findByAccountIdsWhenSequenceFoundPresentReturnsSuccess() {
        // Set up mocked entities
        String CAMPAIGN_ID = getMockFactory().getComponentId();
        Campaign mockedEntity = getMockFactory().newEntity(CAMPAIGN_ID);
        String COMPONENT_ID = mockedEntity.getComponentId();
        Account mockedAccount = getMockFactory().getAccountMockFactory().newEntity(COMPONENT_ID);

        // Set up the mocked repository
        doReturn(Collections.singletonList(mockedEntity)).when(repository).findByComponentIdIn(anySet());
        doReturn(Collections.singletonList(mockedAccount)).when(accountRepository).findAllById(anySet());

        // Execute the service call
        List<CampaignDTO> campaigns = getService().findByAccountIds(new HashSet<>(Collections.singleton(COMPONENT_ID)));

        // Assertions
        Assertions.assertAll(String.format("Campaign list must contain 1 sequence with id [%s]", COMPONENT_ID),
                () -> Assertions.assertFalse(campaigns.isEmpty(), "sequence list can not be empty"),
                () -> {
                    CampaignDTO campaign = campaigns.get(0);
                    Assertions.assertEquals(CAMPAIGN_ID, campaign.getId(), String.format("Sequence id must be [%s]", CAMPAIGN_ID));
                    Assertions.assertNotNull(campaign.getComponentId(), "Component id can not be null");
                    Assertions.assertEquals(COMPONENT_ID, campaign.getComponentId(), String.format("Campaign component id must be [%s]", COMPONENT_ID));
                    Assertions.assertNotNull(campaign.getAccount(), "Account can not be null");
                });
    }

    @Test
    @DisplayName("findByAccountIds: account ids is empty - Failure")
    void findByAccountIdsWhenAccountIdsListIsEmptyReturnsFailure() {

        CampaignService service = getService();
        Exception exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> service.findByAccountIds(new HashSet<>()),
                "findByAccountIds must throw ConstraintViolationException");

        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_LIST_FIELD_CAN_NOT_BE_EMPTY_ERROR).matches(exception.getMessage()));
    }
}
