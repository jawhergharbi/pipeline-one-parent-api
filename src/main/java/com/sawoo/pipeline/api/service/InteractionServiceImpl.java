package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.repository.client.ClientRepositoryWrapper;

import java.util.List;

public class InteractionServiceImpl implements InteractionService {

    private ClientRepositoryWrapper clientRepository;

    @Override
    public List<InteractionDTO> getByTypes(Integer[] types, Long[] clients) {
        return null;
    }
}
