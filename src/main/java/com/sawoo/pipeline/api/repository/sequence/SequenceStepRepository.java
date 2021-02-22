package com.sawoo.pipeline.api.repository.sequence;

import com.sawoo.pipeline.api.model.sequence.SequenceStep;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SequenceStepRepository extends MongoRepository<SequenceStep, String> {

}
