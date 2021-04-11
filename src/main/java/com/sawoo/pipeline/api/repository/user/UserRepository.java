package com.sawoo.pipeline.api.repository.user;

import com.sawoo.pipeline.api.model.user.User;
import com.sawoo.pipeline.api.repository.base.BaseMongoRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;

import java.util.List;
import java.util.Optional;

@JaversSpringDataAuditable
public interface UserRepository extends BaseMongoRepository<User> {

    Optional<User> findByEmail(String email);

    List<User> findByActiveTrueAndRolesIn(List<String> roles);

}
