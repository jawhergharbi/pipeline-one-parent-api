package com.sawoo.pipeline.api.service.prospect;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.repository.prospect.ProspectRepository;
import com.sawoo.pipeline.api.service.base.BaseProxyService;
import com.sawoo.pipeline.api.service.base.BaseService;

import javax.validation.constraints.NotBlank;

public interface ProspectService extends BaseService<ProspectDTO>, BaseProxyService<ProspectRepository, ProspectMapper>,
        ProspectReportService, ProspectTodoService, ProspectSequenceTodoService {

    ProspectDTO deleteProspectSummary(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String prospectId)
            throws ResourceNotFoundException;

    ProspectDTO deleteProspectQualificationComments(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String prospectId)
            throws ResourceNotFoundException;

    ProspectDTO deleteProspectCompanyComments(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String prospectId)
            throws ResourceNotFoundException;

}
