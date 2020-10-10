package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;

import java.util.List;

public interface InteractionService {

    List<InteractionDTO> findBy(Integer[] types, Integer[] status, Long[] clients);
}
