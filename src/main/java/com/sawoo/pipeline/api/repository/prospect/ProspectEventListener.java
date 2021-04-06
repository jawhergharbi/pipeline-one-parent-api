package com.sawoo.pipeline.api.repository.prospect;

import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.repository.listener.PersonCascadeOperationDelegator;
import com.sawoo.pipeline.api.repository.listener.TodoCascadeOperationDelegator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProspectEventListener extends AbstractMongoEventListener<Prospect> {

    @Value("${app.mongo.prospect-todo.cascading:false}")
    private boolean prospectTodoCascading;

    private final PersonCascadeOperationDelegator personCascadeOperationDelegator;
    private final TodoCascadeOperationDelegator todoCascadeOperationDelegator;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Prospect> event) {
        Prospect prospect = event.getSource();
        personCascadeOperationDelegator.onSave(prospect.getPerson(), prospect::setPerson);
        if (prospectTodoCascading) {
            List<Todo> todos = Arrays.asList(prospect.getTodos().toArray(new Todo[0]));
            prospect.getTodos().clear();
            todos.forEach(i -> todoCascadeOperationDelegator.onSave(i, prospect.getTodos()::add));
        }
        super.onBeforeConvert(event);
    }
}
