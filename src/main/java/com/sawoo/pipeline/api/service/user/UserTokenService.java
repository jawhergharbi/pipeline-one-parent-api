package com.sawoo.pipeline.api.service.user;

import com.sawoo.pipeline.api.dto.user.UserTokenDTO;
import com.sawoo.pipeline.api.model.user.UserTokenType;
import com.sawoo.pipeline.api.service.base.BaseService;

import java.util.List;
import java.util.Optional;

public interface UserTokenService extends BaseService<UserTokenDTO>  {

    Optional<UserTokenDTO> findByToken(String token);

    List<UserTokenDTO> findAllByUserId(String userId);

    List<UserTokenDTO> findAllByUserIdAndType(String userId, UserTokenType type);
}
