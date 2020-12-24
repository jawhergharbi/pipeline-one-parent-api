package com.sawoo.pipeline.api.service.account;


import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
import com.sawoo.pipeline.api.repository.user.UserRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Validated
@Primary
public class AccountServiceImpl extends BaseServiceImpl<AccountDTO, Account, AccountRepository, AccountMapper> implements AccountService {

    private final AccountLeadService leadService;
    private final AccountUserService userService;

    @Autowired
    public AccountServiceImpl(
            AccountRepository repository,
            AccountMapper mapper,
            AccountServiceEventListener eventListener,
            @Qualifier(value = "accountLeadService") AccountLeadService leadService,
            UserRepository userRepository) {
        super(repository, mapper, DBConstants.ACCOUNT_DOCUMENT, eventListener);
        this.leadService = leadService;
        this.userService = new AccountUserServiceDecorator(this, userRepository);
    }

    @Override
    public Optional<Account> entityExists(AccountDTO entityToCreate) {
        log.debug(
                "Checking entity existence. [type: {}, linkedIn: {}]",
                DBConstants.ACCOUNT_DOCUMENT,
                entityToCreate.getLinkedInUrl());
        return getRepository().findByLinkedInUrl(entityToCreate.getLinkedInUrl());
    }

    @Override
    public List<AccountDTO> findAllByUser(String userId) throws ResourceNotFoundException {
        return userService.findAllByUser(userId);
    }

    @Override
    public AccountDTO updateUser(String id, String userId) throws ResourceNotFoundException {
        return userService.updateUser(id, userId);
    }

    @Override
    public LeadDTO createLead(String accountId, LeadDTO lead) throws ResourceNotFoundException, CommonServiceException {
        return leadService.createLead(accountId, lead);
    }

    @Override
    public List<LeadDTO> findAllLeads(String accountId) throws ResourceNotFoundException {
        return leadService.findAllLeads(accountId);
    }

    @Override
    public LeadDTO removeLead(String accountId, String leadId) throws ResourceNotFoundException {
        return leadService.removeLead(accountId, leadId);
    }
}
