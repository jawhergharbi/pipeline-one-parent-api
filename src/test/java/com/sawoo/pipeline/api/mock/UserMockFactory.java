package com.sawoo.pipeline.api.mock;

import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthDetails;
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
        String USER_EMAIL = getFAKER().internet().emailAddress();
        String USER_PASSWORD = getFAKER().internet().password(6, 12);
        return newEntity(id, USER_EMAIL, USER_PASSWORD, null);
    }

    public User newEntity(String email, String password) {
        return newEntity(null, email, password, null);
    }

    public User newEntity(String id, String[] roles) {
        return newEntity(
                id,
                getFAKER().internet().emailAddress(),
                getFAKER().internet().password(6, 12),
                roles);
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
        return newDTO(
                id,
                getFAKER().internet().emailAddress(),
                getFAKER().internet().password(6, 12),
                null);
    }

    @Override
    public UserAuthDTO newDTO(String id, UserAuthDTO dto) {
        return dto.toBuilder().id(id).build();
    }

    public UserAuthDTO newDTO(String id, String email, String password, String[] roles) {
        return newDTO(id, email, password, password, roles);
    }

    public UserAuthDTO newDTO(String id, String email, String password, String confirmPassword, String[] roles) {
        return newDTO(id, email, password, confirmPassword, getFAKER().name().fullName(), roles);
    }

    public UserAuthDTO newDTO(String id, String email, String password, String confirmPassword, String fullName, String[] roles) {
        UserAuthDTO entity = new UserAuthDTO();
        LocalDateTime now = LocalDateTime.now();
        entity.setId(id);
        entity.setFullName(fullName);
        entity.setPassword(password);
        entity.setConfirmPassword(confirmPassword);
        entity.setEmail(email);
        entity.setActive(true);
        if (roles != null) {
            entity.setRoles(new HashSet<>(Arrays.asList(roles)));
        } else {
            entity.setRoles(new HashSet<>(Collections.singletonList(Role.USER.name())));
        }
        entity.setCreated(now);
        entity.setUpdated(now);
        return entity;
    }

    public UserAuthDetails newUserAuthDetails(String email, String password, String id, String role) {
        UserAuthDetails mockUserAuth = new UserAuthDetails();
        LocalDateTime now = LocalDateTime.now();
        mockUserAuth.setId(id);
        mockUserAuth.setEmail(email);
        mockUserAuth.setPassword(password);
        mockUserAuth.setActive(true);
        mockUserAuth.setRoles(new HashSet<>(Collections.singletonList(role)));
        mockUserAuth.setCreated(now);
        mockUserAuth.setUpdated(now);
        return mockUserAuth;
    }
}
