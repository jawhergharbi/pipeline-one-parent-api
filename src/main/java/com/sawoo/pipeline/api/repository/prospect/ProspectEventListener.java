package com.sawoo.pipeline.api.repository.prospect;

import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.repository.listener.CompanyCascadeOperationDelegator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProspectEventListener extends AbstractMongoEventListener<Prospect> {

    private final CompanyCascadeOperationDelegator companyCascadeDelegator;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Prospect> event) {
        Prospect prospect = event.getSource();
        companyCascadeDelegator.onSave(prospect.getCompany(), prospect::setCompany);
        super.onBeforeConvert(event);
    }
}
