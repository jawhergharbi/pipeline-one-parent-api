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
import com.sawoo.pipeline.api.repository.lead.LeadRepository;
import com.sawoo.pipeline.api.service.todo.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeadInteractionServiceDecorator implements LeadInteractionService {

    private final TodoService todoService;
    private final LeadRepository repository;
    private final LeadServiceDecoratorHelper helper;
    private final LeadMapper mapper;

    @Override
    public TodoDTO addInteraction(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @Valid TodoDTO interaction)
            throws ResourceNotFoundException, CommonServiceException {
        log.debug("Add new interaction for lead id: [{}].", leadId);

        Lead lead = findLeadById(leadId);
        List<Todo> todos = lead.getTodos();
        todos.stream()
                .filter(i -> i.getScheduled().equals(interaction.getScheduled()))
                .findAny()
                .ifPresent( (i) -> {
                    throw new CommonServiceException(
                            ExceptionMessageConstants.LEAD_INTERACTION_ADD_LEAD_SLOT_ALREADY_SCHEDULED_EXCEPTION,
                            new String[]{leadId, interaction.getScheduled().toString()});
                });

        interaction.setComponentId(leadId);
        final TodoDTO savedInteraction = todoService.create(interaction);

        log.debug("Lead interaction has been created for lead id: [{}]. Todo id [{}]", leadId, interaction.getId());

        todos.add(todoService.getMapper().getMapperIn().getDestination(savedInteraction));
        lead.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
        repository.save(lead);

        return savedInteraction;
    }

    @Override
    public TodoDTO removeInteraction(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String interactionId)
            throws ResourceNotFoundException {
        log.debug("Remove interaction from lead id: [{}].", leadId);

        Lead lead = findLeadById(leadId);
        return lead.getTodos()
                .stream()
                .filter(i -> i.getId().equals(interactionId))
                .findAny()
                .map( i -> {
                    lead.getTodos().remove(i);
                    lead.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
                    repository.save(lead);
                    log.debug("Todo with id [{}] for lead id [{}] has been deleted.", interactionId, leadId);
                    return todoService.delete(i.getId());
                })
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.TODO_DOCUMENT, interactionId }));
    }

    @Override
    public List<TodoAssigneeDTO> getInteractions(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId)
            throws ResourceNotFoundException {
        log.debug("Get todos from lead id: [{}].", leadId);

        Lead lead = findLeadById(leadId);
        List<Todo> todos = lead.getTodos();
        List<TodoAssigneeDTO> assigneeInteractions = Collections.emptyList();
        if (!todos.isEmpty()) {
            final List<UserCommon> users = helper.getUsers(leadId);
            assigneeInteractions = todos
                    .stream()
                    .map(i -> mapInteraction(i, users))
                    .collect(Collectors.toList());
        }
        log.debug("[{}] todos has been found for lead id [{}]", leadId, todos.size());

        return  assigneeInteractions;
    }

    @Override
    public TodoAssigneeDTO getInteraction(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String interactionId)
            throws ResourceNotFoundException {
        log.debug("Get interaction id [{}] from lead id: [{}].", interactionId, leadId);
        Lead lead = findLeadById(leadId);
        return lead
                .getTodos()
                .stream()
                .filter(i -> interactionId.equals(i.getId()))
                .findAny()
                .map(i -> {
                    log.debug("Todo id [{}] for lead id [{}] has been found. \nTodo: [{}]", interactionId, leadId, i);
                    List<UserCommon> users = helper.getUsers(leadId);
                    return mapInteraction(i, users);
                })
                .orElseThrow( () ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.LEAD_DOCUMENT, interactionId }) );
    }

    @Override
    public List<LeadTodoDTO> findBy(List<String> leadIds, List<Integer> status, List<Integer> types) throws CommonServiceException {
        log.debug("Get todos from leads [{}] with status [{}] and types[{}]", leadIds, status, types);

        List<TodoDTO> interactions = todoService.findBy(leadIds, status, types);
        if (interactions.size() > 0) {
            List<Lead> leads = leadIds.size() > 0 ? repository.findAllByIdIn(leadIds) : Collections.emptyList();

            // throw exception if leads.size < leadIds.size

            return interactions
                    .stream()
                    .map((i) -> {
                        LeadTodoDTO interaction = mapper.getInteractionMapperOut().getDestination(i);
                        Optional<Lead> lead = leads.stream().filter(l -> l.getId().equals(interaction.getComponentId())).findAny();
                        lead.ifPresent(value -> interaction.setLead(mapper.getLeadInteractionMapperOut().getDestination(value)));
                        return interaction;
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

    private TodoAssigneeDTO mapInteraction(Todo i, List<UserCommon> users) {
        TodoAssigneeDTO interaction = todoService.getMapper().getAssigneeMapperOut().getDestination(i);
        Optional<UserCommon> user = users.stream().filter(u -> u.getId().equals(interaction.getAssigneeId())).findAny();
        user.ifPresent(interaction::setAssignee);
        return interaction;
    }
}
