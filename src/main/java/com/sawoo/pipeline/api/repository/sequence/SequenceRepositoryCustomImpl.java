package com.sawoo.pipeline.api.repository.sequence;

import com.sawoo.pipeline.api.common.CommonUtils;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.model.sequence.SequenceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class SequenceRepositoryCustomImpl implements SequenceRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Sequence> findByUsersAndStatus(Set<String> userIds, SequenceStatus status) {
        Criteria criteria = new Criteria();
        List<Criteria> andCriteria = new ArrayList<>();
        if (status != null) {
            andCriteria.add(Criteria.where("status").is(status));
        }
        if (CommonUtils.isNotEmptyNorNull(userIds)) {
            andCriteria.add(Criteria.where("users.userId").in(userIds));
        }
        criteria.andOperator(andCriteria.toArray(new Criteria[0]));
        return mongoTemplate.find(new Query(criteria), Sequence.class);
    }
}
