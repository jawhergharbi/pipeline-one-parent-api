package com.sawoo.pipeline.api.repository.user;

import com.sawoo.pipeline.api.model.user.UserToken;
import com.sawoo.pipeline.api.model.user.UserTokenType;
import com.sawoo.pipeline.api.repository.base.BaseMongoRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;

import java.util.List;
import java.util.Optional;

@JaversSpringDataAuditable
public interface UserTokenRepository extends BaseMongoRepository<UserToken> {

    Optional<UserToken> findByToken(String token);

    List<UserToken> findAllByUserId(String userId);

    List<UserToken> findAllByUserIdAndType(String userId, UserTokenType type);

}
