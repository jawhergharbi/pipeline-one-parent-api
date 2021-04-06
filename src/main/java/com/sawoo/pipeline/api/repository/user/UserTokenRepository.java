package com.sawoo.pipeline.api.repository.user;

import com.sawoo.pipeline.api.model.user.UserToken;
import com.sawoo.pipeline.api.model.user.UserTokenType;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

@JaversSpringDataAuditable
public interface UserTokenRepository extends MongoRepository<UserToken, String> {

    Optional<UserToken> findByToken(String token);

    List<UserToken> findAllByUserId(String userId);

    List<UserToken> findAllByUserIdAndType(String userId, UserTokenType type);

}
