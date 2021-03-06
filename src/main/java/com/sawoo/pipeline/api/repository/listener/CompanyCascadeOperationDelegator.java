package com.sawoo.pipeline.api.repository.listener;

import com.sawoo.pipeline.api.model.company.Company;
import com.sawoo.pipeline.api.repository.company.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class CompanyCascadeOperationDelegator implements CascadeOperationDelegation<Company> {

    private final CompanyRepository companyRepository;

    @Override
    public void onSave(Company child, Consumer<Company> parentFunction) {
        if (child != null) {
            if (child.getId() == null) {
                companyRepository
                        .findByName(child.getName())
                        .ifPresentOrElse(parentFunction,
                                () -> {
                                    LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
                                    child.setCreated(now);
                                    child.setUpdated(now);
                                    companyRepository.insert(child);
                                });
            } else {
                LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
                child.setUpdated(now);
                companyRepository.save(child);
            }
        }
    }

    @Override
    public void onDelete(Company child) {
        // nothing to do atm
    }
}
