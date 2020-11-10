package com.sawoo.pipeline.api.mock;

import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@Component
public class UserMockFactory extends BaseMockFactory<UserAuthDTO, User> {

    @Override
    public String getComponentId() {
        return getFAKER().internet().uuid();
    }

    @Override
    public User newEntity(String id) {
        return null;
    }

    public User newEntity(String email, String password) {
        return newEntity(null, email, password, null);
    }

    public User newEntity(String id, String email, String password, String[] roles) {
        User entity = new User();
        LocalDateTime SIGNED_UP_DATE_TIME = LocalDateTime.of(2020, Month.DECEMBER, 12, 12, 0);
        entity.setId(id);
        entity.setEmail(email);
        entity.setCreated(SIGNED_UP_DATE_TIME);
        entity.setPassword(password);
        entity.setActive(true);
        if (roles != null) {
            entity.setRoles(new HashSet<>(Arrays.asList(roles)));
        } else {
            entity.setRoles(new HashSet<>(Collections.singletonList(Role.USER.name())));
        }
        entity.setUpdated(LocalDateTime.now(ZoneOffset.UTC));

        return entity;
    }

    @Override
    public UserAuthDTO newDTO(String id) {
        return null;
    }
}
