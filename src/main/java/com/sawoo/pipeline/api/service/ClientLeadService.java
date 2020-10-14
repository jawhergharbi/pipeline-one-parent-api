package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.lead.LeadMainDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ClientLeadService {

    LeadDTO create(Long clientId, LeadDTO lead) throws CommonServiceException;

    LeadDTO add(Long clientId, Long leadId) throws ResourceNotFoundException;

    LeadDTO remove(Long clientId, Long leadId) throws ResourceNotFoundException;

    List<LeadDTO> findAll(Long clientId) throws ResourceNotFoundException;

    List<LeadMainDTO> findAllLeadsMain(LocalDateTime datetime);

    List<LeadMainDTO> findLeadsMain(List<Long> clientIds, Integer statusMin, Integer statusMax, LocalDateTime datetime);
}
