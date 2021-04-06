package com.sawoo.pipeline.api.service.todo;

import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.repository.todo.TodoRepository;
import com.sawoo.pipeline.api.service.base.BaseProxyService;
import com.sawoo.pipeline.api.service.base.BaseService;

import java.util.List;

public interface TodoService extends BaseService<TodoDTO>, BaseProxyService<TodoRepository, TodoMapper> {

    List<TodoDTO> findBy(List<String> componentIds, List<Integer> status, List<Integer> types);

}
