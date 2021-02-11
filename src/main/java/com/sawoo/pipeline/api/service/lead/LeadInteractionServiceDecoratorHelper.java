package com.sawoo.pipeline.api.service.lead;

import com.sawoo.pipeline.api.dto.UserCommon;
import com.sawoo.pipeline.api.dto.UserCommonType;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.user.User;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class LeadInteractionServiceDecoratorHelper {

    private final AccountRepository accountRepository;

    public List<UserCommon> getUsers(String leadId) {
        log.debug("Retrieving users for leadId [{}]", leadId);
        Optional<Account> account = accountRepository.findByLeadId(leadId);
        return account.map(a -> {
            Set<User> users = a.getUsers();

            log.debug("Account id [{}] is associated to leadId [{}] and there are [{}] users linked to this account",
                    a.getId(),
                    leadId,
                    users.size());

            return users
                    .stream()
                    .map(u ->
                            UserCommon.builder()
                                    .id(u.getId())
                                    .fullName(u.getFullName())
                                    .type(UserCommonType.USER)
                                    .build())
                    .collect(Collectors.toList());
        }).orElse(Collections.emptyList());
    }
}
