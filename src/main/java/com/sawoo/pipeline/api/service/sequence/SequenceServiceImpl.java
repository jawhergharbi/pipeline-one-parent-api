package com.sawoo.pipeline.api.service.sequence;

import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.repository.sequence.SequenceRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Slf4j
@Service
@Validated
public class SequenceServiceImpl extends BaseServiceImpl<SequenceDTO, Sequence, SequenceRepository, SequenceMapper> implements SequenceService {

    @Autowired
    public SequenceServiceImpl(SequenceRepository repository, SequenceMapper mapper, SequenceServiceEventListener eventListener) {
        super(repository, mapper, DBConstants.SEQUENCE_DOCUMENT, eventListener);
    }

    @Override
    public Optional<Sequence> entityExists(SequenceDTO entityToCreate) {
        String entityId = entityToCreate.getId();
        log.debug(
                "Checking entity existence. [type: {}, id: {}]",
                DBConstants.SEQUENCE_DOCUMENT,
                entityId);
        return entityId == null ? Optional.empty() : getRepository().findById(entityToCreate.getId());
    }
}
