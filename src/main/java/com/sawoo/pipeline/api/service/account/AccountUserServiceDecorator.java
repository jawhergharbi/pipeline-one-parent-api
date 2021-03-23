package com.sawoo.pipeline.api.service.account;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.user.UserRole;
import com.sawoo.pipeline.api.service.user.UserAuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AccountUserServiceDecorator implements AccountUserService {

    private final UserAuthService userService;
    private final AccountService service;

    public AccountUserServiceDecorator(UserAuthService userService, AccountService service) {
        this.userService = userService;
        this.service = service;
    }

    @Override
    public List<AccountDTO> findAllByUser(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String userId)
            throws ResourceNotFoundException {
        log.debug("Retrieve accounts for user id [{}]", userId);

        return userService
                .getRepository()
                .findById(userId)
                .map(user -> {
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
        log.debug("Updating user id [{}] within account id [{}]", userId, id);
        return userService
                .getRepository()
                .findById(userId)
                .map(user -> {
                    AccountDTO accountToBeUpdated = new AccountDTO();
                    accountToBeUpdated.getUsers().add(service.getMapper().getUserMapperOut().getDestination(user));
                    AccountDTO updatedAccount = service.update(id, accountToBeUpdated);
                    log.debug("Account id [{}] updated with user id: [{}]", id, userId);
                    return updatedAccount;
                }).orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        new String[]{ DBConstants.USER_DOCUMENT, userId })
                );
    }

    @Override
    public AccountDTO createUser(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String id,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String fullName,
            @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
            @Email(message = ExceptionMessageConstants.COMMON_FIELD_MUST_BE_AN_EMAIL_ERROR) String email)
            throws CommonServiceException {
        String password = RandomStringUtils.randomAlphanumeric(8, 12);
        UserAuthDTO user = UserAuthDTO
                .builder()
                .fullName(fullName)
                .email(email)
                .password(password)
                .confirmPassword(password)
                .roles(new HashSet<>( Arrays.asList( UserRole.USER.name(), UserRole.CLIENT.name() ) ))
                .build();
        // TODO we may check if the user is already created but not active
        user = userService.create(user);
        return updateUser(id, user.getId());
    }
}
