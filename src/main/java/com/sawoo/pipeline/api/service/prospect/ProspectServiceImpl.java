package com.sawoo.pipeline.api.service.prospect;


import com.sawoo.pipeline.api.dto.lead.ProspectDTO;
import com.sawoo.pipeline.api.model.DataStoreConstants;
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
public class ProspectServiceImpl extends BaseServiceImpl<ProspectDTO, Prospect, ProspectRepository> implements ProspectService {

    @Autowired
    public ProspectServiceImpl(ProspectRepository repository, ProspectMapper mapper, ProspectServiceEventListener eventListener) {
        super(repository, mapper, DataStoreConstants.PROSPECT_DOCUMENT, eventListener);
    }

    @Override
    public Optional<Prospect> entityExists(ProspectDTO entityToCreate) {
        return getRepository().findByLinkedInUrl(entityToCreate.getLinkedInUrl());
    }
}
