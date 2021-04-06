package com.sawoo.pipeline.api.controller.campaign;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.campaign.CampaignLeadDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignLeadAddDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignLeadBaseDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignLeadCreateDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Validated
public interface CampaignControllerLeadDelegator {

    ResponseEntity<CampaignLeadDTO> createLead(@NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId,
                                               @Valid CampaignLeadCreateDTO campaignLead)
            throws ResourceNotFoundException, CommonServiceException;

    ResponseEntity<CampaignLeadDTO> addLead(@NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId,
                                            @Valid CampaignLeadAddDTO campaignLead)
            throws ResourceNotFoundException, CommonServiceException;

    ResponseEntity<CampaignLeadDTO> removeLead(@NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId,
                                               @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String leadId)
            throws ResourceNotFoundException, CommonServiceException;

    ResponseEntity<CampaignLeadDTO> updateLead(@NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId,
                                               @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String leadId,
                                               @Valid CampaignLeadBaseDTO campaignLead)
            throws ResourceNotFoundException, CommonServiceException;

    ResponseEntity<List<CampaignLeadDTO>> findAllLeads(@NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId)
            throws ResourceNotFoundException, CommonServiceException;
}