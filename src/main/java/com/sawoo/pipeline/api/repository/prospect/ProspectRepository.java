package com.sawoo.pipeline.api.repository.prospect;

import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.repository.base.BaseMongoRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;

import java.util.List;

@JaversSpringDataAuditable
public interface ProspectRepository extends BaseMongoRepository<Prospect> {

    List<Prospect> findAllByIdIn(List<String> ids);
}
