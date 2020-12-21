package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.prospect.LeadInteractionDTOOld;
import com.sawoo.pipeline.api.dto.prospect.LeadInteractionRequestDTO;

import java.util.List;
import java.util.Optional;

public interface LeadInteractionService {

    LeadInteractionDTOOld create(Long leadId, LeadInteractionRequestDTO interaction) throws ResourceNotFoundException;

    LeadInteractionDTOOld findById(Long leadId, Long id) throws ResourceNotFoundException;

    List<LeadInteractionDTOOld> findAll(Long leadId);

    Optional<LeadInteractionDTOOld> delete(Long leadId, Long id);

    Optional<LeadInteractionDTOOld> update(Long leadId, Long id, LeadInteractionRequestDTO lead);
}
