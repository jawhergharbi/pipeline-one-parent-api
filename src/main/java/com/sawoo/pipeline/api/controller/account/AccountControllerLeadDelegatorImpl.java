package com.sawoo.pipeline.api.controller.account;

import com.sawoo.pipeline.api.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("accountControllerLead")
public class AccountControllerLeadDelegatorImpl implements AccountControllerLeadDelegator {

    private final AccountService service;

    @Autowired
    public AccountControllerLeadDelegatorImpl(AccountService service) {
        this.service = service;
    }
}
