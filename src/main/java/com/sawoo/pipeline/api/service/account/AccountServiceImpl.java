package com.sawoo.pipeline.api.service.account;


import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.model.DataStoreConstants;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Slf4j
@Service
@Validated
public class AccountServiceImpl extends BaseServiceImpl<AccountDTO, Account, AccountRepository> implements AccountService {

    @Autowired
    public AccountServiceImpl(AccountRepository repository, AccountMapper mapper, AccountServiceEventListener eventListener) {
        super(repository, mapper, DataStoreConstants.ACCOUNT_DOCUMENT, eventListener);
    }

    @Override
    public Optional<Account> entityExists(AccountDTO entityToCreate) {
        log.debug(
                "Checking entity existence. [type: {}, linkedIn: {}]",
                DataStoreConstants.ACCOUNT_DOCUMENT,
                entityToCreate.getLinkedInUrl());
        return getRepository().findByLinkedInUrl(entityToCreate.getLinkedInUrl());
    }
}
