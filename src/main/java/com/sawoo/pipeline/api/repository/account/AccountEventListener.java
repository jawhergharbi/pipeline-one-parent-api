package com.sawoo.pipeline.api.repository.account;

import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.repository.listener.CompanyCascadeOperationDelegator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountEventListener extends AbstractMongoEventListener<Account> {

    private final CompanyCascadeOperationDelegator companyCascadeDelegator;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Account> event) {
            Account account = event.getSource();
        companyCascadeDelegator.onSave(account.getCompany(), account::setCompany);
        super.onBeforeConvert(event);
    }
}
