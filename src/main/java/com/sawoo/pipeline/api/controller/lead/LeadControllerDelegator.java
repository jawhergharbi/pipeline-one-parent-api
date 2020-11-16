package com.sawoo.pipeline.api.controller.lead;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerDelegator;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.service.company.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LeadControllerDelegator extends BaseControllerDelegator<CompanyDTO, CompanyService> {

    @Autowired
    public LeadControllerDelegator(CompanyService service) {
        super(service, ControllerConstants.COMPANY_CONTROLLER_API_BASE_URI);
    }

    @Override
    public String getComponentId(CompanyDTO dto) {
        return dto.getId();
    }
}
