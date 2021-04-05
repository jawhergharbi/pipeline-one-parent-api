package com.sawoo.pipeline.api.service.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.UserCommon;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.dto.lead.LeadTodoDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.todo.TodoSourceType;
import com.sawoo.pipeline.api.repository.lead.LeadRepository;
import com.sawoo.pipeline.api.service.todo.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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
public class LeadTodoServiceDecorator implements LeadTodoService {

    private final TodoService todoService;
    private final LeadRepository repository;
    private final LeadServiceDecoratorHelper helper;
    private final LeadMapper mapper;

    @Override
    public TodoDTO addTODO(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @Valid TodoDTO todo)
            throws ResourceNotFoundException, CommonServiceException {
        log.debug("Add new todo for lead id: [{}].", leadId);

        Lead lead = findLeadById(leadId);
        List<Todo> todos = lead.getTodos();

        // Validate schedule
        validateTodoScheduled(todos, todo, leadId);

        todo.setComponentId(leadId);
        final TodoDTO savedTODO = todoService.create(todo);

        log.debug("Lead todo has been created for lead id: [{}]. Todo id [{}]", leadId, todo.getId());

        todos.add(todoService.getMapper().getMapperIn().getDestination(savedTODO));
        lead.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
        repository.save(lead);

        return savedTODO;
    }

    @Override
    public <T extends TodoDTO> List<TodoDTO> addTODOList(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @Valid List<T> todoList) throws ResourceNotFoundException, CommonServiceException {
        log.debug("Add a list of Todos for lead id: [{}]. List size [{}]", leadId, todoList.size());

        Lead lead = findLeadById(leadId);
        List<Todo> todos = lead.getTodos();

        // TODO add signature to create multiple todos in one single insert
        List<TodoDTO> newTodoList = todoList.stream().map(t -> {
            validateTodoScheduled(todos, t, leadId);
            t.setComponentId(leadId);
            TodoDTO savedTODO = todoService.create(t);
            log.debug("Lead todo has been created for lead id: [{}]. Todo id [{}]", leadId, savedTODO.getId());
            return savedTODO;
        }).collect(Collectors.toList());

        todos.addAll(newTodoList
                        .stream()
                        .map(todoService.getMapper().getMapperIn()::getDestination)
                .collect(Collectors.toList()));
        lead.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
        repository.save(lead);

        return newTodoList;
    }

    @Override
    public TodoDTO removeTODO(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String todoId)
            throws ResourceNotFoundException {
        log.debug("Remove todo from lead id: [{}].", leadId);

        Lead lead = findLeadById(leadId);
        return lead.getTodos()
                .stream()
                .filter(i -> i.getId().equals(todoId))
                .findAny()
                .map( i -> {
                    lead.getTodos().remove(i);
                    lead.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
                    repository.save(lead);
                    log.debug("Todo with id [{}] for lead id [{}] has been deleted.", todoId, leadId);
                    return todoService.delete(i.getId());
                })
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.TODO_DOCUMENT, todoId}));
    }

    @Override
    public List<TodoAssigneeDTO> getTODOs(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId)
            throws ResourceNotFoundException {
        log.debug("Get todos from lead id: [{}].", leadId);

        Lead lead = findLeadById(leadId);
        List<Todo> todos = lead.getTodos();
        List<TodoAssigneeDTO> assigneeTODOs = Collections.emptyList();
        if (!todos.isEmpty()) {
            final List<UserCommon> users = helper.getUsers(leadId);
            assigneeTODOs = todos
                    .stream()
                    .map(i -> mapTODO(i, users))
                    .collect(Collectors.toList());
        }
        log.debug("[{}] todos has been found for lead id [{}]", leadId, todos.size());

        return  assigneeTODOs;
    }

    @Override
    public TodoAssigneeDTO getTODO(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String todoId)
            throws ResourceNotFoundException {
        log.debug("Get todo id [{}] from lead id: [{}].", todoId, leadId);
        Lead lead = findLeadById(leadId);
        return lead
                .getTodos()
                .stream()
                .filter(i -> todoId.equals(i.getId()))
                .findAny()
                .map(i -> {
                    log.debug("Todo id [{}] for lead id [{}] has been found. \nTodo: [{}]", todoId, leadId, i);
                    List<UserCommon> users = helper.getUsers(leadId);
                    return mapTODO(i, users);
                })
                .orElseThrow( () ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.LEAD_DOCUMENT, todoId}) );
    }

    @Override
    public List<LeadTodoDTO> findBy(List<String> leadIds, List<Integer> status, List<Integer> types) throws CommonServiceException {
        log.debug("Get todos from leads [{}] with status [{}] and types[{}]", leadIds, status, types);

        List<TodoDTO> todos = todoService.findBy(leadIds, status, types);
        if (!todos.isEmpty()) {
            List<Lead> leads = leadIds.isEmpty() ? Collections.emptyList() : repository.findAllByIdIn(leadIds);

            // throw exception if leads.size < leadIds.size

            return todos
                    .stream()
                    .map(t -> {
                        LeadTodoDTO todo = mapper.getTodoMapperOut().getDestination(t);
                        Optional<Lead> lead = leads.stream().filter(l -> l.getId().equals(todo.getComponentId())).findAny();
                        lead.ifPresent(value -> todo.setLead(mapper.getLeadTodoMapperOut().getDestination(value)));
                        return todo;
                    }).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    private Lead findLeadById(String leadId) throws ResourceNotFoundException {
        return repository
                .findById(leadId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.LEAD_DOCUMENT, leadId }));
    }

    private TodoAssigneeDTO mapTODO(Todo t, List<UserCommon> users) {
        TodoAssigneeDTO todo = todoService.getMapper().getAssigneeMapperOut().getDestination(t);
        Optional<UserCommon> user = users.stream().filter(u -> u.getId().equals(todo.getAssigneeId())).findAny();
        user.ifPresent(todo::setAssignee);
        return todo;
    }

    private void validateTodoScheduled(List<Todo> currentTodos, TodoDTO newTodo, String leadId) {
        Predicate<Todo> filter  = t -> t.getScheduled().equals(newTodo.getScheduled())
                && (newTodo.getSource() == null || newTodo.getSource().getType().equals(TodoSourceType.MANUAL));
        currentTodos.stream()
                .filter(filter)
                .findAny()
                .ifPresent(t -> {
                    throw new CommonServiceException(
                            ExceptionMessageConstants.LEAD_TODO_ADD_LEAD_SLOT_ALREADY_SCHEDULED_EXCEPTION,
                            new String[]{leadId, newTodo.getScheduled().toString()});
                });
    }
}
