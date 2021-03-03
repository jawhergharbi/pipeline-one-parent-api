package com.sawoo.pipeline.api.service.sequencestep;

import com.sawoo.pipeline.api.dto.sequence.SequenceStepDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.sequence.SequenceStep;
import com.sawoo.pipeline.api.repository.sequencestep.SequenceStepRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Validated
public class SequenceStepServiceImpl extends BaseServiceImpl<SequenceStepDTO, SequenceStep, SequenceStepRepository, SequenceStepMapper> implements SequenceStepService{

    @Autowired
    public SequenceStepServiceImpl(SequenceStepRepository repository, SequenceStepMapper mapper) {
        super(repository, mapper, DBConstants.SEQUENCE_STEP_DOCUMENT);
    }

    @Override
    public Optional<SequenceStep> entityExists(SequenceStepDTO entityToCreate) {
        String entityId = entityToCreate.getId();
        log.debug(
                "Checking entity existence. [type: {}, id: {}]",
                DBConstants.SEQUENCE_STEP_DOCUMENT,
                entityId);
        return entityId == null ? Optional.empty() : getRepository().findById(entityToCreate.getId());
    }
}
