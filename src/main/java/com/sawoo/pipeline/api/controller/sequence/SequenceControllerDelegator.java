package com.sawoo.pipeline.api.controller.sequence;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerDelegator;
import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.service.sequence.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SequenceControllerDelegator extends BaseControllerDelegator<SequenceDTO, SequenceService> {

    @Autowired
    public SequenceControllerDelegator(SequenceService service) {
        super(service, ControllerConstants.SEQUENCE_CONTROLLER_API_BASE_URI);
    }

    @Override
    public String getComponentId(SequenceDTO dto) {
        return dto.getId();
    }
}
