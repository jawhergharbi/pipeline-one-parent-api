package com.sawoo.pipeline.api.service.account;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.prospect.LeadDTOOld;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

public interface AccountLeadService {

    LeadDTO createLead(@NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String accountId,
                       @Valid LeadDTO lead)
            throws ResourceNotFoundException, CommonServiceException;

    List<LeadDTO> findAllLeads(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String accountId)
            throws ResourceNotFoundException;

    LeadDTO removeLead(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String accountId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId) throws ResourceNotFoundException;
}
