package com.sawoo.pipeline.api.controller.campaign;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.campaign.CampaignProspectDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignProspectAddDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignProspectBaseDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignProspectCreateDTO;
import com.sawoo.pipeline.api.model.common.Status;
import com.sawoo.pipeline.api.model.prospect.ProspectStatusList;
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
import java.time.LocalDateTime;
import java.util.List;

@Component
@Qualifier("campaignProspectController")
@RequiredArgsConstructor
public class CampaignControllerProspectDelegatorImpl implements CampaignControllerProspectDelegator {

    private final CampaignService service;

    @Override
    public ResponseEntity<CampaignProspectDTO> createProspect(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId,
            @Valid CampaignProspectCreateDTO campaignProspect) throws ResourceNotFoundException, CommonServiceException {
        if (campaignProspect.getProspect().getStatus() == null) {
            campaignProspect.getProspect().setStatus(Status
                    .builder()
                    .value(ProspectStatusList.TARGETABLE.getStatus())
                    .updated(LocalDateTime.now())
                    .build());
        }
        CampaignProspectDTO newEntity = service.createProspect(campaignId, campaignProspect);
        return newOrAddLead(campaignId, newEntity);
    }

    @Override
    public ResponseEntity<CampaignProspectDTO> addProspect(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId,
            @Valid CampaignProspectAddDTO campaignProspect) throws ResourceNotFoundException, CommonServiceException {
        CampaignProspectDTO newEntity = service.addProspect(campaignId, campaignProspect);
        return newOrAddLead(campaignId, newEntity);
    }

    @Override
    public ResponseEntity<CampaignProspectDTO> removeProspect(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String prospectId)
            throws ResourceNotFoundException, CommonServiceException {
        return ResponseEntity.ok().body(service.removeProspect(campaignId, prospectId));
    }

    @Override
    public ResponseEntity<CampaignProspectDTO> updateProspect(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String prospectId,
            @Valid CampaignProspectBaseDTO campaignProspect) throws ResourceNotFoundException, CommonServiceException {
        return ResponseEntity.ok().body(service.updateProspect(campaignId, prospectId, campaignProspect));
    }

    @Override
    public ResponseEntity<List<CampaignProspectDTO>> findAllProspects(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId)
            throws ResourceNotFoundException, CommonServiceException {
        return ResponseEntity.ok().body(service.findAllProspects(campaignId));
    }

    private ResponseEntity<CampaignProspectDTO> newOrAddLead(String campaignId, CampaignProspectDTO campaignLead) {
        try {
            return ResponseEntity
                    .created(new URI(
                            ControllerConstants.CAMPAIGN_CONTROLLER_API_BASE_URI +
                                    "/" +
                                    campaignId +
                                    "/leads/" +
                                    campaignLead.getProspect().getId()))
                    .body(campaignLead);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
