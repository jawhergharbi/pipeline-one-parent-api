package com.sawoo.pipeline.api.controller.account;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerDelegator;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountControllerDelegator extends BaseControllerDelegator<AccountDTO, AccountService> {

    @Autowired
    public AccountControllerDelegator(AccountService service) {
        super(service, ControllerConstants.ACCOUNT_CONTROLLER_API_BASE_URI);
    }

    @Override
    public String getComponentId(AccountDTO dto) {
        return dto.getId();
    }
}
