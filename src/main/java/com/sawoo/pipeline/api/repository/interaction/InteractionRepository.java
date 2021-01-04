package com.sawoo.pipeline.api.repository.interaction;

import com.sawoo.pipeline.api.model.interaction.Interaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InteractionRepository extends MongoRepository<Interaction, String>, InteractionRepositoryCustom {

    List<Interaction> findByComponentId(String componentId);

    List<Interaction> findByComponentIdIn(List<String> componentId);
}
