package com.sawoo.pipeline.api.service.user;

import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.model.user.User;
import com.sawoo.pipeline.api.model.user.UserRole;
import com.sawoo.pipeline.api.service.base.event.BaseServiceBeforeInsertEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAuthServiceEventListener {

    private final PasswordEncoder passwordEncoder;

    @EventListener
    public void handleBeforeInsertEvent(BaseServiceBeforeInsertEvent<UserAuthDTO, User> event) {
        log.debug("UserAuth before insert listener");
        User entity = event.getModel();
        UserAuthDTO dto = event.getDto();
        if (entity != null) {
            // password
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));

            // active true by default
            entity.setActive(true);

            // roles
            if (dto.getRoles() == null || dto.getRoles().size() == 0) {
                entity.setRoles(new HashSet<>(Collections.singletonList(UserRole.USER.name())));
            }
        }

    }
}
