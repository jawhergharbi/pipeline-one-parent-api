package com.sawoo.pipeline.api.service.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.repository.lead.LeadRepository;
import com.sawoo.pipeline.api.service.base.BaseProxyService;
import com.sawoo.pipeline.api.service.base.BaseService;

import javax.validation.constraints.NotBlank;

public interface LeadService extends BaseService<LeadDTO>, BaseProxyService<LeadRepository, LeadMapper>, LeadReportService, LeadInteractionService {

    LeadDTO deleteLeadSummary(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String leadId)
            throws ResourceNotFoundException;

    LeadDTO deleteLeadQualificationComments(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String leadId)
            throws ResourceNotFoundException;

    LeadDTO deleteLeadCompanyComments(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String leadId)
            throws ResourceNotFoundException;

}
