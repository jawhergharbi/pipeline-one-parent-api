package com.sawoo.pipeline.api.repository.prospect;

import com.sawoo.pipeline.api.model.prospect.Prospect;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@JaversSpringDataAuditable
public interface ProspectRepository extends MongoRepository<Prospect, String> {

    List<Prospect> findAllByIdIn(List<String> ids);
}
