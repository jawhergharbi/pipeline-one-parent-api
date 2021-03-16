package com.sawoo.pipeline.api.repository.sequencestep;

import com.sawoo.pipeline.api.model.sequence.SequenceStep;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;

@JaversSpringDataAuditable
public interface SequenceStepRepository extends MongoRepository<SequenceStep, String> {

}
