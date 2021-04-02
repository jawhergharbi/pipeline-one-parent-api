package com.sawoo.pipeline.api.service.todo;


import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.repository.todo.TodoRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
import com.sawoo.pipeline.api.service.infra.audit.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class TodoServiceImpl extends BaseServiceImpl<TodoDTO, Todo, TodoRepository, TodoMapper> implements TodoService {

    @Autowired
    public TodoServiceImpl(TodoRepository repository, TodoMapper mapper, ApplicationEventPublisher eventPublisher, AuditService audit) {
        super(repository, mapper, DBConstants.TODO_DOCUMENT, eventPublisher, audit);
    }

    @Override
    public Optional<Todo> entityExists(TodoDTO entityToCreate) {
        log.debug(
                "Checking entity existence. [type: {}, id: {}]",
                DBConstants.TODO_DOCUMENT,
                entityToCreate.getId());
        if (entityToCreate.getId() == null || entityToCreate.getId().length() == 0) {
            return Optional.empty();
        } else {
            return  getRepository().findById(entityToCreate.getId());
        }
    }

    @Override
    public List<TodoDTO> findBy(List<String> componentIds, List<Integer> status, List<Integer> types) {
        log.debug("Getting todos from components with ids [{}] and status [{}] and types[{}]", componentIds, status, types);
        List<TodoDTO> todos = getRepository()
                .findByStatusAndType(status, types, componentIds)
                .stream()
                .map(getMapper().getMapperOut()::getDestination)
                .collect(Collectors.toList());
        log.debug("[{}] todo/s has/have been found from components with ids [{}] and status [{}] and types [{}]",
                todos.size(),
                componentIds,
                status,
                types);
        return todos;
    }
}
