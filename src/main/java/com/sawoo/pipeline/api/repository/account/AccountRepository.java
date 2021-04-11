package com.sawoo.pipeline.api.repository.account;

import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.repository.base.BaseMongoRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

@JaversSpringDataAuditable
public interface AccountRepository extends BaseMongoRepository<Account>, AccountRepositoryCustom {

    Optional<Account> findByEmail(String email);

    Optional<Account> findByLinkedInUrl(String linkedInUrl);

    @Query("{'users': {'$ref': 'user' , '$id': {'$oid': ?0}}}")
    List<Account> findByUserId(String userId);

    @Query("{'prospects': {'$ref': 'prospect' , '$id': {'$oid': ?0}}}")
    Optional<Account> findByProspectId(String prospectId);
}
