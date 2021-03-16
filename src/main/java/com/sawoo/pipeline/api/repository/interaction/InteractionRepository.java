package com.sawoo.pipeline.api.repository.interaction;

import com.sawoo.pipeline.api.model.interaction.Interaction;
import org.javers.spring.annotation.JaversAuditable;
import org.javers.spring.annotation.JaversAuditableAsync;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@JaversSpringDataAuditable
public interface InteractionRepository extends MongoRepository<Interaction, String>, InteractionRepositoryCustom {

    List<Interaction> findByComponentId(String componentId);

    List<Interaction> findByComponentIdIn(List<String> componentId);

    List<Interaction> findByAssigneeId(String assigneeId);
}
