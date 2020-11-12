package com.sawoo.pipeline.api.controller.prospect;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerDelegator;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.service.prospect.ProspectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProspectControllerDelegator extends BaseControllerDelegator<ProspectDTO, ProspectService> {

    @Autowired
    public ProspectControllerDelegator(ProspectService service) {
        super(service, ControllerConstants.PROSPECT_CONTROLLER_API_BASE_URI);
    }

    @Override
    public String getComponentId(ProspectDTO dto) {
        return dto.getId();
    }
}
