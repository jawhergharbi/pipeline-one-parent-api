package com.sawoo.pipeline.api.service.user;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.dto.user.UserTokenDTO;
import com.sawoo.pipeline.api.model.user.UserToken;
import com.sawoo.pipeline.api.service.base.BaseMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class UserTokenMapper implements BaseMapper<UserTokenDTO, UserToken> {

    private final JMapper<UserTokenDTO, UserToken> mapperOut = new JMapper<>(UserTokenDTO.class, UserToken.class);
    private final JMapper<UserToken, UserTokenDTO> mapperIn = new JMapper<>(UserToken.class, UserTokenDTO.class);
}
