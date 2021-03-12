package com.sawoo.pipeline.api.service.sequence;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.dto.sequence.SequenceStepDTO;
import com.sawoo.pipeline.api.dto.sequence.SequenceUserDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.repository.sequence.SequenceRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Validated
@Primary
public class SequenceServiceImpl extends BaseServiceImpl<SequenceDTO, Sequence, SequenceRepository, SequenceMapper> implements SequenceService {

    private final SequenceAccountService sequenceAccountService;
    private final SequenceStepsService sequenceStepService;

    @Autowired
    public SequenceServiceImpl(SequenceRepository repository,
                               SequenceMapper mapper,
                               SequenceServiceEventListener eventListener,
                               ApplicationEventPublisher eventPublisher,
                               SequenceAccountService sequenceAccountService,
                               SequenceStepsService sequenceStepService) {
        super(repository, mapper, DBConstants.SEQUENCE_DOCUMENT, eventListener, eventPublisher);
        this.sequenceAccountService = sequenceAccountService;
        this.sequenceStepService = sequenceStepService;
    }

    @Override
    public Optional<Sequence> entityExists(SequenceDTO entityToCreate) {
        log.debug(
                "Checking entity existence. [type: {}, name: {}, componentId: {}]",
                DBConstants.CAMPAIGN_DOCUMENT,
                entityToCreate.getName(),
                entityToCreate.getComponentId());
        return getRepository().findByComponentIdAndName(entityToCreate.getComponentId(), entityToCreate.getName());
    }

    @Override
    public SequenceDTO deleteUser(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String id,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String userId)
            throws ResourceNotFoundException, CommonServiceException {
        SequenceDTO sequence = findById(id);

        log.debug("Delete user id {} from sequence [id: {}, name: {}]",
                userId,
                sequence.getId(),
                sequence.getName());

        return update(id,
                SequenceDTO.builder()
                        .users(new HashSet<>(
                                Collections.singleton(
                                        SequenceUserDTO.builder()
                                                .userId(userId)
                                                .toBeDeleted(true)
                                                .build())))
                        .build());
    }

    @Override
    public List<SequenceDTO> findByAccountIds(Set<String> accountIds)
            throws CommonServiceException {
        return sequenceAccountService.findByAccountIds(accountIds);
    }

    @Override
    public SequenceStepDTO addStep(String sequenceId, SequenceStepDTO step)
            throws ResourceNotFoundException, CommonServiceException {
        return sequenceStepService.addStep(sequenceId, step);
    }

    @Override
    public SequenceStepDTO updateStep(String sequenceId, SequenceStepDTO step)
            throws ResourceNotFoundException, CommonServiceException {
        return sequenceStepService.updateStep(sequenceId, step);
    }

    @Override
    public SequenceStepDTO removeStep(String sequenceId, String sequenceStepId)
            throws ResourceNotFoundException, CommonServiceException {
        return sequenceStepService.removeStep(sequenceId, sequenceStepId);
    }

    @Override
    public List<SequenceStepDTO> getSteps(String sequenceId) throws ResourceNotFoundException {
        return sequenceStepService.getSteps(sequenceId);
    }
}
