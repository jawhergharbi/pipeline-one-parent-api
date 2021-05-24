package com.sawoo.pipeline.api.repository.todo;

import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.model.todo.TodoSearch;

import java.util.List;

public interface TodoRepositoryCustom {

    List<Todo> findBy(Integer status, Integer channel, List<String> componentIds);

    List<Todo> findByStatusAndChannel(List<Integer> status, List<Integer> channel, List<String> componentIds);

    List<Todo> searchBy(TodoSearch searchCriteria);

    long remove(TodoSearch searchCriteria);

    List<Todo> findAllAndRemove(TodoSearch searchCriteria);
}
