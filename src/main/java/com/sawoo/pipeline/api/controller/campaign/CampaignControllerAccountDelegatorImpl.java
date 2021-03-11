package com.sawoo.pipeline.api.controller.campaign;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.service.campaign.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

@Component
@Qualifier("campaignAccountController")
public class CampaignControllerAccountDelegatorImpl implements CampaignControllerAccountDelegator {

    private final CampaignService service;

    @Autowired
    public CampaignControllerAccountDelegatorImpl(CampaignService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<List<CampaignDTO>> findByAccounts(
            @NotEmpty(message = ExceptionMessageConstants.COMMON_LIST_FIELD_CAN_NOT_BE_EMPTY_ERROR) Set<String> accountIds)
            throws CommonServiceException {
        return ResponseEntity.ok().body(service.findByAccountIds(accountIds));
    }
}
