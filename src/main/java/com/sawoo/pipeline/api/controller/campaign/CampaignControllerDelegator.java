package com.sawoo.pipeline.api.controller.campaign;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerDelegator;
import com.sawoo.pipeline.api.dto.audit.VersionDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.service.campaign.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

@Component
@Primary
public class CampaignControllerDelegator extends BaseControllerDelegator<CampaignDTO, CampaignService> implements CampaignControllerAccountDelegator {

    private final CampaignControllerAccountDelegator accountDelegator;

    @Autowired
    public CampaignControllerDelegator(
            CampaignService service,
            @Qualifier("campaignAccountController") CampaignControllerAccountDelegator accountDelegator) {
        super(service, ControllerConstants.CAMPAIGN_CONTROLLER_API_BASE_URI);
        this.accountDelegator = accountDelegator;
    }

    @Override
    public String getComponentId(CampaignDTO dto) {
        return dto.getId();
    }

    @Override
    public ResponseEntity<List<CampaignDTO>> findByAccounts(Set<String> accountIds) throws CommonServiceException {
        return accountDelegator.findByAccounts(accountIds);
    }

    ResponseEntity<List<VersionDTO<CampaignDTO>>> getVersions(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String id) {
        return ResponseEntity.ok().body(getService().getVersions(id));
    }
}
