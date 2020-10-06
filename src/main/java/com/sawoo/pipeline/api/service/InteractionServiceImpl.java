package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.repository.client.ClientRepositoryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class InteractionServiceImpl implements InteractionService {

    private ClientRepositoryWrapper clientRepository;

    @Override
    public List<InteractionDTO> getByTypes(Integer[] types, Long[] clients) {
        return null;
    }
}
