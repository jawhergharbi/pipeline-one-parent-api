package com.sawoo.pipeline.api.repository.listener;

import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.repository.todo.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class TodoCascadeOperationDelegator implements CascadeOperationDelegation<Todo> {

    private final TodoRepository repository;

    @Override
    public void onSave(Todo child, Consumer<Todo> parentFunction) {
        if (child != null) {
            if (child.getId() == null) {
                Todo todo = repository.insert(child);
                parentFunction.accept(todo);
            } else {
                Optional<Todo> todo = repository.findById(child.getId());
                todo.ifPresent(parentFunction);
            }
        }
    }

    @Override
    public void onDelete(Todo child) {
        // nothing to do atm
    }
}
