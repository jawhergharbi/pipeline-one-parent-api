package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.prospect.LeadInteractionDTO;
import com.sawoo.pipeline.api.dto.prospect.LeadInteractionRequestDTO;

import java.util.List;
import java.util.Optional;

public interface LeadInteractionService {

    LeadInteractionDTO create(Long leadId, LeadInteractionRequestDTO interaction) throws ResourceNotFoundException;

    LeadInteractionDTO findById(Long leadId, Long id) throws ResourceNotFoundException;

    List<LeadInteractionDTO> findAll(Long leadId);

    Optional<LeadInteractionDTO> delete(Long leadId, Long id);

    Optional<LeadInteractionDTO> update(Long leadId, Long id, LeadInteractionRequestDTO lead);
}
