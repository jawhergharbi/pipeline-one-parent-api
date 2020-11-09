package com.sawoo.pipeline.api.service.user;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.model.UserMongoDB;
import com.sawoo.pipeline.api.service.base.BaseMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class UserAuthMapper implements BaseMapper<UserAuthDTO, UserMongoDB> {

    private final JMapper<UserAuthDTO, UserMongoDB> mapperOut = new JMapper<>(UserAuthDTO.class, UserMongoDB.class);
    private final JMapper<UserMongoDB, UserAuthDTO> mapperIn = new JMapper<>(UserMongoDB.class, UserAuthDTO.class);
}
