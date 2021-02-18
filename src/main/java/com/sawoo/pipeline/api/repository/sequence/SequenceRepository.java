package com.sawoo.pipeline.api.repository.sequence;

import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.model.sequence.SequenceStatus;
import com.sawoo.pipeline.api.model.sequence.SequenceUserType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Set;

public interface SequenceRepository extends MongoRepository<Sequence, String>, SequenceRepositoryCustom {

    List<Sequence> findByStatus(SequenceStatus status);

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
