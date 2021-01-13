package com.sawoo.pipeline.api.service.lead;


import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.repository.lead.LeadRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Validated
@Primary
public class LeadServiceImpl extends BaseServiceImpl<LeadDTO, Lead, LeadRepository, LeadMapper> implements LeadService {

    private final LeadReportService reportService;
    private final LeadInteractionService interactionService;

    @Autowired
    public LeadServiceImpl(LeadRepository repository, LeadMapper mapper,
                           LeadReportService reportService,
                           LeadInteractionService interactionService) {
        super(repository, mapper, DBConstants.LEAD_DOCUMENT);
        this.reportService = reportService;
        this.interactionService = interactionService;
    }

    @Override
    public Optional<Lead> entityExists(LeadDTO entityToCreate) {
        String entityId = entityToCreate.getId();
        log.debug(
                "Checking entity existence. [type: {}, id: {}]",
                DBConstants.LEAD_DOCUMENT,
                entityId);
        return entityId == null ? Optional.empty() : getRepository().findById(entityToCreate.getId());
    }

    @Override
    public byte[] getReport(String id, String type, String lan) throws CommonServiceException, ResourceNotFoundException {
        return reportService.getReport(id, type, lan);
    }

    @Override
    public InteractionDTO addInteraction(String leadId, InteractionDTO interaction) throws ResourceNotFoundException, CommonServiceException {
        return interactionService.addInteraction(leadId, interaction);
    }

    @Override
    public InteractionDTO removeInteraction(String leadId, String interactionId) throws ResourceNotFoundException {
        return interactionService.removeInteraction(leadId, interactionId);
    }

    @Override
    public List<InteractionDTO> getInteractions(String leadId) throws ResourceNotFoundException {
        return interactionService.getInteractions(leadId);
    }

    @Override
    public InteractionDTO getInteraction(String leadId, String interactionId) throws ResourceNotFoundException {
        return interactionService.getInteraction(leadId, interactionId);
    }

    @Override
    public List<LeadInteractionDTO> findBy(List<String> leadIds, List<Integer> status, List<Integer> types) throws CommonServiceException {
        return interactionService.findBy(leadIds, status, types);
    }
}
