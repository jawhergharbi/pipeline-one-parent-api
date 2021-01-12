package com.sawoo.pipeline.api.repository.interaction;

import com.sawoo.pipeline.api.model.interaction.Interaction;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class InteractionRepositoryCustomImpl implements InteractionRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Interaction> findBy(Integer status, Integer type, List<String> componentIds) {
        Criteria criteria = new Criteria();
        List<Criteria> andCriteria = new ArrayList<>();
        if (status != null) {
            andCriteria.add(Criteria.where("status").is(status));
        }
        if (type != null) {
            andCriteria.add(Criteria.where("type").is(type));
        }
        if (componentIds != null && componentIds.size() > 0) {
            andCriteria.add(Criteria.where("componentId").in(componentIds));
        }
        criteria.andOperator(andCriteria.toArray(new Criteria[0]));
        return mongoTemplate.find(new Query(criteria), Interaction.class);
    }

    public List<Interaction> findByStatusAndType(List<Integer> status, List<Integer> type, List<String> componentIds) {
        Criteria criteria = new Criteria();
        List<Criteria> andCriteria = new ArrayList<>();
        if (status != null && status.size() > 0) {
            andCriteria.add(Criteria.where("status").in(status));
        }
        if (type != null && type.size() > 0) {
            andCriteria.add(Criteria.where("type").in(type));
        }
        if (componentIds != null && componentIds.size() > 0) {
            andCriteria.add(Criteria.where("componentId").in(componentIds));
        }
        criteria.andOperator(andCriteria.toArray(new Criteria[0]));
        return mongoTemplate.find(new Query(criteria), Interaction.class);
    }
}
