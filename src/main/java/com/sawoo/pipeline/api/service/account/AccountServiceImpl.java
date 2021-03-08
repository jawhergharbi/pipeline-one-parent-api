package com.sawoo.pipeline.api.service.account;


import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
import com.sawoo.pipeline.api.service.user.UserAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
            AccountLeadService leadService,
            UserAuthService userService) {
        super(repository, mapper, DBConstants.ACCOUNT_DOCUMENT, eventListener);
        this.userService = new AccountUserServiceDecorator(userService, this);
        this.leadService = leadService;
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
    public AccountDTO create(@Valid AccountDTO dto) throws CommonServiceException {
        AccountDTO account = super.create(dto);
        try {
            account = createUser(account.getId(), account.getFullName(), account.getEmail());
        } catch (CommonServiceException exc) {
            delete(account.getId());
            throw exc;
        }
        return account;
    }

    @Override
    public List<AccountDTO> findAllByUser(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String userId)
            throws ResourceNotFoundException {
        return userService.findAllByUser(userId);
    }

    @Override
    public AccountDTO updateUser(String id, String userId) throws ResourceNotFoundException {
        return userService.updateUser(id, userId);
    }

    @Override
    public AccountDTO createUser(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String id,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String fullName,
            @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
            @Email(message = ExceptionMessageConstants.COMMON_FIELD_MUST_BE_AN_EMAIL_ERROR) String email)
            throws ResourceNotFoundException {
        return userService.createUser(id, fullName, email);
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
    public List<LeadDTO> findAllLeads(String[] accountIds, Integer[] leadStatus)
            throws ResourceNotFoundException {
        return leadService.findAllLeads(accountIds, leadStatus);
    }

    @Override
    public LeadDTO removeLead(String accountId, String leadId) throws ResourceNotFoundException {
        return leadService.removeLead(accountId, leadId);
    }

    @Override
    public AccountDTO deleteAccountNotes(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String accountId)
            throws ResourceNotFoundException {
        log.debug("Delete account notes for account id [{}]", accountId);
        Account account = findAccountById(accountId);
        account.setNotes(null);
        account.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
        getRepository().save(account);
        log.debug("Account with id [{}] has been correctly updated", accountId);
        return getMapper().getMapperOut().getDestination(account);
    }

    private Account findAccountById(String accountId) throws ResourceNotFoundException {
        return getRepository()
                .findById(accountId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.ACCOUNT_DOCUMENT, accountId }));
    }
}
