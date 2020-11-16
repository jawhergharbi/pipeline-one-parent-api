package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.contants.DomainConstants;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.StatusDTO;
import com.sawoo.pipeline.api.dto.prospect.LeadDTOOld;
import com.sawoo.pipeline.api.dto.lead.LeadTypeRequestParam;
import com.sawoo.pipeline.api.model.prospect.LeadOld;
import com.sawoo.pipeline.api.model.lead.LeadStatusList;
import com.sawoo.pipeline.api.repository.LeadRepositoryOld;
import com.sawoo.pipeline.api.service.company.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class LeadServiceUtils {

    private final LeadRepositoryOld repository;
    private final CompanyService companyService;

    public void preProcessLead(LeadDTOOld lead, LocalDateTime datetime, int type) throws CommonServiceException {
        repository
                .findByLinkedInUrl(lead.getLinkedInUrl())
                .ifPresent((leadItem) -> {
                    throw new CommonServiceException(
                            ExceptionMessageConstants.COMMON_CREATE_ENTITY_ALREADY_EXISTS_EXCEPTION,
                            new String[]{"Prospect", leadItem.getLinkedInUrl()});
                });

        // Process company info
        processCompanyData(lead, datetime);

        // Process prospect status
        if (lead.getStatus() == null) {
            lead.setStatus(StatusDTO
                    .builder()
                    .value( LeadTypeRequestParam.PROSPECT.getType() == type ?
                            LeadStatusList.FUNNEL_ON_GOING.getStatus() :
                            LeadStatusList.HOT.getStatus() )
                    .updated(datetime)
                    .build());
        }

        // Prospect salutation
        if (lead.getSalutation() == null) {
            lead.setSalutation(DomainConstants.SALUTATION_EMPTY);
        }

        // Updated and Created datetime
        lead.setUpdated(datetime);
        lead.setCreated(datetime);
    }

    public Optional<LeadOld> findById(Long leadId) {
        return repository.findById(leadId);
    }

    private void processCompanyData(LeadDTOOld lead, LocalDateTime datetime) {
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
