package com.sawoo.pipeline.api.service.account;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.user.UserRole;
import com.sawoo.pipeline.api.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AccountUserServiceDecorator implements AccountUserService {

    private final UserRepository userRepository;
    private final AccountService service;

    public AccountUserServiceDecorator(UserRepository userRepository, AccountService service) {
        this.userRepository = userRepository;
        this.service = service;
    }

    @Override
    public List<AccountDTO> findAllByUser(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String userId)
            throws ResourceNotFoundException {
        log.debug("Retrieve accounts for user id [{}]", userId);

        return userRepository
                .findById(userId)
                .map((user) -> {
                    List<Account> accounts;
                    if (user.getRoles().contains(UserRole.ADMIN.name())) {
                        accounts = service.getRepository().findAll();
                    } else {
                        accounts = service.getRepository().findByUserId(userId);
                    }
                    log.debug("[{}] account/s has/have been found for user id: [{}]", accounts.size(), userId);
                    return accounts
                            .stream()
                            .map(service.getMapper().getMapperOut()::getDestination)
                            .collect(Collectors.toList());
                }).orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        new String[]{ DBConstants.USER_DOCUMENT, userId })
                );
    }

    @Override
    public AccountDTO updateUser(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String id,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String userId)
            throws ResourceNotFoundException {
        return userRepository
                .findById(userId)
                .map((user) -> {
                    AccountDTO accountToBeUpdated = new AccountDTO();
                    accountToBeUpdated.getUsers().add(service.getMapper().getUserMapperOut().getDestination(user));
                    return service.update(id, accountToBeUpdated);
                }).orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        new String[]{ DBConstants.USER_DOCUMENT, userId })
                );
    }
}
