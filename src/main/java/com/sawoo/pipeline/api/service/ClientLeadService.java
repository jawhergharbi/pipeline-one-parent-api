package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.lead.LeadBasicDTO;
import com.sawoo.pipeline.api.dto.lead.LeadMainDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ClientLeadService {

    LeadBasicDTO create(Long clientId, LeadBasicDTO lead) throws CommonServiceException;

    LeadBasicDTO add(Long clientId, Long leadId) throws ResourceNotFoundException;

    LeadBasicDTO remove(Long clientId, Long leadId) throws ResourceNotFoundException;

    List<LeadBasicDTO> findAll(Long clientId) throws ResourceNotFoundException;

    List<LeadMainDTO> findAllMain(LocalDateTime datetime);

    List<LeadMainDTO> findClientsMain(List<Long> clientIds, LocalDateTime datetime);
}
