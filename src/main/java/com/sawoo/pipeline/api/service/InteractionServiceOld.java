package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.dto.interaction.InteractionDTOOld;

import java.util.List;

public interface InteractionServiceOld {

    List<InteractionDTOOld> findBy(Integer[] types, Integer[] status, Long[] clients);
}
