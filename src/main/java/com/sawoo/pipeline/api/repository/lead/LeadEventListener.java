package com.sawoo.pipeline.api.repository.lead;

import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.repository.listener.ProspectCascadeOperationDelegator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LeadEventListener extends AbstractMongoEventListener<Lead> {

    private final ProspectCascadeOperationDelegator prospectCascadeDelegator;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Lead> event) {
        Lead lead = event.getSource();
        prospectCascadeDelegator.onSave(lead.getProspect(), lead::setProspect);
        super.onBeforeConvert(event);
    }
}
