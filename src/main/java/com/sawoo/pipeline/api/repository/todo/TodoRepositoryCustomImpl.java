package com.sawoo.pipeline.api.repository.todo;

import com.mongodb.client.result.DeleteResult;
import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.model.todo.TodoSearch;
import com.sawoo.pipeline.api.model.todo.TodoSourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class TodoRepositoryCustomImpl implements TodoRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Todo> findBy(Integer status, Integer type, List<String> componentIds) {
        Criteria criteria = new Criteria();
        List<Criteria> andCriteria = new ArrayList<>();
        if (status != null) {
            andCriteria.add(Criteria.where("status").is(status));
        }
        if (type != null) {
            andCriteria.add(Criteria.where("type").is(type));
        }
        if (componentIds != null && !componentIds.isEmpty()) {
            andCriteria.add(Criteria.where("componentId").in(componentIds));
        }
        criteria.andOperator(andCriteria.toArray(new Criteria[0]));
        return mongoTemplate.find(new Query(criteria), Todo.class);
    }

    public List<Todo> findByStatusAndType(List<Integer> status, List<Integer> type, List<String> componentIds) {
        TodoSearch searchCriteria = TodoSearch.builder()
                .status(status)
                .types(type)
                .componentIds(componentIds)
                .build();
        return searchBy(searchCriteria);
    }

    @Override
    public List<Todo> searchBy(TodoSearch searchCriteria) {
        Criteria criteria = new Criteria();
        List<Criteria> andCriteria = buildAndCriteria(searchCriteria);
        criteria.andOperator(andCriteria.toArray(new Criteria[0]));
        return mongoTemplate.find(new Query(criteria), Todo.class);
    }

    @Override
    public long remove(TodoSearch searchCriteria) {
        Criteria criteria = new Criteria();
        List<Criteria> andCriteria = buildAndCriteria(searchCriteria);
        criteria.andOperator(andCriteria.toArray(new Criteria[0]));
        DeleteResult result = mongoTemplate.remove(new Query(criteria), Todo.class);
        return result.getDeletedCount();
    }

    private List<Criteria> buildAndCriteria(TodoSearch searchCriteria) {
        List<Criteria> andCriteria = new ArrayList<>();

        List<Integer> status = searchCriteria.getStatus();
        if (status != null && !status.isEmpty()) {
            andCriteria.add(Criteria.where("status").in(status));
        }

        List<Integer> type = searchCriteria.getTypes();
        if (type != null && !type.isEmpty()) {
            andCriteria.add(Criteria.where("type").in(type));
        }

        List<String> componentIds = searchCriteria.getComponentIds();
        if (componentIds != null && !componentIds.isEmpty()) {
            andCriteria.add(Criteria.where("componentId").in(componentIds));
        }

        List<String> sourceIds = searchCriteria.getSourceId();
        if (sourceIds != null && !sourceIds.isEmpty()) {
            andCriteria.add(Criteria.where("source.sourceId").in(sourceIds));
        }

        List<Integer> sourceTypes = searchCriteria.getSourceType();
        if (sourceTypes != null && !sourceTypes.isEmpty()) {
            List<String> sourcesTypeIds = sourceTypes.stream().map(t -> TodoSourceType.fromValue(t).name()).collect(Collectors.toList());
            andCriteria.add(Criteria.where("source.type").in(sourcesTypeIds));
        }
        return andCriteria;
    }
}
