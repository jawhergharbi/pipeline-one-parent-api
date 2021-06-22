package com.sawoo.pipeline.api.service.prospect;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.UserCommon;
import com.sawoo.pipeline.api.dto.UserCommonType;
import com.sawoo.pipeline.api.dto.prospect.ProspectTodoDTO;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.dto.todo.TodoSearchDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.model.todo.TodoSearch;
import com.sawoo.pipeline.api.model.todo.TodoSourceType;
import com.sawoo.pipeline.api.repository.prospect.ProspectRepository;
import com.sawoo.pipeline.api.service.todo.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class ProspectTodoServiceDecorator implements ProspectTodoService {

    private final TodoService todoService;
    private final ProspectRepository repository;
    private final ProspectServiceDecoratorHelper helper;
    private final ProspectMapper mapper;

    @Override
    public TodoDTO addTODO(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String prospectId,
            @Valid TodoDTO todo)
            throws ResourceNotFoundException, CommonServiceException {
        log.debug("Add new todo for prospect id: [{}].", prospectId);

        Prospect prospect = findProspectById(prospectId);
        List<Todo> todos = prospect.getTodos();

        // Validate schedule
        validateTodoScheduled(todos, todo, prospectId);

        todo.setComponentId(prospectId);
                final TodoDTO savedTODO = todoService.create(todo);

        log.debug("Prospect todo has been created for prospect id: [{}]. Todo id [{}]", prospectId, todo.getId());

        todos.add(todoService.getMapper().getMapperIn().getDestination(savedTODO));
        prospect.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
        repository.save(prospect);

        return savedTODO;
    }

    @Override
    public List<TodoDTO> addTODOList(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String prospectId,
            @Valid List<TodoDTO> todoList) throws ResourceNotFoundException, CommonServiceException {
        log.debug("Add a list of TODOs for prospect id: [{}]. List size [{}]", prospectId, todoList.size());

        Prospect prospect = findProspectById(prospectId);
        List<Todo> todos = prospect.getTodos();

        // TODO add contract to create multiple TODOs in one single insert
        List<TodoDTO> newTodoList = todoList.stream().map(t -> {
            validateTodoScheduled(todos, t, prospectId);
            t.setComponentId(prospectId);
            TodoDTO savedTODO = todoService.create(t);
            log.debug("Prospect TODO has been created for prospect id: [{}]. TODO id [{}]", prospectId, savedTODO.getId());
            return savedTODO;
        }).collect(Collectors.toList());

        todos.addAll(newTodoList
                        .stream()
                        .map(todoService.getMapper().getMapperIn()::getDestination)
                .collect(Collectors.toList()));
        prospect.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
        repository.save(prospect);

        return newTodoList;
    }

    @Override
    public TodoDTO removeTODO(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String prospectId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String todoId)
            throws ResourceNotFoundException {
        log.debug("Remove todo from prospect id: [{}].", prospectId);

        Prospect prospect = findProspectById(prospectId);
        return prospect.getTodos()
                .stream()
                .filter(i -> i.getId().equals(todoId))
                .findAny()
                .map( i -> {
                    prospect.getTodos().remove(i);
                    prospect.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
                    repository.save(prospect);
                    log.debug("Todo with id [{}] for prospect id [{}] has been deleted.", todoId, prospectId);
                    return todoService.delete(i.getId());
                })
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.TODO_DOCUMENT, todoId}));
    }

    @Override
    public List<TodoDTO> removeTODOList(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String prospectId,
            @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
            @NotEmpty(message = ExceptionMessageConstants.COMMON_ILLEGAL_ENUMERATION_VALUE_EXCEPTION) List<String> todoIds)
            throws ResourceNotFoundException {
        Prospect prospect = findProspectById(prospectId);

        List<Todo> todosToBeDeleted = prospect.getTodos()
                .stream()
                .filter(t -> todoIds.contains(t.getId()))
                .collect(Collectors.toList());

        List<String> idsTodoToBeDeleted = todosToBeDeleted.stream().map(Todo::getId).collect(Collectors.toList());

        if (idsTodoToBeDeleted.size() != todoIds.size()) {
            todoIds.removeAll(idsTodoToBeDeleted);
            throw new ResourceNotFoundException(
                    ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                    new String[]{ DBConstants.TODO_DOCUMENT, String.join(",", todoIds) });
        } else {
            prospect.getTodos().removeAll(todosToBeDeleted);
            prospect.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
            repository.save(prospect);
            log.debug("Todo/s with id/s [{}] for prospect id [{}] has been deleted.", todoIds, prospectId);
            return todoService.deleteByIds(idsTodoToBeDeleted);
        }
    }

    @Override
    public List<TodoAssigneeDTO> getTODOs(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String prospectId)
            throws ResourceNotFoundException {
        log.debug("Get TODOs from prospect id: [{}].", prospectId);

        Prospect prospect = findProspectById(prospectId);
        List<Todo> todos = prospect.getTodos();
        List<TodoAssigneeDTO> assigneeTODOs = Collections.emptyList();
        if (!todos.isEmpty()) {
            final List<UserCommon> users = helper.getUsers(prospectId);
            assigneeTODOs = todos
                    .stream()
                    .map(t -> mapTODO(t, users, prospect))
                    .collect(Collectors.toList());
        }
        log.debug("[{}] todos has been found for prospect id [{}]", prospectId, todos.size());

        return  assigneeTODOs;
    }

    @Override
    public TodoAssigneeDTO getTODO(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String prospectId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String todoId)
            throws ResourceNotFoundException {
        log.debug("Get todo id [{}] from prospect id: [{}].", todoId, prospectId);
        Prospect prospect = findProspectById(prospectId);
        return prospect
                .getTodos()
                .stream()
                .filter(i -> todoId.equals(i.getId()))
                .findAny()
                .map(i -> {
                    log.debug("Todo id [{}] for prospect id [{}] has been found. \nTodo: [{}]", todoId, prospectId, i);
                    List<UserCommon> users = helper.getUsers(prospectId);
                    return mapTODO(i, users, prospect);
                })
                .orElseThrow( () ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.PROSPECT_DOCUMENT, todoId}) );
    }

    @Override
    public List<ProspectTodoDTO> findBy(List<String> prospectIds, List<Integer> status, List<Integer> channels) throws CommonServiceException {
        log.debug("Get TODOs from prospects [{}] with status [{}] and channels[{}]", prospectIds, status, channels);

        List<TodoDTO> todos = todoService.searchBy(prospectIds, status, channels);
        if (!todos.isEmpty()) {
            List<Prospect> prospects = prospectIds.isEmpty() ? Collections.emptyList() : repository.findAllByIdIn(prospectIds);
            return mapTODOsProspects(todos, prospects, null);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<ProspectTodoDTO> searchBy(TodoSearch searchCriteria) {
        log.debug("Search TODOs with the following search criteria [{}]", searchCriteria);

        List<TodoDTO> todos = todoService.searchBy(searchCriteria);
        if (!todos.isEmpty()) {
            List<Prospect> prospects = searchCriteria.getComponentIds().isEmpty() ?
                    Collections.emptyList() :
                    repository.findAllByIdIn(searchCriteria.getComponentIds());
            List<UserCommon> users = helper.getUsersByAccountIdIn(searchCriteria.getAccountIds());
            return mapTODOsProspects(todos, prospects, users);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public long removeTODOs(TodoSearchDTO searchCriteria) {
        log.debug("Remove TODOs with the following search criteria [{}]", searchCriteria);
        List<TodoDTO> todos = todoService.findAllAndRemove(searchCriteria);
        if (todos != null && !todos.isEmpty()) {
            List<String> prospectIds = searchCriteria.getComponentIds();
            log.debug("[{}] TODOs will be removed from their prospects [{}]", todos.size(), prospectIds);
            prospectIds.forEach(prospectId -> removeDeletedTODOs(prospectId, todos));
        }
        return todos != null ? todos.size() : 0;
    }

    private void removeDeletedTODOs(String prospectId, List<TodoDTO> todos) {
        Predicate<Todo> isContained = t -> todos.stream().anyMatch(todo -> todo.getId().equals(t.getId()));
        Prospect prospect = findProspectById(prospectId);
        List<Todo> updatedTodos = prospect
                .getTodos()
                .stream()
                .filter(isContained)
                .collect(Collectors.toList());
        prospect.setTodos(updatedTodos);
        prospect.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
        repository.save(prospect);
        log.debug("Prospect TODOs updated. Prospect id: [{}]. Remove some TODOs", prospectId);
    }

    @Override
    public long removeTODOs(List<String> todoIds) {
        log.debug("Remove TODOs with the following ids [{}]", todoIds);
        return todoService.deleteByIds(todoIds).stream().count();
    }

    private Prospect findProspectById(String prospectId) throws ResourceNotFoundException {
        return repository
                .findById(prospectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.PROSPECT_DOCUMENT, prospectId }));
    }

    private TodoAssigneeDTO mapTODO(Todo t, List<UserCommon> users, Prospect prospect) {
        TodoAssigneeDTO todo = todoService.getMapper().getAssigneeMapperOut().getDestination(t);
        if (todo.getAssigneeId().equals(prospect.getId())) {
            todo.setAssignee(UserCommon.builder()
                    .id(prospect.getId())
                    .fullName(prospect.getPerson().getFullName())
                    .type(UserCommonType.PROSPECT)
                    .build());
        } else {
            Optional<UserCommon> user = users.stream().filter(u -> u.getId().equals(todo.getAssigneeId())).findAny();
            user.ifPresent(todo::setAssignee);
        }
        return todo;
    }

    private List<ProspectTodoDTO> mapTODOsProspects(List<TodoDTO> todos, List<Prospect> prospects, List<UserCommon> users) {
        return todos
                .stream()
                .map(t -> {
                    ProspectTodoDTO todo = mapper.getTodoMapperOut().getDestination(t);
                    Optional<Prospect> prospect = prospects.stream().filter(l -> l.getId().equals(todo.getComponentId())).findAny();
                    Optional<UserCommon> user = users == null ? Optional.empty() : users.stream().filter(l -> l.getId().equals(todo.getAssigneeId())).findAny();
                    prospect.ifPresent(value -> todo.setProspect(mapper.getProspectTodoMapperOut().getDestination(value)));
                    user.ifPresent(todo::setAssignee);
                    return todo;
                }).collect(Collectors.toList());
    }

    private void validateTodoScheduled(List<Todo> currentTodos, TodoDTO newTodo, String prospectId) {
        Predicate<Todo> filter  = t -> t.getScheduled().equals(newTodo.getScheduled())
                && (newTodo.getSource() == null || newTodo.getSource().getType().equals(TodoSourceType.MANUAL));
        currentTodos.stream()
                .filter(filter)
                .findAny()
                .ifPresent(t -> {
                    throw new CommonServiceException(
                            ExceptionMessageConstants.PROSPECT_TODO_ADD_PROSPECT_SLOT_ALREADY_SCHEDULED_EXCEPTION,
                            new String[]{prospectId, newTodo.getScheduled().toString()});
                });
    }
}
