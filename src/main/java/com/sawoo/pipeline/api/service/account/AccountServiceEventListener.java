package com.sawoo.pipeline.api.service.account;

import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.user.User;
import com.sawoo.pipeline.api.model.user.UserRole;
import com.sawoo.pipeline.api.service.base.event.BaseServiceBeforeUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountServiceEventListener {

    private final AccountMapper mapper;

    @EventListener
    public void handleBeforeUpdateEvent(BaseServiceBeforeUpdateEvent<AccountDTO, Account> event) {
        log.debug("Account before update listener");
        AccountDTO dto = event.getDto();
        Account entity = event.getModel();
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
                    UserRole userRole = UserRole.getUserManagementRole(user.getRoles());
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
