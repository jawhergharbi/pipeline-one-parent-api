package com.sawoo.pipeline.api.service.user;

import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.model.UserMongoDB;
import com.sawoo.pipeline.api.service.base.BaseServiceEventListener;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class UserAuthServiceEventListener implements BaseServiceEventListener<UserAuthDTO, UserMongoDB> {

    private final PasswordEncoder passwordEncoder;

    @Override
    public void onBeforeCreate(UserAuthDTO dto, UserMongoDB entity) {
        if (entity != null) {
            // password
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));

            // active true by default
            entity.setActive(true);

            // roles
            if (dto.getRoles() == null || dto.getRoles().size() == 0) {
                entity.setRoles(new HashSet<>(Collections.singletonList(Role.USER.name())));
            }
        }
    }

    @Override
    public void onBeforeUpdate(UserAuthDTO dto, UserMongoDB entity) {
        // nothing
    }
}
