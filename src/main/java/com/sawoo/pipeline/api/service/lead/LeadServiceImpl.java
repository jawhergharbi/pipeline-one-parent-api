package com.sawoo.pipeline.api.service.lead;


import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.repository.lead.LeadRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Slf4j
@Service
@Validated
public class LeadServiceImpl extends BaseServiceImpl<LeadDTO, Lead, LeadRepository, LeadMapper> implements LeadService {

    @Autowired
    public LeadServiceImpl(LeadRepository repository, LeadMapper mapper) {
        super(repository, mapper, DBConstants.LEAD_DOCUMENT);
    }

    @Override
    public Optional<Lead> entityExists(LeadDTO entityToCreate) {
        log.debug(
                "Checking entity existence. [type: {}, id: {}]",
                DBConstants.PROSPECT_DOCUMENT,
                entityToCreate.getId());
        return getRepository().findById(entityToCreate.getId());
    }
}
