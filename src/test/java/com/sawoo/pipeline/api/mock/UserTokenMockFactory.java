package com.sawoo.pipeline.api.mock;

import com.sawoo.pipeline.api.dto.user.UserAuthTokenDTO;
import com.sawoo.pipeline.api.model.user.UserToken;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class UserTokenMockFactory extends BaseMockFactory<UserAuthTokenDTO, UserToken> {

    @Override
    public String getComponentId() {
        return getFAKER().internet().uuid();
    }

    @Override
    public UserToken newEntity(String id) {
        return UserToken.builder()
                .id(id)
                .userId(getFAKER().internet().uuid())
                .token(UUID.randomUUID().toString())
                .expirationDate(LocalDateTime.now().plusHours(3))
                .build();
    }

    @Override
    public UserAuthTokenDTO newDTO(String id) {
        return UserAuthTokenDTO.builder()
                .id(id)
                .userId(getFAKER().internet().uuid())
                .token(UUID.randomUUID().toString())
                .expirationDate(LocalDateTime.now().plusHours(3))
                .build();
    }

    @Override
    public UserAuthTokenDTO newDTO(String id, UserAuthTokenDTO dto) {
        return UserAuthTokenDTO.builder()
                .id(id)
                .token(dto.getToken())
                .userId(dto.getUserId())
                .expirationDate(dto.getExpirationDate())
                .build();
    }
}
