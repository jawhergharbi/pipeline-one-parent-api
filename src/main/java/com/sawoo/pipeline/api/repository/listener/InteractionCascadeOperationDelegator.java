package com.sawoo.pipeline.api.repository.listener;

import com.sawoo.pipeline.api.model.interaction.Interaction;
import com.sawoo.pipeline.api.repository.interaction.InteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class InteractionCascadeOperationDelegator implements CascadeOperationDelegation<Interaction> {

    private final InteractionRepository repository;

    @Override
    public void onSave(Interaction child, Consumer<Interaction> parentFunction) {
        if (child != null) {
            if (child.getId() == null) {
                Interaction interaction = repository.insert(child);
                parentFunction.accept(interaction);
            } else {
                Optional<Interaction> interaction = repository.findById(child.getId());
                interaction.ifPresent(parentFunction);
            }
        }
    }
}
