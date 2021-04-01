package com.sawoo.pipeline.api.controller.todo;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerDelegator;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.service.todo.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TodoControllerDelegator extends BaseControllerDelegator<TodoDTO, TodoService> {

    @Autowired
    public TodoControllerDelegator(TodoService service) {
        super(service, ControllerConstants.TODO_CONTROLLER_API_BASE_URI);
    }

    @Override
    public String getComponentId(TodoDTO dto) {
        return dto.getId();
    }
}
