package com.sawoo.pipeline.api.controller.campaign;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.campaign.CampaignLeadDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignLeadAddDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignLeadBaseDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignLeadCreateDTO;
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
import java.util.List;

@Component
@Qualifier("campaignLeadController")
@RequiredArgsConstructor
public class CampaignControllerLeadDelegatorImpl implements CampaignControllerLeadDelegator {

    private final CampaignService service;

    @Override
    public ResponseEntity<CampaignLeadDTO> createLead(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId,
            @Valid CampaignLeadCreateDTO campaignLead) throws ResourceNotFoundException, CommonServiceException {
        CampaignLeadDTO newEntity = service.createLead(campaignId, campaignLead);
        return newOrAddLead(campaignId, newEntity);
    }

    @Override
    public ResponseEntity<CampaignLeadDTO> addLead(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId,
            @Valid CampaignLeadAddDTO campaignLead) throws ResourceNotFoundException, CommonServiceException {
        CampaignLeadDTO newEntity = service.addLead(campaignId, campaignLead);
        return newOrAddLead(campaignId, newEntity);
    }

    @Override
    public ResponseEntity<CampaignLeadDTO> removeLead(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String leadId)
            throws ResourceNotFoundException, CommonServiceException {
        return ResponseEntity.ok().body(service.removeLead(campaignId, leadId));
    }

    @Override
    public ResponseEntity<CampaignLeadDTO> updateLead(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String leadId,
            @Valid CampaignLeadBaseDTO campaignLead) throws ResourceNotFoundException, CommonServiceException {
        return ResponseEntity.ok().body(service.updateLead(campaignId, leadId, campaignLead));
    }

    @Override
    public ResponseEntity<List<CampaignLeadDTO>> findAllLeads(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId)
            throws ResourceNotFoundException, CommonServiceException {
        return ResponseEntity.ok().body(service.findAllLeads(campaignId));
    }

    private ResponseEntity<CampaignLeadDTO> newOrAddLead(String campaignId, CampaignLeadDTO campaignLead) {
        try {
            return ResponseEntity
                    .created(new URI(
                            ControllerConstants.CAMPAIGN_CONTROLLER_API_BASE_URI +
                                    "/" +
                                    campaignId +
                                    "/leads/" +
                                    campaignLead.getLead().getId()))
                    .body(campaignLead);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
