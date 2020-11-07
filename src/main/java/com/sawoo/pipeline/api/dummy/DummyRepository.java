package com.sawoo.pipeline.api.dummy;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DummyRepository extends MongoRepository<DummyEntity, String> {
}
