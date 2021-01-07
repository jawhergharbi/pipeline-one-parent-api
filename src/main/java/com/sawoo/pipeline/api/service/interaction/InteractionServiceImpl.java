package com.sawoo.pipeline.api.service.interaction;


import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.interaction.Interaction;
import com.sawoo.pipeline.api.repository.interaction.InteractionRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class InteractionServiceImpl extends BaseServiceImpl<InteractionDTO, Interaction, InteractionRepository, InteractionMapper> implements InteractionService {

    @Autowired
    public InteractionServiceImpl(InteractionRepository repository, InteractionMapper mapper) {
        super(repository, mapper, DBConstants.INTERACTION_DOCUMENT);
    }

    @Override
    public Optional<Interaction> entityExists(InteractionDTO entityToCreate) {
        log.debug(
                "Checking entity existence. [type: {}, id: {}]",
                DBConstants.INTERACTION_DOCUMENT,
                entityToCreate.getId());
        if (entityToCreate.getId() == null || entityToCreate.getId().length() == 0) {
            return Optional.empty();
        } else {
            return  getRepository().findById(entityToCreate.getId());
        }
    }

    @Override
    public List<InteractionDTO> findBy(List<String> componentIds, List<Integer> status, List<Integer> types) {
        log.debug("Getting interactions from components with ids [{}] and status [{}] and types[{}]", componentIds, status, types);
        List<InteractionDTO> interactions = getRepository()
                .findByStatusAndType(status, types, componentIds)
                .stream()
                .map(getMapper().getMapperOut()::getDestination)
                .collect(Collectors.toList());
        log.debug("[{}] interaction/s has/have been found from components with ids [{}] and status [{}] and types [{}]",
                interactions.size(),
                componentIds,
                status,
                types);
        return interactions;
    }
}
