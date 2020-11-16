package com.sawoo.pipeline.api.service.account;


import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.repository.user.UserRepository;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class AccountServiceImpl extends BaseServiceImpl<AccountDTO, Account, AccountRepository, AccountMapper> implements AccountService {

    private final UserRepository userRepository;

    @Autowired
    public AccountServiceImpl(
            AccountRepository repository,
            AccountMapper mapper,
            AccountServiceEventListener eventListener,
            UserRepository userRepository) {
        super(repository, mapper, DBConstants.ACCOUNT_DOCUMENT, eventListener);
        this.userRepository = userRepository;
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
        log.debug("Retrieve accounts for user id [{}]", userId);

        return userRepository
                .findById(userId)
                .map((user) -> {
                    List<Account> accounts;
                    if (user.getRoles().contains(Role.ADMIN.name())) {
                        accounts = getRepository().findAll();
                    } else {
                        accounts = getRepository().findByUserId(userId);
                    }
                    log.debug("[{}] account/s has/have been found for user id: [{}]", accounts.size(), userId);
                    return accounts
                            .stream()
                            .map(getMapper().getMapperOut()::getDestination)
                            .collect(Collectors.toList());
                }).orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        new String[]{ DBConstants.USER_DOCUMENT, userId })
                );
    }

    @Override
    public AccountDTO updateUser(String id, String userId) throws ResourceNotFoundException {
        return userRepository
                .findById(userId)
                .map((user) -> {
                    AccountDTO accountToBeUpdated = new AccountDTO();
                    accountToBeUpdated.getUsers().add(getMapper().getUserMapperOut().getDestination(user));
                    return update(id, accountToBeUpdated);
                }).orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        new String[]{ DBConstants.USER_DOCUMENT, userId })
                );
    }
}
