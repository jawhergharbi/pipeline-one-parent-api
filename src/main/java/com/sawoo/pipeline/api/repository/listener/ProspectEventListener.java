package com.sawoo.pipeline.api.repository.listener;

import com.sawoo.pipeline.api.model.CompanyMongoDB;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class ProspectEventListener extends AbstractMongoEventListener<Prospect> {

    private final CompanyRepository companyRepository;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Prospect> event) {
        Prospect prospect = event.getSource();
        CompanyMongoDB company = prospect.getCompany();
        if (company != null && company.getId() == null) {
            companyRepository
                    .findByName(company.getName())
                    .ifPresentOrElse(
                            prospect::setCompany,
                            () -> {
                                LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
                                company.setCreated(now);
                                company.setUpdated(now);
                                companyRepository.insert(company);
                            });
        }
        super.onBeforeConvert(event);
    }
}
