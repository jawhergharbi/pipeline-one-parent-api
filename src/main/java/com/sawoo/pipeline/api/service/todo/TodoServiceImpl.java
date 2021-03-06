package com.sawoo.pipeline.api.service.todo;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.dto.todo.TodoSearchDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.model.todo.TodoSearch;
import com.sawoo.pipeline.api.repository.todo.TodoRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
import com.sawoo.pipeline.api.service.infra.audit.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
            return getRepository().findById(entityToCreate.getId());
        }
    }

    @Override
    public List<TodoDTO> searchBy(List<String> componentIds, List<Integer> status, List<Integer> channels) {
        log.debug("Getting todos from components with ids [{}] and status [{}] and channels[{}]", componentIds, status, channels);
        List<TodoDTO> todos = getRepository()
                .findByStatusAndChannel(status, channels, componentIds)
                .stream()
                .map(getMapper().getMapperOut()::getDestination)
                .collect(Collectors.toList());
        log.debug("[{}] todo/s has/have been found from components with ids [{}] and status [{}] and channels [{}]",
                todos.size(),
                componentIds,
                status,
                channels);
        return todos;
    }

    @Override
    public List<TodoDTO> searchBy(@NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR) TodoSearch search) {
        log.debug("Search TODOs with the following search criteria: [{}]", search);
        List<TodoDTO> todos = getRepository()
                .searchBy(search)
                .stream()
                .map(getMapper().getMapperOut()::getDestination)
                .collect(Collectors.toList());
        log.debug("[{}] todo/s has/have been found with the following search criteria: [{}]", todos.size(), search);
        return todos;
    }

    @Override
    public long remove(
            @Valid @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR) TodoSearchDTO searchCriteria) {
        log.debug("Delete TODOs with the following search criteria: [{}]", searchCriteria);
        long deleted = getRepository().remove(getMapper().getMapperTodoSearchIn().getDestination(searchCriteria));
        log.debug("[{}] todo/s has/have been deleted with the following search criteria: [{}]", deleted, searchCriteria);
        return deleted;
    }

    @Override
    public List<TodoDTO> findAllAndRemove(
            @Valid @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR) TodoSearchDTO searchCriteria) {
        log.debug("FindAllAndRemove TODOs with the following search criteria: [{}]", searchCriteria);

        List<Todo> todos = getRepository().findAllAndRemove(getMapper().getMapperTodoSearchIn().getDestination(searchCriteria));

        log.debug("[{}] todo/s has/have been deleted with the following search criteria: [{}]", todos.size(), searchCriteria);

        return todos.stream().map(getMapper().getMapperOut()::getDestination).collect(Collectors.toList());
    }
}
