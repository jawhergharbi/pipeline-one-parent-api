package com.sawoo.pipeline.api.repository.todo;

import com.sawoo.pipeline.api.model.todo.Todo;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@JaversSpringDataAuditable
public interface TodoRepository extends MongoRepository<Todo, String>, TodoRepositoryCustom {

    List<Todo> findByComponentId(String componentId);

    List<Todo> findByComponentIdIn(List<String> componentId);

    List<Todo> findByAssigneeId(String assigneeId);
}
