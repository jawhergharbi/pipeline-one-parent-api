package com.sawoo.pipeline.api.controller.campaign;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.campaign.CampaignLeadAddDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignLeadDTO;
import com.sawoo.pipeline.api.service.campaign.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.net.URI;
import java.net.URISyntaxException;

@Component
@Qualifier("campaignLeadController")
@RequiredArgsConstructor
public class CampaignControllerLeadDelegatorImpl implements CampaignControllerLeadDelegator {

    private final CampaignService service;

    @Override
    public ResponseEntity<CampaignLeadDTO> addCampaignLead(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String campaignId,
            @Valid CampaignLeadAddDTO campaignLead) throws ResourceNotFoundException, CommonServiceException {
        CampaignLeadDTO newEntity = service.addCampaignLead(campaignId, campaignLead);
        try {
            return ResponseEntity
                    .created(new URI(ControllerConstants.CAMPAIGN_CONTROLLER_API_BASE_URI + "/" + campaignId + "/leads/" + newEntity.getLead().getId()))
                    .body(newEntity);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<CampaignLeadDTO> removeCampaignLead(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String campaignId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId)
            throws ResourceNotFoundException, CommonServiceException {
        return ResponseEntity.ok().body(service.removeCampaignLead(campaignId, leadId));
    }
}
