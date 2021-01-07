package com.sawoo.pipeline.api.repository.interaction;

import com.sawoo.pipeline.api.model.interaction.Interaction;

import java.util.List;

public interface InteractionRepositoryCustom {

    List<Interaction> findBy(Integer status, Integer type, List<String> componentIds);

    List<Interaction> findByStatusAndType(List<Integer> status, List<Integer> type, List<String> componentIds);
}
