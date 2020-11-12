package com.sawoo.pipeline.api.controller.user;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerDelegator;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.service.user.UserAuthJwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserControllerDelegator extends BaseControllerDelegator<UserAuthDTO, UserAuthJwtService> {

    @Autowired
    public UserControllerDelegator(UserAuthJwtService service) {
        super(service, ControllerConstants.USER_CONTROLLER_API_BASE_URI);
    }

    @Override
    public String getComponentId(UserAuthDTO dto) {
        return dto.getId();
    }
}
