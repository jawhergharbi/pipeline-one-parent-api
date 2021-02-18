package com.sawoo.pipeline.api.service.sequence;

import com.sawoo.pipeline.api.common.CommonUtils;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.dto.sequence.SequenceUserDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.model.sequence.SequenceUserType;
import com.sawoo.pipeline.api.repository.sequence.SequenceRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class SequenceServiceImpl extends BaseServiceImpl<SequenceDTO, Sequence, SequenceRepository, SequenceMapper> implements SequenceService {

    @Autowired
    public SequenceServiceImpl(SequenceRepository repository,
                               SequenceMapper mapper,
                               SequenceServiceEventListener eventListener) {
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
    public List<SequenceDTO> findByAccountIds(Set<String> accountIds) throws CommonServiceException {
        log.debug("Retrieve the list of sequences for accounts [ids: {}]", accountIds);
        if (CommonUtils.isEmptyOrNull(accountIds)) {
            return findAll();
        } else {
            List<Sequence> sequences = getRepository().findByUsers(accountIds);

            log.debug("[{}] sequence/s has/have been found for account [ids: {}]", sequences.size(), accountIds);

            return sequences.stream().map(s -> {
                        SequenceDTO sequence = getMapper().getMapperOut().getDestination(s);
                        Optional<SequenceUserDTO> owner = sequence.getUsers().stream().filter(su -> SequenceUserType.OWNER.equals(su.getType())).findFirst();
                        owner.ifPresent(o -> sequence.setOwnerId(o.getUserId()));
                        return sequence;
                    }).collect(Collectors.toList());
        }
    }
}
