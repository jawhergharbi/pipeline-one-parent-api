package com.sawoo.pipeline.api.service.prospect;


import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.repository.ProspectRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Slf4j
@Service
@Validated
public class ProspectServiceImpl extends BaseServiceImpl<ProspectDTO, Prospect, ProspectRepository, ProspectMapper> implements ProspectService {

    @Autowired
    public ProspectServiceImpl(ProspectRepository repository, ProspectMapper mapper, ProspectServiceEventListener eventListener) {
        super(repository, mapper, DBConstants.PROSPECT_DOCUMENT, eventListener);
    }

    @Override
    public Optional<Prospect> entityExists(ProspectDTO entityToCreate) {
        log.debug(
                "Checking entity existence. [type: {}, linkedIn: {}]",
                DBConstants.PROSPECT_DOCUMENT,
                entityToCreate.getLinkedInUrl());
        return getRepository().findByLinkedInUrl(entityToCreate.getLinkedInUrl());
    }
}
