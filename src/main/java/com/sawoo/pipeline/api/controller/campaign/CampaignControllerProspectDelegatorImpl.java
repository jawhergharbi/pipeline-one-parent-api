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
import com.sawoo.pipeline.api.model.prospect.ProspectQualification;
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
@Qualifier("campaignProspectController")
@RequiredArgsConstructor
public class CampaignControllerProspectDelegatorImpl implements CampaignControllerProspectDelegator {

    private final CampaignService service;

    @Override
    public ResponseEntity<CampaignProspectDTO> createProspect(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId,
            @Valid CampaignProspectCreateDTO campaignProspect) throws ResourceNotFoundException, CommonServiceException {
        if (campaignProspect.getProspect().getQualification() == null) {
            campaignProspect.getProspect().setQualification(Status
                    .builder()
                    .value(ProspectQualification.TARGETABLE.getValue())
                    .build());
        }
        CampaignProspectDTO newEntity = service.createProspect(campaignId, campaignProspect);
        return newOrAddProspect(campaignId, newEntity);
    }

    @Override
    public ResponseEntity<CampaignProspectDTO> addProspect(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId,
            @Valid CampaignProspectAddDTO campaignProspect) throws ResourceNotFoundException, CommonServiceException {
        CampaignProspectDTO newEntity = service.addProspect(campaignId, campaignProspect);
        return newOrAddProspect(campaignId, newEntity);
    }

    @Override
    public ResponseEntity<CampaignProspectDTO> removeProspect(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String prospectId)
            throws ResourceNotFoundException, CommonServiceException {
        // TODO add TODOIds
        return ResponseEntity.ok().body(service.removeProspect(campaignId, prospectId, null));
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

    private ResponseEntity<CampaignProspectDTO> newOrAddProspect(String campaignId, CampaignProspectDTO campaignProspect) {
        try {
            return ResponseEntity
                    .created(new URI(ControllerConstants.CAMPAIGN_CONTROLLER_API_BASE_URI +
                            "/" +
                            campaignId +
                            "/" +
                            ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME +
                            "/" +
                            campaignProspect.getProspect().getId()))
                    .body(campaignProspect);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
