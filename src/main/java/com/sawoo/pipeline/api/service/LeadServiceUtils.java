package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.contants.DomainConstants;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.StatusDTO;
import com.sawoo.pipeline.api.dto.lead.LeadBasicDTO;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class LeadServiceUtils {

    private final LeadRepository repository;
    private final CompanyService companyService;

    public void preProcessLead(LeadBasicDTO lead, LocalDateTime datetime) throws CommonServiceException {
        repository
                .findByFullName(lead.getFullName())
                .ifPresent((leadItem) -> {
                    throw new CommonServiceException(
                            ExceptionMessageConstants.COMMON_CREATE_ENTITY_ALREADY_EXISTS_EXCEPTION,
                            new String[]{"Lead", leadItem.getFullName()});
                });

        // Process company info
        processCompanyData(lead, datetime);

        // Process lead status
        if (lead.getStatus() == null) {
            lead.setStatus(StatusDTO
                    .builder()
                    .value(DomainConstants.LeadStatus.WARM.ordinal())
                    .updated(datetime)
                    .build());
        }

        // Updated and Created datetime
        lead.setUpdated(datetime);
        lead.setCreated(datetime);
    }

    public Optional<Lead> findById(Long leadId) {
        return repository.findById(leadId);
    }

    private void processCompanyData(LeadBasicDTO lead, LocalDateTime datetime) {
        // Create company entry
        if (lead.getCompany() != null) {
            companyService
                    .findByName(lead.getCompany().getName())
                    .ifPresentOrElse(lead::setCompany, () -> {
                        lead.getCompany().setUpdated(datetime);
                        lead.getCompany().setCreated(datetime);

                        log.debug("Company found. Lead [{}] is going ot be assign to company name [{}]",
                                lead.getFullName(),
                                lead.getCompany().getName());
                    });
        }
        // Company comments
        if (lead.getCompanyComments() != null) {
            if (lead.getCompany() == null) {
                log.warn("Company comment are being added when there is no company defined for lead [{}]", lead.getFullName());
            }
            if (lead.getCompanyComments().getUpdated() == null) {
                lead.getCompanyComments().setUpdated(datetime);
            }
        }
    }
}
