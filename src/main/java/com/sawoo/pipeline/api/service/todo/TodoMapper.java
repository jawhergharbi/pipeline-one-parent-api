package com.sawoo.pipeline.api.service.todo;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.service.base.BaseMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class TodoMapper implements BaseMapper<TodoDTO, Todo> {

    private final JMapper<TodoDTO, Todo> mapperOut = new JMapper<>(TodoDTO.class, Todo.class);
    private final JMapper<TodoAssigneeDTO, Todo> assigneeMapperOut = new JMapper<>(TodoAssigneeDTO.class, Todo.class);
    private final JMapper<Todo, TodoDTO> mapperIn = new JMapper<>(Todo.class, TodoDTO.class);
}
