package com.sawoo.pipeline.api.service.account;

import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.model.User;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.service.base.BaseServiceEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountServiceEventListener implements BaseServiceEventListener<AccountDTO, Account> {

    private final AccountMapper mapper;

    @Override
    public void onBeforeInsert(AccountDTO dto, Account entity) {
        // nothing to do atm
    }

    @Override
    public void onBeforeSave(AccountDTO dto, Account entity) {
        // nothing to do atm
    }

    @Override
    public void onBeforeUpdate(AccountDTO dto, Account entity) {
        if (dto.getUsers().size() > 0) {
            Set<User> users = entity.getUsers();
            dto.getUsers().forEach((user) -> {
                if (users.stream().anyMatch(u -> u.getId().equals(user.getId()))) {
                    log.info("User [id: {}, username:{}] was already part of the account's [id: {}, fullName: {}] users",
                            user.getId(),
                            user.getEmail(),
                            entity.getId(),
                            entity.getFullName());
                } else {
                    Role userRole = Role.getDefaultRole(user.getRoles());
                    users.removeIf(u -> u.getRoles().contains(userRole.name()));
                    users.add(mapper.getUserMapperIn().getDestination(user));
                }
            });
            log.debug("Users [{}] ready to be updated for account: [id: {}, username: {}]",
                    users,
                    entity.getId(),
                    entity.getFullName());
        }
    }
}
