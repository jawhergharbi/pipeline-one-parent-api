package com.sawoo.pipeline.api.repository.sequencestep;

import com.sawoo.pipeline.api.model.sequence.SequenceStep;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SequenceStepRepository extends MongoRepository<SequenceStep, String> {

}
