package com.sawoo.pipeline.api.controller.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerDelegator;
import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.service.lead.LeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Component
@Primary
public class LeadControllerDelegator extends BaseControllerDelegator<LeadDTO, LeadService> implements LeadControllerReportDelegator, LeadControllerInteractionDelegator {

    private final LeadControllerReportDelegator reportDelegator;
    private final LeadControllerInteractionDelegator leadInteractionDelegator;

    @Autowired
    public LeadControllerDelegator(
            LeadService service,
            @Qualifier("leadControllerReport") LeadControllerReportDelegator reportDelegator,
            @Qualifier("leadControllerInteraction") LeadControllerInteractionDelegator leadInteractionDelegator) {
        super(service, ControllerConstants.LEAD_CONTROLLER_API_BASE_URI);
        this.reportDelegator = reportDelegator;
        this.leadInteractionDelegator = leadInteractionDelegator;
    }

    @Override
    public String getComponentId(LeadDTO dto) {
        return dto.getId();
    }

    @Override
    public ResponseEntity<InputStreamResource> getReport(String id, String template, String lan) {
        return reportDelegator.getReport(id, template, lan);
    }

    @Override
    public ResponseEntity<InteractionDTO> addInteraction(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String id,
            @Valid InteractionDTO interaction)
            throws ResourceNotFoundException, CommonServiceException {
        return leadInteractionDelegator.addInteraction(id, interaction);
    }
}
