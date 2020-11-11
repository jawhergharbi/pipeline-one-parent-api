package com.sawoo.pipeline.api.service.account;

import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.service.base.BaseServiceEventListener;
import org.springframework.stereotype.Component;

@Component
public class AccountServiceEventListener implements BaseServiceEventListener<AccountDTO, Account> {
    @Override
    public void onBeforeCreate(AccountDTO dto, Account entity) {
        // nothing to do atm
    }

    @Override
    public void onBeforeUpdate(AccountDTO dto, Account entity) {
        // nothing to do atm
    }
}
