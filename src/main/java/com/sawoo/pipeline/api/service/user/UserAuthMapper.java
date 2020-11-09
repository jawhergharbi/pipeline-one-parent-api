package com.sawoo.pipeline.api.service.user;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.model.User;
import com.sawoo.pipeline.api.service.base.BaseMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class UserAuthMapper implements BaseMapper<UserAuthDTO, User> {

    private final JMapper<UserAuthDTO, User> mapperOut = new JMapper<>(UserAuthDTO.class, User.class);
    private final JMapper<User, UserAuthDTO> mapperIn = new JMapper<>(User.class, UserAuthDTO.class);
}
