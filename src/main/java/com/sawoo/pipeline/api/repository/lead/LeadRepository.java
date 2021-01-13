package com.sawoo.pipeline.api.repository.lead;

import com.sawoo.pipeline.api.model.lead.Lead;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadRepository extends MongoRepository<Lead, String> {

    List<Lead> findAllByIdIn(List<String> ids);
}
