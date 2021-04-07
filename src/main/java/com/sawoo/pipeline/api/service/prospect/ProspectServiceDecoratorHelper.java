package com.sawoo.pipeline.api.service.prospect;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.UserCommon;
import com.sawoo.pipeline.api.dto.UserCommonType;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.user.User;
import com.sawoo.pipeline.api.model.user.UserRole;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProspectServiceDecoratorHelper {

    private final AccountRepository accountRepository;

    public List<UserCommon> getUsers(String prospectId) {
        log.debug("Retrieving users for prospectId [{}]", prospectId);
        Account account = findAccountByProspectId(prospectId);
        Set<User> users = account.getUsers();

        log.debug("Account id [{}] is associated to prospectId [{}] and there are [{}] users linked to this account",
                account.getId(),
                prospectId,
                users.size());

        return users
                .stream()
                .map(this::mapUser)
                .collect(Collectors.toList());
    }

    public UserCommon getAssignee(String prospectId, String assigneeId) {
        log.debug("Retrieving assignee user for Prospect id [{}]. Assignee id: [{}]", prospectId, assigneeId);
        Account account = findAccountByProspectId(prospectId);
        Set<User> users = account.getUsers();
        if (assigneeId != null) {
            return users.stream()
                    .filter(u -> u.getId().equals(assigneeId))
                    .findAny()
                    .map(this::mapUser)
                    .orElseThrow(() -> new CommonServiceException(
                            ExceptionMessageConstants.PROSPECT_PROSPECT_ACCOUNT_ASSIGNEE_NOT_FOUND_EXCEPTION,
                            new String[] {account.getId(), assigneeId}));
        } else {
            User user = getUserByRole(users, UserRole.AST);
            if (user == null) {
                user = getUserByRole(users, UserRole.CLIENT);
            }
            if (user != null) {
                return mapUser(user);
            } else {
                throw new CommonServiceException(
                        ExceptionMessageConstants.PROSPECT_PROSPECT_ACCOUNT_NO_ASSIGNEE_FOUND_EXCEPTION,
                        new String[] {account.getId()}
                );
            }
        }
    }

    private User getUserByRole(Set<User> users, UserRole role) {
        return users.stream()
                .filter(u -> u.getRoles().contains(role.name()))
                .findFirst()
                .orElse(null);
    }

    private UserCommon mapUser(User user) {
        return UserCommon.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .type(UserCommonType.USER)
                .build();
    }

    private Account findAccountByProspectId(String prospectId) {
        return accountRepository
                .findByProspectId(prospectId)
                .orElseThrow(() -> new CommonServiceException(
                        ExceptionMessageConstants.PROSPECT_PROSPECT_ACCOUNT_NOT_FOUND_EXCEPTION,
                        new String[] {prospectId}));
    }
}
