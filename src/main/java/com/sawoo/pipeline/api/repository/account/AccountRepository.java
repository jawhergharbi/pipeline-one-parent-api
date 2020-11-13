package com.sawoo.pipeline.api.repository.account;

import com.sawoo.pipeline.api.model.account.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends MongoRepository<Account, String>, AccountRepositoryCustom {

    Optional<Account> findByEmail(String email);

    Optional<Account> findByLinkedInUrl(String linkedInUrl);

    @Query("{'users': {'$ref': 'user' , '$id': {'$oid': ?0}}}")
    List<Account> findByUserId(String userId);
}
