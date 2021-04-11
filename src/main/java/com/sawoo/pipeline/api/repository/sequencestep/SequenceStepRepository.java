package com.sawoo.pipeline.api.repository.sequencestep;

import com.sawoo.pipeline.api.model.sequence.SequenceStep;
import com.sawoo.pipeline.api.repository.base.BaseMongoRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;

@JaversSpringDataAuditable
public interface SequenceStepRepository extends BaseMongoRepository<SequenceStep> {

}
