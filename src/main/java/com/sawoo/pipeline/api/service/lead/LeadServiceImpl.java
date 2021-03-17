package com.sawoo.pipeline.api.service.lead;


import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.interaction.InteractionAssigneeDTO;
import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.repository.lead.LeadRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
import com.sawoo.pipeline.api.service.infra.audit.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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
                           LeadInteractionService interactionService,
                           AuditService audit) {
        super(repository, mapper, DBConstants.LEAD_DOCUMENT, audit);
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
    public List<InteractionAssigneeDTO> getInteractions(String leadId) throws ResourceNotFoundException {
        return interactionService.getInteractions(leadId);
    }

    @Override
    public InteractionAssigneeDTO getInteraction(String leadId, String interactionId) throws ResourceNotFoundException {
        return interactionService.getInteraction(leadId, interactionId);
    }

    @Override
    public List<LeadInteractionDTO> findBy(List<String> leadIds, List<Integer> status, List<Integer> types) throws CommonServiceException {
        return interactionService.findBy(leadIds, status, types);
    }

    @Override
    public LeadDTO deleteLeadSummary(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String leadId)
            throws ResourceNotFoundException {
        log.debug("Delete lead summary for lead id [{}]", leadId);
        Consumer<Lead> setNull = l -> l.setLeadNotes(null);
        return deleteLeadNotes(leadId, setNull);
    }

    @Override
    public LeadDTO deleteLeadQualificationComments(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String leadId)
            throws ResourceNotFoundException {
        log.debug("Delete qualification comments for lead id [{}]", leadId);
        Consumer<Lead> setNull = l -> {
            if (l.getStatus() != null) {
                l.getStatus().setNotes(null);
            }
        };
        return deleteLeadNotes(leadId, setNull);
    }

    @Override
    public LeadDTO deleteLeadCompanyComments(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR) String leadId)
            throws ResourceNotFoundException {
        log.debug("Delete lead company comments for lead id [{}]", leadId);
        Consumer<Lead> setNull = l -> l.setCompanyNotes(null);
        return deleteLeadNotes(leadId, setNull);
    }

    private Lead findLeadById(String leadId) throws ResourceNotFoundException {
        return getRepository()
                .findById(leadId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.LEAD_DOCUMENT, leadId }));
    }

    private LeadDTO deleteLeadNotes(String leadId, Consumer<Lead> setNull) {
        Lead lead = findLeadById(leadId);
        setNull.accept(lead);
        lead.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
        getRepository().save(lead);
        log.debug("Lead with id [{}] has been correctly updated", leadId);
        return getMapper().getMapperOut().getDestination(lead);
    }
}
