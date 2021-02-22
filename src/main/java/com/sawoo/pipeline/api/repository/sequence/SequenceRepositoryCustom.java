package com.sawoo.pipeline.api.repository.sequence;

import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.model.sequence.SequenceStatus;

import java.util.List;
import java.util.Set;

public interface SequenceRepositoryCustom {

    List<Sequence> findByUsersAndStatus(Set<String> userIds, SequenceStatus status);
}
