package com.sawoo.pipeline.api.repository.lead;

import com.sawoo.pipeline.api.model.interaction.Interaction;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.repository.listener.InteractionCascadeOperationDelegator;
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

    @Value("${app.mongo.lead-interaction.cascading:false}")
    private boolean leadInteractionCascading;

    private final PersonCascadeOperationDelegator personCascadeOperationDelegator;
    private final InteractionCascadeOperationDelegator interactionCascadeOperationDelegator;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Lead> event) {
        Lead lead = event.getSource();
        personCascadeOperationDelegator.onSave(lead.getPerson(), lead::setPerson);
        if (leadInteractionCascading) {
            List<Interaction> interactions = Arrays.asList(lead.getInteractions().toArray(new Interaction[0]));
            lead.getInteractions().clear();
            interactions.forEach(i -> interactionCascadeOperationDelegator.onSave(i, lead.getInteractions()::add));
        }
        super.onBeforeConvert(event);
    }
}
