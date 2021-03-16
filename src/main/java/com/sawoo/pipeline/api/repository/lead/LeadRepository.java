package com.sawoo.pipeline.api.repository.lead;

import com.sawoo.pipeline.api.model.lead.Lead;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@JaversSpringDataAuditable
public interface LeadRepository extends MongoRepository<Lead, String> {

    List<Lead> findAllByIdIn(List<String> ids);
}
