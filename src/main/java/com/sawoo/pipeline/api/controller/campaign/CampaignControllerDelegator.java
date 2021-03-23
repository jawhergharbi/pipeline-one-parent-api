package com.sawoo.pipeline.api.controller.campaign;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerDelegator;
import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignLeadDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignLeadAddDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignLeadBaseDTO;
import com.sawoo.pipeline.api.service.campaign.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@Primary
public class CampaignControllerDelegator extends BaseControllerDelegator<CampaignDTO, CampaignService> implements CampaignControllerAccountDelegator, CampaignControllerLeadDelegator {

    private final CampaignControllerAccountDelegator accountDelegator;
    private final CampaignControllerLeadDelegator leadDelegator;

    @Autowired
    public CampaignControllerDelegator(
            CampaignService service,
            @Qualifier("campaignAccountController") CampaignControllerAccountDelegator accountDelegator,
            @Qualifier("campaignLeadController") CampaignControllerLeadDelegator leadDelegator) {
        super(service, ControllerConstants.CAMPAIGN_CONTROLLER_API_BASE_URI);
        this.accountDelegator = accountDelegator;
        this.leadDelegator = leadDelegator;
    }

    @Override
    public String getComponentId(CampaignDTO dto) {
        return dto.getId();
    }

    @Override
    public ResponseEntity<List<CampaignDTO>> findByAccounts(Set<String> accountIds) throws CommonServiceException {
        return accountDelegator.findByAccounts(accountIds);
    }

    @Override
    public ResponseEntity<CampaignLeadDTO> addLead(String campaignId, CampaignLeadAddDTO campaignLead)
            throws ResourceNotFoundException, CommonServiceException {
        return leadDelegator.addLead(campaignId, campaignLead);
    }

    @Override
    public ResponseEntity<CampaignLeadDTO> removeLead(String campaignId, String leadId)
            throws ResourceNotFoundException, CommonServiceException {
        return leadDelegator.removeLead(campaignId, leadId);
    }

    @Override
    public ResponseEntity<CampaignLeadDTO> updateLead(String campaignId, String leadId, CampaignLeadBaseDTO campaignLead)
            throws ResourceNotFoundException, CommonServiceException {
        return leadDelegator.updateLead(campaignId, leadId, campaignLead);
    }

    @Override
    public ResponseEntity<List<CampaignLeadDTO>> findAllLeads(String campaignId)
            throws ResourceNotFoundException, CommonServiceException {
        return leadDelegator.findAllLeads(campaignId);
    }
}
