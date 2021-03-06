package com.sawoo.pipeline.api.repository.sequence;

import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.model.sequence.SequenceStatus;
import com.sawoo.pipeline.api.model.sequence.SequenceUserType;
import com.sawoo.pipeline.api.repository.base.BaseMongoRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@JaversSpringDataAuditable
public interface SequenceRepository extends BaseMongoRepository<Sequence>, SequenceRepositoryCustom {

    List<Sequence> findByStatus(SequenceStatus status);

    List<Sequence> findByComponentId(String componentId);

    List<Sequence> findByComponentIdAndStatus(String componentId, SequenceStatus status);

    List<Sequence> findByComponentIdIn(Set<String> componentIds);

    List<Sequence> findByComponentIdInAndStatus(Set<String> componentIds, SequenceStatus status);

    Optional<Sequence> findByComponentIdAndName(String componentId, String name);

    @Query(value = "{ users: { $elemMatch: { userId: ?0 } }}")
    List<Sequence> findByUser(String userId);

    @Query(value = "{ users: { $elemMatch: { userId: ?0 } }, status: ?1 }")
    List<Sequence> findByUserAndStatus(String userId, SequenceStatus status);

    @Query(value = "{ users: { $elemMatch: { userId: {$in: ?0} } } }")
    List<Sequence> findByUsers(Set<String> userIds);

    @Query(value = "{ users: { $elemMatch: { userId: ?0, type: ?1 } } }")
    List<Sequence> findByUserIdAndUserType(String userId, SequenceUserType type);

    @Query(value = "{ users: { $elemMatch: { userId: {$in: ?0}, type: ?1 } } }")
    List<Sequence> findByUserIdsAndUserType(Set<String> userIds, SequenceUserType type);
}
