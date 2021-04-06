package com.sawoo.pipeline.api.repository.lead;

import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.repository.listener.TodoCascadeOperationDelegator;
import com.sawoo.pipeline.api.repository.listener.PersonCascadeOperationDelegator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LeadEventListener extends AbstractMongoEventListener<Lead> {

    @Value("${app.mongo.lead-todo.cascading:false}")
    private boolean leadTodoCascading;

    private final PersonCascadeOperationDelegator personCascadeOperationDelegator;
    private final TodoCascadeOperationDelegator todoCascadeOperationDelegator;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Lead> event) {
        Lead lead = event.getSource();
        personCascadeOperationDelegator.onSave(lead.getPerson(), lead::setPerson);
        if (leadTodoCascading) {
            List<Todo> todos = Arrays.asList(lead.getTodos().toArray(new Todo[0]));
            lead.getTodos().clear();
            todos.forEach(i -> todoCascadeOperationDelegator.onSave(i, lead.getTodos()::add));
        }
        super.onBeforeConvert(event);
    }
}
