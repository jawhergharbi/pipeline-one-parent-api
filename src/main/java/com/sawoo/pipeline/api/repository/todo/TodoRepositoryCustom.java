package com.sawoo.pipeline.api.repository.todo;

import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.model.todo.TodoSearch;

import java.util.List;

public interface TodoRepositoryCustom {

    List<Todo> findBy(Integer status, Integer type, List<String> componentIds);

    List<Todo> findByStatusAndType(List<Integer> status, List<Integer> type, List<String> componentIds);

    List<Todo> searchBy(TodoSearch searchCriteria);

    long remove(TodoSearch searchCriteria);
}
