package com.sawoo.pipeline.api.controller.interaction;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerDelegator;
import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.service.interaction.InteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InteractionControllerDelegator extends BaseControllerDelegator<InteractionDTO, InteractionService> {

    @Autowired
    public InteractionControllerDelegator(InteractionService service) {
        super(service, ControllerConstants.INTERACTION_CONTROLLER_API_BASE_URI);
    }

    @Override
    public String getComponentId(InteractionDTO dto) {
        return dto.getId();
    }
}
