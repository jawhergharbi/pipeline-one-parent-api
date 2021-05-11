package com.sawoo.pipeline.api.repository.todo;

import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.repository.base.BaseMongoRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;

import java.util.List;

@JaversSpringDataAuditable
public interface TodoRepository extends BaseMongoRepository<Todo>, TodoRepositoryCustom {

    List<Todo> findByComponentId(String componentId);

    List<Todo> findByComponentIdIn(List<String> componentId);

    List<Todo> findByAssigneeId(String assigneeId);
}
