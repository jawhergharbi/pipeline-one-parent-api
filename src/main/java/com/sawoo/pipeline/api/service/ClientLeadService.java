package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.prospect.LeadDTOOld;
import com.sawoo.pipeline.api.dto.prospect.LeadMainDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ClientLeadService {

    LeadDTOOld create(Long clientId, LeadDTOOld lead, int type) throws CommonServiceException;

    LeadDTOOld add(Long clientId, Long leadId) throws ResourceNotFoundException;

    LeadDTOOld remove(Long clientId, Long leadId) throws ResourceNotFoundException;

    List<LeadDTOOld> findAll(Long clientId) throws ResourceNotFoundException;

    List<LeadMainDTO> findAllLeadsMain(LocalDateTime datetime);

    List<LeadMainDTO> findLeadsMain(List<Long> clientIds, Integer statusMin, Integer statusMax, LocalDateTime datetime);
}
