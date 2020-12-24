package com.sawoo.pipeline.api.service.lead;


import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.repository.lead.LeadRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
import com.sawoo.pipeline.api.service.common.CommonDiscAnalysisData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Slf4j
@Service
@Validated
public class LeadServiceImpl extends BaseServiceImpl<LeadDTO, Lead, LeadRepository, LeadMapper> implements LeadService {

    private final LeadReportService reportService;

    @Autowired
    public LeadServiceImpl(LeadRepository repository, LeadMapper mapper, CommonDiscAnalysisData discAnalysisData) {
        super(repository, mapper, DBConstants.LEAD_DOCUMENT);
        this.reportService = new LeadReportServiceDecorator(getRepository(), discAnalysisData);
    }

    @Override
    public Optional<Lead> entityExists(LeadDTO entityToCreate) {
        log.debug(
                "Checking entity existence. [type: {}, id: {}]",
                DBConstants.LEAD_DOCUMENT,
                entityToCreate.getId());
        return getRepository().findById(entityToCreate.getId());
    }

    @Override
    public byte[] getReport(String id, String type, String lan) throws CommonServiceException, ResourceNotFoundException {
        return reportService.getReport(id, type, lan);
    }
}
