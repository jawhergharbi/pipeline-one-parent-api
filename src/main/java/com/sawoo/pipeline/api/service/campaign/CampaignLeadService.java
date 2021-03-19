package com.sawoo.pipeline.api.service.campaign;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.campaign.CampaignLeadAddDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignLeadDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

public interface CampaignLeadService {

    CampaignLeadDTO addCampaignLead(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId,
            @Valid CampaignLeadAddDTO campaignLead)
            throws ResourceNotFoundException, CommonServiceException;

    CampaignLeadDTO removeCampaignLead(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String campaignId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String leadId);
}
