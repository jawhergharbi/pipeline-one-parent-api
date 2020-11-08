package com.sawoo.pipeline.api.repository;

import com.sawoo.pipeline.api.model.prospect.Prospect;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProspectRepository extends MongoRepository<Prospect, String> {

    Optional<Prospect> findByLinkedInUrl(String linkedInUrl);
}
