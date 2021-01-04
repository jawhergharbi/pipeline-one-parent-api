package com.sawoo.pipeline.api.service.leadinteraction;


import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.lead.LeadInteraction;
import com.sawoo.pipeline.api.repository.leadinteraction.LeadInteractionRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Slf4j
@Service
@Validated
public class LeadInteractionServiceImpl extends BaseServiceImpl<LeadInteractionDTO, LeadInteraction, LeadInteractionRepository, LeadInteractionMapper> implements LeadInteractionService {

    @Autowired
    public LeadInteractionServiceImpl(LeadInteractionRepository repository, LeadInteractionMapper mapper) {
        super(repository, mapper, DBConstants.LEAD_INTERACTION_DOCUMENT);
    }

    @Override
    public Optional<LeadInteraction> entityExists(LeadInteractionDTO entityToCreate) {
        log.debug(
                "Checking entity existence. [type: {}, linkedIn: {}]",
                DBConstants.LEAD_INTERACTION_DOCUMENT,
                entityToCreate.getId());
        if (entityToCreate.getId() == null || entityToCreate.getId().length() == 0) {
            return Optional.empty();
        } else {
            return  getRepository().findById(entityToCreate.getId());
        }
    }
}
