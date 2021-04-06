package com.sawoo.pipeline.api.controller.campaign;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerDelegator;
import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignProspectDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignProspectAddDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignProspectBaseDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignProspectCreateDTO;
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
public class CampaignControllerDelegator extends BaseControllerDelegator<CampaignDTO, CampaignService> implements CampaignControllerAccountDelegator, CampaignControllerProspectDelegator {

    private final CampaignControllerAccountDelegator accountDelegator;
    private final CampaignControllerProspectDelegator prospectDelegator;

    @Autowired
    public CampaignControllerDelegator(
            CampaignService service,
            @Qualifier("campaignAccountController") CampaignControllerAccountDelegator accountDelegator,
            @Qualifier("campaignProspectController") CampaignControllerProspectDelegator prospectDelegator) {
        super(service, ControllerConstants.CAMPAIGN_CONTROLLER_API_BASE_URI);
        this.accountDelegator = accountDelegator;
        this.prospectDelegator = prospectDelegator;
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
    public ResponseEntity<CampaignProspectDTO> createProspect(String campaignId, CampaignProspectCreateDTO campaignProspect)
            throws ResourceNotFoundException, CommonServiceException {
        return prospectDelegator.createProspect(campaignId, campaignProspect);
    }

    @Override
    public ResponseEntity<CampaignProspectDTO> addProspect(String campaignId, CampaignProspectAddDTO campaignProspect)
            throws ResourceNotFoundException, CommonServiceException {
        return prospectDelegator.addProspect(campaignId, campaignProspect);
    }

    @Override
    public ResponseEntity<CampaignProspectDTO> removeProspect(String campaignId, String prospectId)
            throws ResourceNotFoundException, CommonServiceException {
        return prospectDelegator.removeProspect(campaignId, prospectId);
    }

    @Override
    public ResponseEntity<CampaignProspectDTO> updateProspect(String campaignId, String prospectId, CampaignProspectBaseDTO campaignProspect)
            throws ResourceNotFoundException, CommonServiceException {
        return prospectDelegator.updateProspect(campaignId, prospectId, campaignProspect);
    }

    @Override
    public ResponseEntity<List<CampaignProspectDTO>> findAllProspects(String campaignId)
            throws ResourceNotFoundException, CommonServiceException {
        return prospectDelegator.findAllProspects(campaignId);
    }
}
